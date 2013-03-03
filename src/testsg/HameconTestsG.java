package testsg;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import socket.ClientSocket;
import socket.ClientSocketListener;
import util.Data;

/**
 * Hamecon pour l'application TestsG.
 */
public class HameconTestsG implements ClientSocketListener, ActionListener, ChangeListener
{
	/**
	 * La vue (optionnelle) pour le controle des evenements en local
	 */
	private VueHameconTestsG vue;
	
	/**
	 * L'espion des evenements de l'application
	 */
	private EspionG espion;
	
	/**
	 * La socket de connexion avec TestsG
	 */
	private ClientSocket socket;
	
	/**
	 * Le joueur d'evenement
	 */
	private Replayer replayer;
	
	/**
	 * La configuration courante de l'hamecon
	 */
	private HashMap configuration;
	
	/**
	 * Constructeur
	 */
	public HameconTestsG(String fichierConfig)
	{
		// *** chargement de la configuration
		configuration = chargerConfiguration(fichierConfig);
		
		espion = new EspionG();
		socket = null;
		replayer = new Replayer();
		
		// si on est en mode avec la vue de controle local
		if ((Boolean) configuration.get("controle_local"))
		{
			vue = new VueHameconTestsG();
			
			vue.getTextFieldIp().setText("" + configuration.get("ip"));
			vue.getSpinnerPort().setValue(configuration.get("port"));
			
			vue.getBtnConnecter().addActionListener(this);
			vue.getBtnEnregistrer().addActionListener(this);
			vue.getBtnJouer().addActionListener(this);
			vue.getSpinnerOffsetX().addChangeListener(this);
			vue.getSpinnerOffsetY().addChangeListener(this);

			vue.setVisible(true);
		}
		// sinon on se connecte immediatement
		else
		{
			vue = null;
			connecter();
		}
	}
	
	/**
	 * Constructeur par defaut (fichier de configuration = "hamecon.config")
	 */
	public HameconTestsG()
	{
		this("hamecon.config");
	}
	
	/**
	 * Charge la configuration depuis un fichier
	 */
	private static HashMap chargerConfiguration(String fichierConfig)
	{
		// configuration par defaut
		HashMap retour = new HashMap();
			retour.put("ip", "127.0.0.1");
			retour.put("port", 4242);
			retour.put("controle_local", false);
		try {
			File fichier = new File(fichierConfig);
			Scanner scan = new Scanner(fichier);
			while (scan.hasNext()) {
				String clef = scan.next();
				if (clef.equals("ip")) retour.put("ip", scan.next());
				else if (clef.equals("port")) retour.put("port", scan.nextInt());
				else if (clef.equals("controle_local")) retour.put("controle_local", scan.nextBoolean());
			}
		} catch (Exception e) {}
		return retour;
	}

	/**
	 * Requete recue de la part de TestsG
	 */
	public synchronized void requestReceived(Object obj, ClientSocket src)
	{
		// System.out.println("Recu : " + obj);
		
		if (!(obj instanceof HashMap)) return;
		
		HashMap message = (HashMap) obj;
		
		if (message.containsKey("record"))
		{
			HashMap<Object, Object> message_ret = new HashMap<Object, Object>();
			
			if ((Boolean)message.get("record"))
			{
				espion.startEventsTracker();
				message_ret.put("recording", true);
			}
			else
			{
				envoyerEvents();
				espion.stopEventsTracker();
				message_ret.put("recording", false);
			}
			
			socket.send(message_ret);
		}
		
		if (message.containsKey("replay"))
		{
			HashMap<Object, Object> message_ret = new HashMap<Object, Object>();
			
			Object events = message.get("replay");
			
			if (events == null)
			{
				Replayer.stopAll();
				message_ret.put("replaying", false);
				socket.send(message_ret);
			}
			
			else
			{
				message_ret.put("replaying", true);
				socket.send(message_ret);
				
				replayer.replay((ArrayList<InputEvent>) events);
				
				message_ret.put("replaying", false);
				socket.send(message_ret);
			}
		}
		
		if (message.containsKey("speed"))
		{
			replayer.setSpeed((Double) message.get("speed"));
		}
		
		if (message.containsKey("sync"))
		{
			espion.synchronisation((Long) message.get("sync"));
		}
	}

