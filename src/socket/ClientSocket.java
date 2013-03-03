package socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientSocket
{
	private Socket csock;
	
	private ArrayList<ClientSocketListener> listeners;
	
	private ObjectOutputStream oos;
	
	private ObjectInputStream ois;
	
	private Object owner;

	private boolean stop;
	
	private InternalThread thread;
	
	private ArrayList<Request> requests;
	
	private HashMap<Integer, Answer> answers;
	
	private static class Request implements Serializable
	{
		private static final long serialVersionUID = 42;
		public Request(int id, String method, Object[] args) {
			this.id = id;
			this.method = method;
			this.args = args;
		}
		public int id;
		public String method;
		public Object[] args;
	}
	
	private static class Answer implements Serializable
	{
		private static final long serialVersionUID = 42;
		public Answer(int id) {
			this.id = id;
			this.ans = null;
			errorFlag = true;
		}
		public Answer(int id, Object ans) {
			this.id = id;
			this.ans = ans;
			errorFlag = false;
		}
		public int id;
		public Object ans;
		public boolean errorFlag;
	}

	/**
	 * Constructeur.
	 * @param host : l'adresse ip du serveur sur lequel se connecter
	 * @param port : le port du serveur sur lequel se connecter
	 */
	public ClientSocket(String host, int port) throws UnknownHostException, IOException
	{
		this(new Socket(host, port));
	}

	/**
	 * Constructeur.
	 * @param socket : la socket support vers le client.
	 */
	public ClientSocket(Socket socket)
	{
		if (socket == null) throw new NullPointerException();
		csock = socket;
		listeners = new ArrayList<ClientSocketListener>();
		try
		{
			oos = new ObjectOutputStream(csock.getOutputStream());
			ois = new ObjectInputStream(csock.getInputStream());
		} 
		catch (SocketException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		owner = null;
		stop = false;
		requests = new ArrayList<Request>();
		answers = new HashMap<Integer, Answer>();
		thread = new InternalThread();
		thread.start();
	}
	
	public void setOwner(Object owner)
	{
		this.owner = owner;
	}

	/**
	 * Ajoute un ecouteur reseau.
	 */
	public void addClientSocketListener(ClientSocketListener listener)
	{
		if (listener == null) throw new NullPointerException();
		listeners.add(listener);
	}
	
	/**
	 * Envoi une requete au client.
	 */
	public void send(Object obj)
	{
		try
		{
			oos.writeObject(obj);
			oos.flush();
			oos.reset();
		}
		// socket fermee (seulement ?)
		catch (SocketException e)
		{
			stop();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Envoi une requete au client.
	 */
	public Object sendRequest(String nomMethode, Object... args)
	{
		// *** Envoi de la requete
		Request req = new Request(requests.size(), nomMethode, args);
		requests.add(req);
		try
		{
			oos.writeObject(req);
			oos.flush();
			oos.reset();
		}
		// socket fermee (seulement ?)
		catch (SocketException e)
		{
			stop();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// *** Attente de la reponse
		synchronized (req) {
			try {
				req.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Answer ans = answers.get(req.id);
		if (ans.errorFlag) throw new RuntimeException("Impossible d'appeler " +
				"la methode \"" + nomMethode + "\" !");
		
		// *** Retour de la reponse
		return answers.get(req.id).ans;
	}

	/**
	 * Ferme la connexion avec le client.
	 */
	public synchronized void stop()
	{
		if (!stop)
		{
			stop = true;
			try {
				csock.close();
			} catch (IOException e) {}
			for (ClientSocketListener nl : listeners)
				new SocketClosedThread(nl).start();
		}
	}
	
	/**
	 * @return l'adresse IP distante, sous la forme "xxx.xxx.xxx.xxx"
	 */
	public String getRemoteIP()
	{
		return csock.getInetAddress().getHostAddress();
	}
	
	/**
	 * @return le port local sur lequel la socket est connectee
	 */
	public int getLocalPort()
	{
		return csock.getLocalPort();
	}
	
	/**
	 * @return le port distant sur lequel la socket est connectee
	 */
	public int getRemotePort()
	{
		return csock.getPort();
	}
	
	/**
	 * Gestionnaire du thread interne.
	 */
	private class InternalThread extends Thread
	{
		public InternalThread()
		{
			super("ClientSocket");
		}

		public void run()
		{
			Object obj = null;
			Request req = null;
			Answer ans = null;
			int id;
			String methodName = null;
			Object[] args = null;
			while (!stop)
			{
				try
				{
					// *** Reception d'un message
					obj = ois.readObject();
					
					// *** Cas d'une reponse a une requete
					if (obj instanceof Answer)
					{
						ans = (Answer) obj;
						id = ans.id;
						answers.put(id, ans);
						req = requests.get(id);
						synchronized (req) {
							req.notify();
						}
					}
					
					// *** Cas d'une requete a laquelle on va repondre
					else if (obj instanceof Request)
					{
						req = (Request) obj;
						id = req.id;
						methodName = req.method;
						args = req.args;
						ans = new Answer(id);
						for (Method met : owner.getClass().getMethods())
						{
							if (!met.getName().equals(methodName)) continue;
							try {
								ans = new Answer(id, met.invoke(owner, args));
								break;
							} catch (Exception e) {}
						}
						send(ans);
					}
					
					// *** Cas general : autre message
					else
					{
						for (ClientSocketListener nl : listeners)
							new RequestReceivedThread(nl, obj).start();
					}
				}
				// socket fermee
				catch (NullPointerException e)
				{
					ClientSocket.this.stop();
				}
				// socket fermee
				catch (EOFException e)
				{
					ClientSocket.this.stop();
				}
				// serveur fermee (seulement ?)
				catch (SocketException e)
				{
					ClientSocket.this.stop();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Thread gerant l'envoi d'un message aux listeners quand la socket est fermee
	 */
	private class SocketClosedThread extends Thread
	{
		private ClientSocketListener listener;

		public SocketClosedThread(ClientSocketListener listener)
		{
			this.listener = listener;
		}
		
		public void run()
		{
			listener.socketClosed(ClientSocket.this);
		}
	}
	
	/**
	 * Thread gerant l'envoi d'un message aux listener quand un message est arrive
	 * sur cette socket.
	 */
	private class RequestReceivedThread extends Thread
	{
		private ClientSocketListener listener;
		
		private Object message;
		
		public RequestReceivedThread(ClientSocketListener client, Object message)
		{
			this.message = message;
			this.listener = client;
		}
		
		public void run()
		{
			listener.requestReceived(message, ClientSocket.this);
		}
	}
}