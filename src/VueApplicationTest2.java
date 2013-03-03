import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JComboBox;

/**
 * Application test numero 2 pour TestsG (vue)
 */
public class VueApplicationTest2 extends JFrame
{
	private static final long serialVersionUID = 42;
	
	private JLabel labelCompteur;
	private JButton btnIncrementerLeCompteur;
	private JTextField textFieldTitre;
	private JComboBox comboBoxItem;

	private JButton btnTitre;

	public VueApplicationTest2()
	{
		setTitle("Application Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1);
		
		JLabel lblChangerLeTitre = new JLabel("Changer le titre de l'application : ");
		panel_1.add(lblChangerLeTitre);
		
		textFieldTitre = new JTextField();
		panel_1.add(textFieldTitre);
		textFieldTitre.setColumns(10);
		
		btnTitre = new JButton("Valider");
		panel_1.add(btnTitre);
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel.add(panel_2);
		
		JLabel lblSelectionnerUnItem = new JLabel("Selectionner un item dans la liste : ");
		panel_2.add(lblSelectionnerUnItem);
		
		comboBoxItem = new JComboBox();
		panel_2.add(comboBoxItem);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_3.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel.add(panel_3);
		
		btnIncrementerLeCompteur = new JButton("Incrementer le compteur");
		panel_3.add(btnIncrementerLeCompteur);
		
		labelCompteur = new JLabel("0");
		panel_3.add(labelCompteur);
		
		pack();
	}

	public JLabel getLabelCompteur()
	{
		return labelCompteur;
	}

	public JButton getBtnIncrementerLeCompteur()
	{
		return btnIncrementerLeCompteur;
	}

	public JTextField getTextFieldTitre()
	{
		return textFieldTitre;
	}

	public JComboBox getComboBoxItem()
	{
		return comboBoxItem;
	}

	public JButton getBtnTitre()
	{
		return btnTitre;
	}

}
