/**
 * Models a group of Bodies, calculating forces and keeping track of positions
 * @author Rajiv Thamburaj
 */
public class Cluster
{
	// Instance variables
	private Body[] bodies;
	// Constants
	private final double G = 10000.0;


	/**
	 * Constructor
	 * @param bodies
	 */
	public Cluster(Body ... bodies)
	{
		this.bodies = bodies;
	}

	/**
	 * Updates the position of each Body with Verlet integration by calculating velocities and accelerations
	 * @param dt
	 */
	public void updatePositions(double dt)
	{
		int numBodies = this.bodies.length;

		// First, find and update the position of each body
		for (int i = 0; i < numBodies; i++)
		{
			SpaceVector position = this.bodies[i].getPosition();
			SpaceVector velocity = this.bodies[i].getVelocity();
			SpaceVector acceleration = this.bodies[i].getAcceleration();

			// s(t+dt) = s(t) + dt*v(t) + 0.5*dt^2*a(t)
			SpaceVector newPosition = SpaceVector.add(position,
					velocity.scalarProduct(dt),
					acceleration.scalarProduct(dt*dt/2)
			);
			this.bodies[i].setPosition(newPosition);
		}

		// Next, find and update the velocity and acceleration of each body (we must do this after calculating all positions)
		for (int i = 0; i < numBodies; i++)
		{
			SpaceVector velocity = this.bodies[i].getVelocity();
			SpaceVector acceleration = this.bodies[i].getAcceleration();

			// a(t) is determined from the potential function for the system
			SpaceVector newAcceleration = getAcceleration(this.bodies[i]);
			this.bodies[i].setAcceleration(newAcceleration);

			// v(t+dt) = v(t) + 0.5*dt*[a(t) + a(t+dt)]
			SpaceVector newVelocity = SpaceVector.add(velocity,
					SpaceVector.add(acceleration, newAcceleration).scalarProduct(dt/2)
			);
			this.bodies[i].setVelocity(newVelocity);
		}
	}

	/**
	 * Finds the acceleration of a Body due to all other Bodies
	 * @param body
	 * @return SpaceVector corresponding to the Body's acceleration
	 */
	private SpaceVector getAcceleration(Body body)
	{
		// Get the body's position and mass
		SpaceVector position = body.getPosition();
		double mass = body.getMass();

		// Create an array of SpaceVectors
		int numBodies = this.bodies.length;
		SpaceVector[] forceVectors = new SpaceVector[numBodies-1];
		// We need to skip the Body whose acceleration we are calculating, so we must use a second index
		int currentForceVector = 0;


		// Find all force vectors
		for (int i = 0; i < numBodies; i++)
		{
			Body otherBody = this.bodies[i];

			// A Body does not exert a gravitational force on itself
			if (otherBody == body)
				continue;

			SpaceVector otherPosition = otherBody.getPosition();
			double otherMass = otherBody.getMass();

			// Find the force due to the current Body
			forceVectors[currentForceVector] = getNewtonianForce(mass, otherMass, position, otherPosition);

			currentForceVector++;
		}


		SpaceVector netForce = SpaceVector.add(forceVectors);

		// a = (1/m)*F
		SpaceVector acceleration = netForce.scalarProduct(1 / body.getMass());
		return acceleration;
	}

	/**
	 * Calculates the Newtonian gravitational force between two massive Bodies
	 * @param mass
	 * @param otherMass
	 * @param position
	 * @param otherPosition
	 * @return SpaceVector corresponding to the Newtonian force between two Bodies
	 */
	private SpaceVector getNewtonianForce(double mass, double otherMass, SpaceVector position, SpaceVector otherPosition)
	{
		// Since the scale of this simulation is much smaller (in terms of both distances and times), the value of G
		// must differ significantly from our universe's value of 6.67E-11

		// Scalar portion: G * m_1 * m_2 / r^2
		SpaceVector r = SpaceVector.add(otherPosition, position.negative());
		double magnitudeSquared = Math.pow(r.getNorm(), 2.0);
		double scalarPortion = this.G * mass * otherMass / magnitudeSquared;

		// Vector portion: r_u
		SpaceVector vectorPortion = r.normalized();

		SpaceVector newtonianForce = vectorPortion.scalarProduct(scalarPortion);
		return newtonianForce;
	}

	/**
	 * Due to the initial momentum of the system, the Bodies will eventually drift off Canvas. This method
	 * reduces the system's center-of-mass velocity to 0, so that it remains stationary with respect to the origin.
	 */
	public void adjustForCenterOfMassVelocity()
	{
		int numBodies = this.bodies.length;
		// These SpaceVectors corresponding to the momentum of each Body
		SpaceVector[] weightedVelocityVectors = new SpaceVector[numBodies];
		double totalMass = 0;

		// Find the weighted velocity vectors (momentum vectors) for the system
		for (int i = 0; i < numBodies; i++)
		{
			Body currentBody = this.bodies[i];

			// p = m * v
			SpaceVector weightedVelocity = currentBody.getVelocity().scalarProduct(currentBody.getMass());
			weightedVelocityVectors[i] = weightedVelocity;

			totalMass += currentBody.getMass();
		}

		// v_com = (∑ m_i * v_i) / (∑ m_i)
		SpaceVector velocity_com = SpaceVector.add(weightedVelocityVectors).scalarProduct(1.0 / totalMass);

		// Adjust each Body's velocity by subtracting the center-of-mass velocity
		for (int i = 0; i < numBodies; i++)
		{
			SpaceVector currentVelocity = this.bodies[i].getVelocity();
			SpaceVector adjustedVelocity = SpaceVector.add(currentVelocity, velocity_com.negative());
			this.bodies[i].setVelocity(adjustedVelocity);
		}
	}

	/**
	 * Finds and updates the initial accelerations for all Bodies
	 */
	public void computeInitialAccelerations()
	{
		int numBodies = this.bodies.length;

		// Find and update each Body's acceleration SpaceVector
		for (int i = 0; i < numBodies; i++)
		{
			SpaceVector acceleration = getAcceleration(bodies[i]);

			bodies[i].setAcceleration(acceleration);
		}
	}

	/**
	 * Gets bodies (Body[])
	 * @return Array of Bodies
	 */
	public Body[] getBodies()
	{
		return this.bodies;
	}
}