	/**
	 * Socket fermee avec TestsG
	 */
	public synchronized void socketClosed(ClientSocket src)
	{
		deconnecter();
	}

	/**
	 * Clic sur un bouton sur la vue de controle local
	 */
	public void actionPerformed(ActionEvent e)
	{
		// bouton connecter
		if (e.getSource() == vue.getBtnConnecter())
		{
			if (socket == null) connecter();
			else deconnecter();
		}
		// bouton enregistrer
		else if (e.getSource() == vue.getBtnEnregistrer())
		{
			if (espion.isTracking())
			{
				espion.stopEventsTracker();
				JFileChooser fc = new JFileChooser(".");
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileNameExtensionFilter("Liste d'evenements (*.data)", "data"));
				int ret = fc.showSaveDialog(vue);
				if (ret == JFileChooser.APPROVE_OPTION)
				{
					File f = fc.getSelectedFile();
					if (Data.store(espion.getEvents(), f.getPath()))
						JOptionPane.showMessageDialog(vue, "Evenements enregistres avec succes !");
				}
				espion.resetEvents();
				vue.getBtnEnregistrer().setText("Enregistrer");
				vue.getBtnJouer().setEnabled(true);
			}
			else
			{
				vue.getBtnEnregistrer().setText("Stop");
				vue.getBtnJouer().setEnabled(false);
				espion.startEventsTracker();
			}
		}
		else if (e.getSource() == vue.getBtnJouer())
		{
			if (replayer.isPlaying())
			{
				Replayer.stopAll();
			}
			else
			{
				JFileChooser fc = new JFileChooser(".");
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileFilter(new FileNameExtensionFilter("Liste d'evenements (*.data)", "data"));
				int ret = fc.showOpenDialog(vue);
				if (ret == JFileChooser.APPROVE_OPTION)
				{
					final File f = fc.getSelectedFile();
					(new Thread(){
						public void run() {
							try{
								vue.getBtnJouer().setText("Stop");
								vue.getBtnEnregistrer().setEnabled(false);
								replayer.replay(
								(ArrayList<InputEvent>) Data.load(f.getPath()));
								vue.getBtnJouer().setText("Jouer");
								vue.getBtnEnregistrer().setEnabled(true);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(vue, "Impossible de lire la liste d'evenements !");
							}
						}
					}).start();
				}
			}
		}
	}
	
	/**
	 * Connexion avec TestsG
	 */
	private void connecter()
	{
		if (socket != null) return;
		if ((Boolean) configuration.get("controle_local"))
		{
			configuration.put("ip", vue.getTextFieldIp().getText());
			configuration.put("port", vue.getSpinnerPort().getValue());
		}
		try
		{
			socket = new ClientSocket("" + configuration.get("ip"), 
										(Integer)configuration.get("port"));
			socket.addClientSocketListener(this);
			
			if ((Boolean) configuration.get("controle_local"))
			{
				vue.getBtnConnecter().setText("Deconnecter");
				vue.getBtnEnregistrer().setEnabled(false);
				vue.getBtnJouer().setEnabled(false);
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(vue, "Erreur de connexion avec TestsG : " + e.getMessage());
		}
	}
	
	/**
	 * Deconnection de TestsG
	 */
	private synchronized void deconnecter()
	{
		if (socket == null) return;
		socket.stop();
		if ((Boolean) configuration.get("controle_local"))
		{
			vue.getBtnConnecter().setText("Connecter");
			vue.getBtnEnregistrer().setEnabled(true);
			vue.getBtnJouer().setEnabled(true);
		}
		socket = null;
	}

	/**
	 * Envoi les evenements a TestsG
	 */
	private void envoyerEvents()
	{
		if (espion.getEvents().isEmpty()) return;
		
		HashMap<Object, Object> message = new HashMap<Object, Object>();
		
		boolean relance = espion.isTracking();
		
		if (relance) espion.stopEventsTracker();
		
		message.put("events", espion.getEvents());
		socket.send(message);
		espion.resetEvents();
		
		if (relance) espion.startEventsTracker();
	}

	/**
	 * Changement d'un spinner de l'offset
	 */
	public void stateChanged(ChangeEvent e)
	{
		int offX = (Integer) vue.getSpinnerOffsetX().getValue();
		int offY = (Integer) vue.getSpinnerOffsetY().getValue();
		replayer.setOffset(new Dimension(offX, offY));
	}
}
