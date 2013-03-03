package testsg;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import socket.ClientSocket;
import socket.ClientSocketListener;
import socket.ServerSocket;
import socket.ServerSocketListener;
import util.Data;

/**
 * Application TestsG (controleur)
 */
public class TestsG implements ActionListener, ServerSocketListener, ClientSocketListener, ChangeListener, ListSelectionListener
{
	/**
	 * Vue
	 */
	private VueTestsG m_vue;
	
	/**
	 * Socket serveur
	 */
	private ServerSocket ssocket;
	
	/**
	 * "le serveur est il demarre ?"
	 */
	private boolean started;
	
	/**
	 * "est on en train d'enregistrer des evenements ?"
	 */
	private boolean isRecording;
	
	/**
	 * "est on en train de rejouer des evenements ?"
	 */
	private boolean isReplaying;
	
	/**
	 * Liste des evenements du tableau
	 */
	private ArrayList<InputEvent> events;
	
	/**
	 * Modele du tableau des evenements
	 */
	private DefaultTableModel tableEvents;
	
	/**
	 * Modele de la liste des clients
	 */
	private DefaultListModel listClients;

	/**
	 * Classe interne representant un client
	 */
	private class Client
	{
		public ClientSocket socket;

		public Client(ClientSocket sock)
		{
			socket = sock;
		}
		
		public String toString()
		{
			return socket.getRemoteIP() + ":" + socket.getRemotePort();
		}
	}

