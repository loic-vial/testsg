package socket;

/**
 * Ecouteur de sockets serveur.
 */
public interface ServerSocketListener
{
	/**
	 * Un client vient de se connecter.
	 * @param client : le client.
	 */
	public void clientConnected(ClientSocket client);
	
	/**
	 * La socket vient de se fermer.
	 * @parem src : la socket qui vient de se fermer.
	 */
	public void socketClosed(ServerSocket src);
}
