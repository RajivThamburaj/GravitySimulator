import java.awt.Color;

/**
 * Models a celestial body (such as a star, planet, or moon)
 * @author Rajiv Thamburaj
 */
public class Body
{
	// Instance variables
	private double diameter;
	private double mass;
	private SpaceVector position;
	private SpaceVector velocity;
	private SpaceVector acceleration;
	private Color color;

	/**
	 * Constructor
	 * @param diameter
	 * @param mass
	 * @param position
	 * @param velocity
	 * @param color
	 */
	public Body(double diameter, double mass, SpaceVector position, SpaceVector velocity, Color color)
	{
		this.diameter = diameter;
		this.mass = mass;
		this.position = position;
		this.velocity = velocity;
		this.color = color;

	}

	/**
	 * Gets diameter (double)
	 * @return Diameter of the Body
	 */
	public double getDiameter()
	{
		return this.diameter;
	}

	/**
	 * Gets mass (double)
	 * @return Mass of the Body
	 */
	public double getMass()
	{
		return this.mass;
	}

	/**
	 * Gets position (SpaceVector)
	 * @return Position of the Body
	 */
	public SpaceVector getPosition()
	{
		return this.position;
	}

	/**
	 * Sets Position (SpaceVector)
	 * @param position
	 */
	public void setPosition(SpaceVector position)
	{
		this.position = position;
	}

	/**
	 * Gets velocity (SpaceVector)
	 * @return Velocity of the body
	 */
	public SpaceVector getVelocity()
	{
		return this.velocity;
	}

	/**
	 * Sets velocity (SpaceVector)
	 * @param velocity
	 */
	public void setVelocity(SpaceVector velocity)
	{
		this.velocity = velocity;
	}

	/**
	 * Gets acceleration (SpaceVector)
	 * @return Acceleration of the Body
	 */
	public SpaceVector getAcceleration()
	{
		return acceleration;
	}

	/**
	 * Sets acceleration (SpaceVector)
	 * @param acceleration
	 */
	public void setAcceleration(SpaceVector acceleration)
	{
		this.acceleration = acceleration;
	}

	/**
	 * Gets color (Color)
	 * @return Color of the Body (for painting purposes)
	 */
	public Color getColor()
	{
		return color;
	}
}