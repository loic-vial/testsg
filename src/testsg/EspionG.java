package testsg;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

public class EspionG implements AWTEventListener
{
	private final ArrayList<InputEvent> events;
	
	private boolean tracking;
	
	private long ecartTimestamp;

	/**
	 * Constructeur par defaut : 
	 * - espion inactif,
	 * - sans decalage de timestamp.
	 */
	public EspionG()
	{
		Toolkit.getDefaultToolkit().addAWTEventListener(this, 
				AWTEvent.MOUSE_MOTION_EVENT_MASK +
				AWTEvent.MOUSE_WHEEL_EVENT_MASK +
				AWTEvent.MOUSE_EVENT_MASK + 
				AWTEvent.KEY_EVENT_MASK);
		
		events = new ArrayList<InputEvent>();
		tracking = false;
		ecartTimestamp = 0;
	}

	/**
	 * Constructeur normal.
	 * @param tsSynchro : Le timestamp sur lequel se synchroniser
	 */
	public EspionG(long tsSynchro)
	{
		this();
		synchronisation(tsSynchro);
	}

	/**
	 * Constructeur normal.
	 * @param tsDebut : Le timestamp a partir duquel on commence a espionner
	 * @param tsFin : Le timestamp a partir duquel on arrete d'espionnner
	 * @param tsSynchro : Le timestamp sur lequel se synchroniser
	 */
	public EspionG(long tsDebut, long tsFin, long tsSynchro)
	{
		this(tsSynchro);
		new AutoEspion(tsDebut, tsFin).start();
	}

	/**
	 * Methode startEventsTracker
	 */
	public void startEventsTracker()
	{
		tracking = true;
	}

	/**
	 * Methode stopEventsTracker
	 */
	public void stopEventsTracker()
	{
		tracking = false;
	}
	
	/**
	 * 
	 */
	public boolean isTracking()
	{
		return tracking;
	}

	/**
	 * Methode getEvents
	 */
	public ArrayList<InputEvent> getEvents()
	{
		return events;
	}

	/**
	 * Methode resetEvents
	 */
	public void resetEvents()
	{
		events.clear();
	}

	/**
	 * Retourne le timestamp du dernier evenement. Retourne 0 si aucun evenement
	 * n'est enregistre. Cette metode prend en compte la synchronisation, la
	 * date renvoyee est donc exacte.
	 */
	public long getDateDernierEvenement()
	{
		if (events.isEmpty()) return 0;
		InputEvent event = events.get(events.size() - 1);
		return event.getWhen() + ecartTimestamp;
	}

	/**
	 * Retourne l'ecart de timestamp courant
	 */
	public long getEcartTimestamp()
	{
		return ecartTimestamp;
	}

	/**
	 * Synchronise les dates des evenements avec un timestamp
	 */
	public void synchronisation(long timestamp)
	{
		ecartTimestamp = timestamp - new Date().getTime();
	}

	/**
	 * Classe interne gerant le depart et l'arret automatique de l'espion
	 */
	private class AutoEspion extends Thread
	{
		private long tsDebut;
		private long tsFin;

		public AutoEspion(long tsDebut, long tsFin)
		{
			this.tsDebut = tsDebut;
			this.tsFin = tsFin;
		}

		public void run()
		{
			// recupere le temps d'attente necessaire
			long attente = tsDebut - new Date().getTime();

			// on attend jusqu'au timestamp de debut
			try
			{
				Thread.sleep(attente);
			} catch (InterruptedException e) {}

			// on lance l'espion
			EspionG.this.startEventsTracker();

			// on recupere le temps pendant lequel on doit espionner
			long duree = tsFin - tsDebut;

			// on attend la fin de l'espionnage
			try
			{
				Thread.sleep(duree);
			} catch (InterruptedException e){}

			// enfin on arrete l'espion
			EspionG.this.stopEventsTracker();
		}
	}

	public void eventDispatched(AWTEvent e)
	{
		if (tracking && e instanceof InputEvent)
		{
			InputEvent ie = (InputEvent) e;
			int id = ie.getID();
			
			if ((e instanceof KeyEvent && 
				(id == KeyEvent.KEY_PRESSED || 
				id == KeyEvent.KEY_RELEASED)) ||
				(e instanceof MouseEvent && 
				(id == MouseEvent.MOUSE_MOVED ||
				id == MouseEvent.MOUSE_DRAGGED || 
				id == MouseEvent.MOUSE_PRESSED || 
				id == MouseEvent.MOUSE_WHEEL || 
				id == MouseEvent.MOUSE_RELEASED)))
					events.add(ie);
		}
	}
}
