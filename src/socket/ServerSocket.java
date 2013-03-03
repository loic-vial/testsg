package socket;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Gestionnaire de socket serveur.
 * Cette classe automatise la creation d'un serveur et la connexion avec des 
 * clients.
 */
public class ServerSocket
{
	private java.net.ServerSocket ssock;
	
	private ArrayList<ServerSocketListener> listeners;
	
	private boolean stop;
	
	private InternalThread thread;
	
	/**
	 * Constructeur.
	 * Note : Le serveur est demarre automatiquement a la fin du constructeur.
	 * @param port : Le port d'ecoute.
	 */
	public ServerSocket(int port) throws IOException
	{
		ssock = new java.net.ServerSocket(port);
		listeners = new ArrayList<ServerSocketListener>();
		stop = false;
		thread = new InternalThread();
		thread.start();
	}
	
	/**
	 * Ajoute un ecouteur de la socket.
	 */
	public void addServerSocketListener(ServerSocketListener listener)
	{
		if (listener == null) throw new NullPointerException();
		listeners.add(listener);
	}
	
	/**
	 * Arrete le serveur.
	 */
	public synchronized void stop()
	{
		if (!stop)
		{
			stop = true;
			try {
				ssock.close();
			} catch (IOException e) {}
			for (ServerSocketListener nl : listeners)
				new SocketClosedThread(nl).start();
		}
	}
	
	/**
	 * @return le port d'ecoute.
	 */
	public int getPort()
	{
		return ssock.getLocalPort();
	}
	
	/**
	 * Classe interne permettant la gestion interne du thread du serveur.
	 */
	private class InternalThread extends Thread
	{
		public InternalThread()
		{
			super("ServerSocket");
		}
		
		public void run()
		{
			ClientSocket client = null;
			while (!stop)
			{
				try
				{
					client = new ClientSocket(ssock.accept());
					for (ServerSocketListener nl : listeners)
						new ClientConnectedThread(nl, client).start();
				}
				// socket serveur fermee (seulement ?)
				catch (SocketException e)
				{
					ServerSocket.this.stop();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private class SocketClosedThread extends Thread
	{
		private ServerSocketListener listener;

		public SocketClosedThread(ServerSocketListener listener)
		{
			this.listener = listener;
		}
		
		public void run()
		{
			listener.socketClosed(ServerSocket.this);
		}
	}
	
	private class ClientConnectedThread extends Thread
	{
		private ServerSocketListener listener;
		
		private ClientSocket client;

		public ClientConnectedThread(ServerSocketListener listener, ClientSocket client)
		{
			this.listener = listener;
			this.client   = client;
		}
		
		public void run()
		{
			listener.clientConnected(client);
		}
	}
	
}

