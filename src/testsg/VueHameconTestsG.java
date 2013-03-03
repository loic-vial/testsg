package testsg;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

public class VueHameconTestsG extends JFrame
{
	private static final long serialVersionUID = 42;
	private JTextField textFieldIp;
	private JButton btnConnecter;
	private JSpinner spinnerPort;
	private JButton btnEnregistrer;
	private JButton btnJouer;
	private JSpinner spinnerOffsetY;
	private JSpinner spinnerOffsetX;

	/**
	 * Create the frame.
	 */
	public VueHameconTestsG()
	{
		setTitle("Hamecon TestsG");
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Reseau", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_2 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel_2, BorderLayout.NORTH);
		
		JLabel lblPort = new JLabel("Port : ");
		panel_2.add(lblPort);
		
		spinnerPort = new JSpinner();
		spinnerPort.setModel(new SpinnerNumberModel(4242, 0, 65535, 1));
		panel_2.add(spinnerPort);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		panel_1.add(panel, BorderLayout.CENTER);
		
		JLabel lblIp = new JLabel("IP : ");
		panel.add(lblIp);
		
		textFieldIp = new JTextField();
		textFieldIp.setText("127.0.0.1");
		panel.add(textFieldIp);
		textFieldIp.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.SOUTH);
		
		btnConnecter = new JButton("Connecter");
		panel_3.add(btnConnecter);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Local", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_4);
		
		btnEnregistrer = new JButton("Enregistrer");
		panel_4.add(btnEnregistrer);
		
		btnJouer = new JButton("Jouer");
		panel_4.add(btnJouer);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Parametre", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));
		
		JPanel panel_6 = new JPanel();
		panel_5.add(panel_6);
		
		JLabel lblOffsetX = new JLabel("Offset X : ");
		panel_6.add(lblOffsetX);
		
		spinnerOffsetX = new JSpinner();
		spinnerOffsetX.setModel(new SpinnerNumberModel(0, -999, 999, 1));
		panel_6.add(spinnerOffsetX);
		
		JPanel panel_7 = new JPanel();
		panel_5.add(panel_7);
		
		JLabel lblOffsetY = new JLabel("Offset Y : ");
		panel_7.add(lblOffsetY);
		
		spinnerOffsetY = new JSpinner();
		spinnerOffsetY.setModel(new SpinnerNumberModel(0, -999, 999, 1));
		panel_7.add(spinnerOffsetY);
		
		pack();
	}

	public JTextField getTextFieldIp()
	{
		return textFieldIp;
	}

	public JButton getBtnConnecter()
	{
		return btnConnecter;
	}

	public JSpinner getSpinnerPort()
	{
		return spinnerPort;
	}

	public JButton getBtnEnregistrer()
	{
		return btnEnregistrer;
	}

	public JButton getBtnJouer()
	{
		return btnJouer;
	}

	public JSpinner getSpinnerOffsetY()
	{
		return spinnerOffsetY;
	}

	public JSpinner getSpinnerOffsetX()
	{
		return spinnerOffsetX;
	}

}
