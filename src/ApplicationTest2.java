import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Application de test numero 2 pour TestsG (controleur)
 */
public class ApplicationTest2
{
	/**
	 * Vue
	 */
	private VueApplicationTest2 vue;
	
	/**
	 * Constructeur.
	 */
	public ApplicationTest2()
	{
		vue = new VueApplicationTest2();
		
		vue.setTitle("Application Test");
		vue.getComboBoxItem().addItem("France");
		vue.getComboBoxItem().addItem("Italie");
		vue.getComboBoxItem().addItem("Espagne");
		vue.getComboBoxItem().addItem("Allemagne");
		vue.getLabelCompteur().setText("0");
		
		vue.getBtnTitre().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				vue.setTitle(vue.getTextFieldTitre().getText());
			}
		});
		
		vue.getBtnIncrementerLeCompteur().addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				vue.getLabelCompteur().setText("" + (getCompteur() + 1));
			}
		});
		
		vue.setVisible(true);
	}
	
	/**
	 * Retourne le titre de l'application
	 */
	public String getTitle()
	{
		return vue.getTitle();
	}
	
	/**
	 * Retourne l'item selectionne dans la liste
	 */
	public Object getSelectedItem()
	{
		return vue.getComboBoxItem().getSelectedItem();
	}
	
	/**
	 * Retourne la valeur du compteur
	 */
	public int getCompteur()
	{
		return Integer.valueOf(vue.getLabelCompteur().getText());
	}
	
	/**
	 * Ferme la fenetre
	 */
	public void finalize()
	{
		vue.dispose();
	}
}
