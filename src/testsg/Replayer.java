package testsg;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import util.Data;

/**
 * S'occupe de rejouer les evenements qu'on lui donne
 */
public class Replayer
{
	private Robot bot;
	
	private double speed;
	
	private Dimension offset;
	
	private boolean playing;
	
	private static boolean stop = false;
	
	/**
	 * Constructeur par defaut : vitesse 1.0
	 */
	public Replayer()
	{
		this(1.0);
	}
	
	/**
	 * Constructeur normal
	 * @param speed : le facteur vitesse de jeu du replayer. Un petit nombre
	 * augmente la vitesse, alors qu'un grand nombre la reduit.
	 */
	public Replayer(double speed)
	{
		this.speed = speed;
		try {
			bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		playing = false;
		offset = new Dimension(0, 0);
	}
	
	/**
	 * Joue une liste d'evenements
	 */
	public void replay(ArrayList<InputEvent> events)
	{
		if (events == null || events.isEmpty()) return;
		int id = 0;
		long ecart;
		long ex = events.get(0).getWhen();
		stop = false;
		playing = true;
		for (InputEvent event : events)
		{
			if (stop) break;
			
			id = event.getID();
			ecart = event.getWhen() - ex;
			ex = event.getWhen();
			if (ecart < 0) ecart = 0;
			bot.delay((int) (ecart * speed));
			
			if (event instanceof MouseEvent)
			{
				MouseEvent mEvent = (MouseEvent)event;
				if (id == MouseEvent.MOUSE_MOVED || id == MouseEvent.MOUSE_DRAGGED) {	
					bot.mouseMove(mEvent.getXOnScreen() + offset.width, mEvent.getYOnScreen() + offset.height);
				}
				else if (mEvent.getID() == MouseEvent.MOUSE_PRESSED) {
					bot.mousePress(mouseEventIdToInputEventId(mEvent.getButton()));
				}
				else if (mEvent.getID() == MouseEvent.MOUSE_RELEASED) {
					bot.mouseRelease(mouseEventIdToInputEventId(mEvent.getButton()));
				}
				else if (mEvent.getID() == MouseEvent.MOUSE_WHEEL) {
					MouseWheelEvent mwEvent = (MouseWheelEvent) mEvent;
					bot.mouseWheel(mwEvent.getWheelRotation());
				}
			}
			if (event instanceof KeyEvent)
			{
				KeyEvent kEvent = (KeyEvent)event;
				if (id == KeyEvent.KEY_PRESSED) {
					bot.keyPress(kEvent.getKeyCode());
				}
				else if (id == KeyEvent.KEY_RELEASED) {
					bot.keyRelease(kEvent.getKeyCode());
				}
			}
		}
		playing = false;
	}
	
	/**
	 * Joue une liste d'evenement serialisee dans un fichier
	 */
	public void replay(String fileName)
	{
		replay((ArrayList<InputEvent>) Data.load(fileName));
	}
	
	/**
	 * Definit le decalage de coordonnees a utiliser pour le positionnement de la souris 
	 */
	public void setOffset(Dimension offset)
	{
		this.offset = offset;
	}
	
	/**
	 * Definit la vitesse de jeu
	 */
	public void setSpeed(double speed)
	{
		this.speed = speed;
	}
	
	/**
	 * Retourne true ssi le robot est en train de jouer des evenements
	 */
	public boolean isPlaying()
	{
		return playing;
	}
	
	/**
	 * Arrete immediatement tous les robots qui sont en train de jouer
	 */
	public static void stopAll()
	{
		stop = true;
	}
	
	private int mouseEventIdToInputEventId(int mouseEventId)
	{
		switch (mouseEventId)
		{
		case MouseEvent.BUTTON1 :
			return InputEvent.BUTTON1_MASK;
		case MouseEvent.BUTTON2 :
			return InputEvent.BUTTON2_MASK;
		case MouseEvent.BUTTON3 :
			return InputEvent.BUTTON3_MASK;
		default :
			return 0;
		}
	}
}
