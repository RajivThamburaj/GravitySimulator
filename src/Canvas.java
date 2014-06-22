import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import org.w3c.dom.*;

/**
 * Inherits from JPanel - the "view" class for the Cluster "model"
 * @author Rajiv Thamburaj
 */
public final class Canvas extends JPanel implements ActionListener
{
	// Instance variables
	private Timer timer;
	private Cluster cluster;
	private ArrayList<Point2D.Double[]> points;
	private String configurationsFile;
	private int frameNumber = 0;
	private boolean showPaths;
	private double timeStep = 0.0005;
	// Constants
	private final int MAX_TRACE_POINTS = 200;
	private final int VIEW_UPDATE_RATE = 10;

	/**
	 * Constructor
	 */
	public Canvas()
	{
		double DEFAULT_TIME_STEP = 0.0005;

		this.setOpaque(true);
		setBackground(new Color(0, 0, 40));

		this.timer = new Timer(1, this);
		this.points = new ArrayList<Point2D.Double[]>();
		this.showPaths = true;
		this.timeStep = DEFAULT_TIME_STEP;
	}

	/**
	 * Paints bodies and paths to the JPanel
	 * @param g
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Translate the coordinate system
		g2d.translate(this.getWidth() / 2, this.getHeight() / 2);

		Body[] bodies = this.cluster.getBodies();
		int numBodies = bodies.length;
		Point2D.Double[] pointArray = new Point2D.Double[numBodies];

		// Paint each body
		for (int i = 0; i < numBodies; i++)
		{
			SpaceVector position = bodies[i].getPosition();
			double[] components = position.getComponents();
			double x = components[0];
			double y = components[1];
			double diameter = bodies[i].getDiameter();

			// This ArrayList keeps track of the position of the body at fixed intervals to
			// paint the motion path
			pointArray[i] = new Point2D.Double(x, -y);

			// Paint the body
			g2d.setColor(bodies[i].getColor());
			g2d.fill(new Ellipse2D.Double(x - diameter / 2, -y - diameter / 2, diameter, diameter));

			// Remove earlier points from the ArrayList
			if (this.points.size() > this.MAX_TRACE_POINTS)
			{
				points.remove(0);
			}

			// Create a GeneralPath that passes through each point in the ArrayList
			if (this.showPaths && points.size() >= 2)
			{
				GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, this.points.size());
				path.moveTo(this.points.get(0)[i].getX(), this.points.get(0)[i].getY());

				for (int j = 1; j < this.points.size(); j++)
				{
					path.lineTo(this.points.get(j)[i].getX(), this.points.get(j)[i].getY());
				}

				g2d.draw(path);
			}

			// Create a light border around the body
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.draw(new Ellipse2D.Double(x - diameter / 2, -y - diameter / 2, diameter, diameter));
		}

		// Add the array corresponding to the locations of each body to the ArrayList
		this.points.add(pointArray);
	}

	/**
	 * Loads the desired configuration and computes initial conditions
	 * @param configurationName
	 */
	public void prepareSimulation(String configurationName)
	{
		this.cluster = loadConfiguration(configurationName);
		this.cluster.adjustForCenterOfMassVelocity();
		this.cluster.computeInitialAccelerations();
	}

	/**
	 * Reads the configuration information from the XML file
	 * @param configurationName
	 * @return Cluster corresponding to the configuration name
	 */
	private Cluster loadConfiguration(String configurationName)
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

			for (int i = 0; i < numNodes; i++)
			{
				Node node = nodeList.item(i);

				if (node.getNodeType() != Node.ELEMENT_NODE)
					continue;

				Element element = (Element) node;

				String name = element.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();

				// Verify that the current configuration has the desired name
				if (!name.equals(configurationName))
					continue;

				// Get the list of bodies in the configuration
				NodeList bodiesList = element.getElementsByTagName("body");
				int numBodies = bodiesList.getLength();
				Body[] bodies = new Body[numBodies];

				// Extract the information for each body
				for (int j = 0; j < numBodies; j++)
				{
					Node bodyNode = bodiesList.item(j);

					if (bodyNode.getNodeType() != Node.ELEMENT_NODE)
						continue;

					Element bodyElement = (Element) bodyNode;

					// Find the body's diameter, mass, position, and velocity
					Double diameter = Double.parseDouble(bodyElement.getElementsByTagName("diameter").item(0).getChildNodes().item(0).getNodeValue());
					Double mass = Double.parseDouble(bodyElement.getElementsByTagName("mass").item(0).getChildNodes().item(0).getNodeValue());
					Double s_x = Double.parseDouble(bodyElement.getElementsByTagName("s_x").item(0).getChildNodes().item(0).getNodeValue());
					Double s_y = Double.parseDouble(bodyElement.getElementsByTagName("s_y").item(0).getChildNodes().item(0).getNodeValue());
					Double v_x = Double.parseDouble(bodyElement.getElementsByTagName("v_x").item(0).getChildNodes().item(0).getNodeValue());
					Double v_y = Double.parseDouble(bodyElement.getElementsByTagName("v_y").item(0).getChildNodes().item(0).getNodeValue());
					String RGB = bodyElement.getElementsByTagName("RGB").item(0).getChildNodes().item(0).getNodeValue();

					SpaceVector position = new SpaceVector(s_x, s_y);
					SpaceVector velocity = new SpaceVector(v_x, v_y);

					// Find the planet's color
					String[] colorValues = RGB.split("-");
					int r = Integer.parseInt(colorValues[0]);
					int g = Integer.parseInt(colorValues[1]);
					int b = Integer.parseInt(colorValues[2]);
					Color color = new Color(r, g, b);

					Body body = new Body(diameter, mass, position, velocity, color);
					bodies[j] = body;
				}

				// Return the desired Cluster
				Cluster cluster = new Cluster(bodies);
				return cluster;
			}
		}

		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		return new Cluster();
	}

	/**
	 * Begins the simulation by starting the timer
	 */
	public void beginSimulation()
	{
		this.timer.start();
	}

	/**
	 * Pauses the simulation by stopping the timer
	 */
	public void pauseSimulation()
	{
		this.timer.stop();
	}

	/**
	 * Removes the motion-tracking points from the Canvas
	 */
	public void clearPoints()
	{
		this.points = new ArrayList<Point2D.Double[]>();
	}

	/**
	 * Listener for timer events (telling the Canvas to update the model and the view)
	 * @param event
	 */
	public void actionPerformed(ActionEvent event)
	{
		// Update the model
		this.cluster.updatePositions(this.timeStep);
		this.frameNumber++;

		// Update the view (less frequently)
		if (this.frameNumber % this.VIEW_UPDATE_RATE == 0)
		{
			repaint();
		}
	}

	// Mutator methods

	/**
	 * Sets configurationsFile (String)
	 * @param configurationsFile
	 */
	public void setConfigurationsFile(String configurationsFile)
	{
		this.configurationsFile = configurationsFile;
	}

	/**
	 * Sets showPaths (boolean)
	 * @param showPaths
	 */
	public void setShowPaths(boolean showPaths)
	{
		this.showPaths = showPaths;
	}

	/**
	 * Sets timeStep (double)
	 * @param timeStep
	 */
	public void setTimeStep(double timeStep)
	{
		this.timeStep = timeStep;
	}
}