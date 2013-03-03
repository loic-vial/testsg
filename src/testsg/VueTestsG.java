package testsg;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JButton;
//import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.Box;
import javax.swing.JSlider;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.MatteBorder;

/**
 * Application TestsG (vue)
 */
public class VueTestsG extends JFrame
{
	private static final long serialVersionUID = 42;
	private JPanel contentPane;
	private JButton btnDemarrer;
	private JSpinner spinnerPort;
	private JTable tableEvents;
	private JButton btnEnregistrer;
	private JButton btnJouer;
	private JButton btnEffacer;
	private JButton btnDeconnecter;
	private JButton btnCharger;
	private JButton btnSauvegarder;
	private JSlider sliderVitesse;
	private JList listClients;

	/**
	 * Create the frame.
	 */
	public VueTestsG()
	{
		setTitle("TestsG");
		setMinimumSize(new Dimension(800, 600));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(Color.GRAY));
		contentPane.add(panel_4, BorderLayout.NORTH);

		JLabel lblTestsg = new JLabel("TestsG");
		lblTestsg.setFont(new Font("Dialog", Font.BOLD, 18));
		panel_4.add(lblTestsg);

		JPanel panel_5 = new JPanel();
		contentPane.add(panel_5, BorderLayout.CENTER);
		panel_5.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBorder(new MatteBorder(0, 1, 1, 1, (Color) Color.GRAY));
		panel_5.add(panel, BorderLayout.NORTH);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);

		JLabel lblPort = new JLabel("Port : ");
		panel.add(lblPort);

		spinnerPort = new JSpinner();
		spinnerPort.setModel(new SpinnerNumberModel(4242, 0, 65535, 1));
		panel.add(spinnerPort);

		btnDemarrer = new JButton("Demarrer");
		panel.add(btnDemarrer);

		JPanel panel_3 = new JPanel();
		panel_5.add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));
				
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setBorder(new MatteBorder(0, 1, 1, 1, (Color) Color.GRAY));
		panel_3.add(verticalBox, BorderLayout.NORTH);
		
		JPanel panel_7 = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) panel_7.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		verticalBox.add(panel_7);
		
		btnDeconnecter = new JButton("Deconnecter");
		panel_7.add(btnDeconnecter);
		
		btnEnregistrer = new JButton("Enregistrer");
		panel_7.add(btnEnregistrer);
		
		btnJouer = new JButton("Jouer");
		panel_7.add(btnJouer);
		
		JLabel lblVitesse = new JLabel("Vitesse : ");
		panel_7.add(lblVitesse);
		
		sliderVitesse = new JSlider();
		sliderVitesse.setSnapToTicks(true);
		sliderVitesse.setMajorTickSpacing(100);
		sliderVitesse.setMinorTickSpacing(25);
		sliderVitesse.setPaintTicks(true);
		sliderVitesse.setPaintLabels(true);
		sliderVitesse.setValue(100);
		sliderVitesse.setMaximum(500);
		panel_7.add(sliderVitesse);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new MatteBorder(0, 0, 1, 1, (Color) Color.GRAY));
		panel_3.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
				
		JPanel panel_10 = new JPanel();
		panel_2.add(panel_10, BorderLayout.NORTH);

		JLabel lblEvenements = new JLabel("Evenements : ");
		panel_10.add(lblEvenements);
		
		tableEvents = new JTable() {
			private static final long serialVersionUID = 42;
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(Math.max(rendererWidth +
						getIntercellSpacing().width,
						tableColumn.getPreferredWidth()));
				return component;
			}
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;
			}
		};
		tableEvents.setDoubleBuffered(true);
		tableEvents.setColumnSelectionAllowed(true);
		tableEvents.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane pane = new JScrollPane(tableEvents);
		pane.setDoubleBuffered(true);
		panel_2.add(pane, BorderLayout.CENTER);
		
		JPanel panel_6 = new JPanel();
		panel_2.add(panel_6, BorderLayout.SOUTH);
		
		btnCharger = new JButton("Charger");
		panel_6.add(btnCharger);
		
		btnSauvegarder = new JButton("Sauvegarder");
		btnSauvegarder.setVisible(false);
		panel_6.add(btnSauvegarder);
		
		btnEffacer = new JButton("Effacer");
		panel_6.add(btnEffacer);
		
		JPanel panel_8 = new JPanel();
		panel_8.setBorder(new MatteBorder(0, 1, 1, 1, (Color) Color.GRAY));
		panel_3.add(panel_8, BorderLayout.WEST);
		panel_8.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		panel_8.add(panel_1, BorderLayout.NORTH);
		
		JLabel lblClients = new JLabel("Clients : ");
		panel_1.add(lblClients);
		
		listClients = new JList();
		//listClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel_8.add(listClients, BorderLayout.CENTER);
		
		pack();
	}

	public JButton getBtnDemarrer()
	{
		return btnDemarrer;
	}

	public JButton getBtnEnregistrer()
	{
		return btnEnregistrer;
	}

	public JButton getBtnDeconnecter()
	{
		return btnDeconnecter;
	}

	public JButton getBtnCharger()
	{
		return btnCharger;
	}

	public JButton getBtnSauvegarder()
	{
		return btnSauvegarder;
	}

	public JButton getBtnJouer()
	{
		return btnJouer;
	}

	public JButton getBtnEffacer()
	{
		return btnEffacer;
	}
	
	public JSpinner getSpinnerPort()
	{
		return spinnerPort;
	}

	public JTable getTableEvents()
	{
		return tableEvents;
	}

	public JSlider getSliderVitesse()
	{
		return sliderVitesse;
	}

	public JList getListClients()
	{
		return listClients;
	}

}
