package socket;

/**
 * Ecouteur de sockets client.
 */
public interface ClientSocketListener
{
	/**
	 * Une requete est recue.
	 * @param obj : la requete.
	 * @parem src : la socket source, dont la requete provient.
	 */
	public void requestReceived(Object obj, ClientSocket src);
	
	/**
	 * La socket vient de se fermer.
	 * @parem src : la socket qui vient de se fermer.
	 */
	public void socketClosed(ClientSocket src);
}
