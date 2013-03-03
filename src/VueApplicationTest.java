import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Application de test numero 1 pour TestsG (vue)
 */
public class VueApplicationTest extends JFrame
{
	private static final long serialVersionUID = 42;
	private JPanel contentPane;
	private JTextField textField;
	private JTextArea fgf;

	public VueApplicationTest()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(0, 0);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblBlahBlihBleh = new JLabel("Blah blih bleh : ");
		panel.add(lblBlahBlihBleh);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_3.add(panel_4, BorderLayout.NORTH);
		
		JLabel lblCombobox = new JLabel("ComboBox : ");
		panel_4.add(lblCombobox);
		
		JComboBox comboBox = new JComboBox();
		panel_4.add(comboBox);
		
		JCheckBox chckbxCheckBox = new JCheckBox("CheckBox");
		panel_4.add(chckbxCheckBox);
		
		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));
		
		fgf = new JTextArea();
		fgf.setLineWrap(true);
		panel_5.add(fgf);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.SOUTH);
		
		JButton btnCacahuete = new JButton("Cacahuete !");
		panel_2.add(btnCacahuete);
		
		JPanel panel_6 = new JPanel();
		contentPane.add(panel_6, BorderLayout.WEST);
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			private static final long serialVersionUID = 1L;
			String[] values = new String[] {"GF", "G", "GF", "FG", "GFGFHGFH", "GF", "HGF", "HGF", "HFGgh", "g", "gf", "hgf", "hf", "gh", "fh", "f", "HFG", "HGF"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		JScrollPane jsp = new JScrollPane(list);
		panel_6.add(jsp);
		
		pack();
	}

}
