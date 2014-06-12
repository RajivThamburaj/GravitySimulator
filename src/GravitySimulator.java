import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import org.w3c.dom.*;

/**
 * Base class for this project. Extends a JFrame to build the user interface.
 * @author Rajiv Thamburaj
 */
public final class GravitySimulator extends JFrame implements ItemListener, ChangeListener, ActionListener
{
	// Instance variables
	private Canvas canvas;
	private String[] configurationNames;
	private final String configurationsFile = "ClusterConfigurations.xml";
	private JButton startButton;
	private JComboBox comboBox;

	/**
	 * Constructor
	 */
	public GravitySimulator()
	{
		loadClusterConfigurationNames();
		initGUI();
	}

	/**
	 * Loads the names of the cluster configurations from the given XML file
	 */
	private void loadClusterConfigurationNames()
	{
		try
		{
			// Get the document element from the XML file
			File file = new File(this.configurationsFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			// Create the list of nodes
			NodeList nodeList = doc.getElementsByTagName("configuration");
			int numNodes = nodeList.getLength();
			this.configurationNames = new String[numNodes];

			// Add each node's name element to the configurationNames array
			for (int i = 0; i < numNodes; i++)
			{
				Node node = nodeList.item(i);

				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element element = (Element) node;

				String name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

				this.configurationNames[i] = name;
			}
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Initializes GUI elements, adds listeners, and prepares the simulation
	 */
	private void initGUI()
	{
		final int PAD = 5;

		// Set up the toolbar at the top of the frame
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(Color.GRAY);
		Insets insets = new Insets(PAD, PAD, PAD, PAD);

		// Set up local variables
		JComboBox comboBox;
		JButton button;
		JSlider slider;
		JCheckBox checkBox;
		GridBagConstraints constraints = new GridBagConstraints();

		// Create the comboBox (lists planetary configurations)
		comboBox = new JComboBox(this.configurationNames);
		this.comboBox = comboBox;
		comboBox.setFocusable(false);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = insets;
		panel.add(comboBox, constraints);
		constraints = new GridBagConstraints();
		comboBox.addActionListener(this);

		// Create the "Start" button (allows starting and pausing of the simulation)
		button = new JButton("Start");
		this.startButton = button;
		button.setFocusPainted(false);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = insets;
		panel.add(button, constraints);
		constraints = new GridBagConstraints();
		button.setActionCommand("start");
		button.addActionListener(this);

		// Create the "Reset" button (returns the simulation to its original state)
		button = new JButton("Reset");
		button.setFocusPainted(false);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = insets;
		panel.add(button, constraints);
		constraints = new GridBagConstraints();
		button.setActionCommand("reset");
		button.addActionListener(this);

		// Create the slider (controls the speed of the simulation)
		slider = new JSlider(JSlider.HORIZONTAL, 1, 9, 5);
		slider.setFocusable(false);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = insets;
		panel.add(slider, constraints);
		constraints = new GridBagConstraints();
		slider.addChangeListener(this);

		// Create the checkBox (controls whether path trajectories are visible)
		checkBox = new JCheckBox("Show paths");
		checkBox.setFocusable(false);
		checkBox.setForeground(Color.WHITE);
		checkBox.setSelected(true);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.weightx = 1;
		constraints.weighty = 0;
		constraints.insets = insets;
		panel.add(checkBox, constraints);
		constraints = new GridBagConstraints();
		checkBox.addItemListener(this);

		// Create an instance of the Canvas class (the space where planets are painted)
		canvas = new Canvas();
		canvas.setConfigurationsFile(this.configurationsFile);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.gridwidth = 5;
		panel.add(canvas, constraints);

		add(panel);

		// Configure the JFrame
		setTitle("Gravity Simulator");
		setSize(900, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		// Prepare the first configuration
		canvas.prepareSimulation(this.configurationNames[0]);
	}

	/**
	 * Listener for "start", "pause", and "reset" actions
	 * @param event
	 */
	public void actionPerformed(ActionEvent event)
	{
		if ("start".equals(event.getActionCommand()))
		{
			canvas.beginSimulation();

			JButton button = (JButton) event.getSource();
			button.setText("Pause");
			button.setActionCommand("pause");
		}

		else if ("pause".equals(event.getActionCommand()))
		{
			canvas.pauseSimulation();

			JButton button = (JButton) event.getSource();
			button.setText("Start");
			button.setActionCommand("start");
		}

		else if ("reset".equals(event.getActionCommand()) || "comboBoxChanged".equals(event.getActionCommand()))
		{
			canvas.pauseSimulation();

			canvas.prepareSimulation((String) this.comboBox.getSelectedItem());
			canvas.clearPoints();
			canvas.repaint();

			startButton.setText("Start");
			startButton.setActionCommand("start");
		}
	}

	/**
	 * Listener for state changes (relating to the JSlider)
	 * @param event
	 */
	public void stateChanged(ChangeEvent event)
	{
		JSlider source = (JSlider) event.getSource();
		double timeStep = source.getValue() / 10000.0;
		this.canvas.setTimeStep(timeStep);
	}

	/**
	 * Listener for item state changes (relating to the JCheckBox)
	 * @param event
	 */
	public void itemStateChanged(ItemEvent event)
	{
		if (event.getStateChange() == ItemEvent.SELECTED)
		{
			this.canvas.setShowPaths(true);
		}

		else
		{
			this.canvas.setShowPaths(false);
		}
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				GravitySimulator simulator = new GravitySimulator();
				simulator.setVisible(true);
			}
		});
	}
}