	/**
	 * Constructeur
	 */
	public TestsG()
	{
		m_vue = new VueTestsG();
		
		m_vue.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				if (started) stopServer();
				System.exit(0);
			}
		});
		m_vue.getBtnDemarrer().addActionListener(this);
		m_vue.getBtnEnregistrer().addActionListener(this);
		m_vue.getBtnJouer().addActionListener(this);
		m_vue.getBtnDeconnecter().addActionListener(this);
		m_vue.getSliderVitesse().addChangeListener(this);
		m_vue.getBtnCharger().addActionListener(this);
		m_vue.getBtnSauvegarder().addActionListener(this);
		m_vue.getBtnEffacer().addActionListener(this);
		m_vue.getListClients().addListSelectionListener(this);
		
		m_vue.getBtnDeconnecter().setEnabled(false);
		m_vue.getBtnEnregistrer().setEnabled(false);
		m_vue.getBtnJouer().setEnabled(false);
		m_vue.getSliderVitesse().setEnabled(false);
		
		m_vue.setVisible(true);
		
		ssocket = null;
		started = false;
		isRecording = false;
		isReplaying = false;
		
		events = new ArrayList<InputEvent>();
		tableEvents = new DefaultTableModel(
				new Object[][]{}, 
				new String[] {"Ecart de temps (ms)", "Type", "Info"});
		m_vue.getTableEvents().setModel(tableEvents);
		
		listClients = new DefaultListModel();
		m_vue.getListClients().setModel(listClients);
		
		m_vue.pack();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		// clic sur le bouton "demarrer" : demarre/arrete le serveur
		if (e.getSource().equals(m_vue.getBtnDemarrer()))
		{
			if (!started) startServer();
			else stopServer();
		}
		
		// clic sur le bouton "deconnecter" : deconnecte les clients selectionnes
		if (e.getSource().equals(m_vue.getBtnDeconnecter()))
		{
			for (Client cl : getSelectedClients())
				cl.socket.stop();
		}
		
		// clic sur le bouton "enregistrer" : enregistre les events des clients selectionnes
		else if (e.getSource().equals(m_vue.getBtnEnregistrer()))
		{
			HashMap<String, Object> message = new HashMap<String, Object>();
			
			if (isRecording) message.put("record", false);
			else message.put("record", true);
				
			for (Client cl : getSelectedClients())
				cl.socket.send(message);
		}
		
		// clic sur le bouton "jouer" : rejoue les events sur les clients selectionnes
		else if (e.getSource().equals(m_vue.getBtnJouer()))
		{
			HashMap<String, Object> message = new HashMap<String, Object>();
			
			if (isReplaying) message.put("replay", null);
			else message.put("replay", events);
			
			for (Client cl : getSelectedClients())
				cl.socket.send(message);
		}
		
		// clic sur le bouton "effacer" : efface la liste d'events
		else if (e.getSource().equals(m_vue.getBtnEffacer()))
		{
			clearEventList();
		}
		
		// clic sur le bouton "charger" : charge une liste d'events
		else if (e.getSource().equals((m_vue.getBtnCharger())))
		{
			JFileChooser fc = new JFileChooser(".");
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileNameExtensionFilter("Liste d'evenements (*.data)", "data"));
			int ret = fc.showOpenDialog(m_vue);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				setEventList((ArrayList<InputEvent>) Data.load(f.getPath()));
			}
		}
		
		// clic sur le bouton "sauvegarder" : sauvegarde la liste d'events
		else if (e.getSource().equals((m_vue.getBtnSauvegarder())))
		{
			JFileChooser fc = new JFileChooser(".");
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileNameExtensionFilter("Liste d'evenements (*.data)", "data"));
			int ret = fc.showSaveDialog(m_vue);
			if (ret == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				Data.store(events, f.getPath());
			}
		}
		
		m_vue.pack();
	}

	public void clientConnected(ClientSocket client)
	{
		Client cl = new Client(client);
		
		// System.out.println("Nouveau client : " + cl);

		client.addClientSocketListener(this);
		listClients.addElement(cl);
		
		m_vue.getSliderVitesse().setEnabled(true);
		sendSliderValue();
		
		m_vue.pack();
	}

	public synchronized void requestReceived(Object obj, ClientSocket src)
	{
		// System.out.println("Recu : " + obj);

		if (!(obj instanceof HashMap)) return;
		
		HashMap message = (HashMap) obj;

		if (message.containsKey("events"))
		{
			/*
			String path1 = "./events";
			String path2 = path1 + "/" + getClientBySocket(src).socket.getRemoteIP();
			
			new File(path1).mkdir();
			new File(path2).mkdir();

			File file = new File(path2 + "/" + 1 + ".data");
			
			for (int nb = 2 ; file.exists() ; nb++)
				file = new File(path2 + "/" + nb + ".data");
			
			new ObjectOutputStream(
					new FileOutputStream(
								file)).writeObject(message.get("events"));
			*/
			String path = "./events/" + getClientBySocket(src).socket.getRemoteIP();
			File file = new File(path + "/" + 1 + ".data");
			for (int nb = 2 ; file.exists() ; nb++)
				file = new File(path + "/" + nb + ".data");
			if (Data.store(message.get("events"), file.getPath()))
				JOptionPane.showMessageDialog(m_vue, 
										"Evenements enregistres sous " + file.getPath());
		}
		
		if (message.containsKey("recording"))
		{
			Boolean recording = (Boolean) message.get("recording");
			if (recording && !isRecording)
			{
				m_vue.getBtnEnregistrer().setText("Stop");
				isRecording = true;
				m_vue.getListClients().setEnabled(false);
			}
			else if (!recording && isRecording)
			{
				m_vue.getBtnEnregistrer().setText("Enregistrer");
				isRecording = false;
				m_vue.getListClients().setEnabled(true);
			}
		}
		
		if (message.containsKey("replaying"))
		{
			Boolean replaying = (Boolean) message.get("replaying");
			if (replaying && !isReplaying)
			{
				m_vue.getBtnJouer().setText("Stop");
				isReplaying = true;
				m_vue.getListClients().setEnabled(false);
			}
			else if (!replaying && isReplaying)
			{
				m_vue.getBtnJouer().setText("Jouer");
				isReplaying = false;
				m_vue.getListClients().setEnabled(true);
			}
		}
	}

	public synchronized void socketClosed(ClientSocket src)
	{
		Client clientDeco = getClientBySocket(src);
		
		// System.out.println("Client deconnecte : " + clientDeco);
		listClients.removeElement(clientDeco);

		if (listClients.isEmpty())
		{
			m_vue.getBtnEnregistrer().setEnabled(false);
			m_vue.getBtnJouer().setEnabled(false);
			m_vue.getBtnDeconnecter().setEnabled(false);
			m_vue.getSliderVitesse().setEnabled(false);
		}
		m_vue.pack();
	}

	public void socketClosed(ServerSocket src)
	{
		ssocket = null;
		System.gc();
		m_vue.getSpinnerPort().setEnabled(true);
		m_vue.getBtnDemarrer().setText("Demarrer");
		m_vue.pack();
		started = false;
		// System.out.println("Serveur arrete");
	}
	
	/**
	 * le sliding de la vitesse de jeu qui slide
	 */
	public void stateChanged(ChangeEvent e)
	{
		sendSliderValue();
	}
	
	/**
	 * Selection dans la liste des clients
	 */
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) return;
		JList src = m_vue.getListClients();
		
		// pas de client selectionne
		if (src.getSelectedIndex() == -1)
		{
			m_vue.getBtnEnregistrer().setEnabled(false);
			m_vue.getBtnJouer().setEnabled(false);
			m_vue.getBtnDeconnecter().setEnabled(false);
		}
		
		// un client selectionne
		else
		{
			m_vue.getBtnEnregistrer().setEnabled(true);
			m_vue.getBtnJouer().setEnabled(true);
			m_vue.getBtnDeconnecter().setEnabled(true);
		}
		
	}

	/**
	 * Retourne les clients selectionnes de la liste
	 */
	private Client[] getSelectedClients()
	{
		Object[] objs = m_vue.getListClients().getSelectedValues();
		Client[] retour = new Client[objs.length];
		for (int i = 0 ; i < retour.length ; i++)
			retour[i] = (Client) objs[i];
		return retour;
	}
	
	/**
	 * Retourne tous les clients de la liste
	 */
	private Client[] getClients()
	{
		Client[] retour = new Client[listClients.getSize()];
		for (int i = 0 ; i < retour.length ; i++)
			retour[i] = (Client) listClients.getElementAt(i);
		return retour;
	}
	
	/**
	 * Retourne le client associe a une socket
	 */
	private Client getClientBySocket(ClientSocket csm)
	{
		for (int i = 0 ; i < listClients.getSize() ; i++)
		{
			Client cl = (Client)listClients.get(i);
			if (cl.socket.equals(csm)) return cl;
		}
		return null;
	}
	
	/**
	 * Efface le tableau des evenements
	 */
	private void clearEventList()
	{
		events.clear();
		while (tableEvents.getRowCount() > 0)
			tableEvents.removeRow(0);
	}
	
	/**
	 * Definit le tableau des evenements avec une liste
	 */
	private void setEventList(ArrayList<InputEvent> eventList)
	{
		clearEventList();
		if (eventList == null || eventList.isEmpty()) return;
		
		long ecart;
		String type;
		String info;
		long ex = eventList.get(0).getWhen();
		for (InputEvent event : eventList)
		{
			ecart = event.getWhen() - ex;
			ex = event.getWhen();
			
			type = event.getClass().getName();
			info = event.toString();
			
			events.add(event);
			tableEvents.addRow(new Object[]{ecart, type, info});
		}
	}

	private void startServer()
	{
		if (!started)
		{
			try {
				ssocket = new ServerSocket(
						(Integer) m_vue.getSpinnerPort().getValue());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			ssocket.addServerSocketListener(this);
			m_vue.getSpinnerPort().setValue(ssocket.getPort());
			m_vue.getSpinnerPort().setEnabled(false);
			m_vue.getBtnDemarrer().setText("Stop");
			m_vue.pack();
			started = true;
			// System.out.println("Serveur en ecoute sur le port " + ssocket.getPort());
		}
	}
	
	private void stopServer()
	{
		if (started) ssocket.stop();
	}

	/**
	 * Envoi a tous les clients la valeur courante du sliding de vitesse
	 */
	private void sendSliderValue()
	{
		double vitesse = m_vue.getSliderVitesse().getValue();
		vitesse /= 100;
		HashMap<String, Object> message = new HashMap<String, Object>();
		message.put("speed", vitesse);
		for (Client cl : getClients())
			cl.socket.send(message);
	}

}
