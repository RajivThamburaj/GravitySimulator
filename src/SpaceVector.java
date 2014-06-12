/**
 * Performs operations on elements of vector spaces
 * @author Rajiv Thamburaj
 */
public class SpaceVector
{
	// Instance variables
	private double[] components;
	private int dimension;

	/**
	 * Constructor
	 * @param initialComponents
	 */
	public SpaceVector(double ... initialComponents)
	{
		this.dimension = initialComponents.length;
		this.components = new double[this.dimension];

		for (int i = 0; i < this.dimension; i++)
		{
			this.components[i] = initialComponents[i];
		}
	}

	/**
	 * Converts the SpaceVector to a String of the form "[x_1, x_2, ..., x_n]"
	 * @return String corresponding to the SpaceVector
	 */
	@Override
	public String toString()
	{
		String vector = "[";

		// Loop through all components
		for (int i = 0; i < this.dimension; i++)
		{
			if (i == this.dimension - 1)
				vector += this.components[i];
			else
				vector += this.components[i] + ", ";
		}

		vector += "]";
		return vector;
	}

	/**
	 * Gets the magnitude of the SpaceVector
	 */
	public double getNorm()
	{
		double normSquared = 0.0;

		for (int i = 0; i < this.dimension; i++)
		{
			normSquared += Math.pow(this.components[i], 2.0);
		}

		// |v| = sqrt(v_1^2 + v_2^2 + ... + v_n^2)
		double norm = Math.sqrt(normSquared);
		return norm;
	}

	/**
	 * Normalizes the SpaceVector
	 * @return Normalized SpaceVector
	 */
	public SpaceVector normalized()
	{
		// u = (1/|v|) * v
		return this.scalarProduct(1/(this.getNorm()));
	}

	/**
	 * Multiplies the SpaceVector by a scalar
	 * @param scalar
	 * @return SpaceVector corresponding to the scalar product
	 */
	public SpaceVector scalarProduct(double scalar)
	{
		double[] newComponents = new double[this.dimension];

		// Multiply each component by the scalar
		for (int i = 0; i < this.dimension; i++)
		{
			newComponents[i] = this.components[i] * scalar;
		}

		SpaceVector newVector = new SpaceVector(newComponents);
		return newVector;
	}

	/**
	 * Adds a group of SpaceVectors
	 * @param vectors
	 * @return SpaceVector corresponding to the sum of the arguments
	 */
	public static SpaceVector add(SpaceVector ... vectors)
	{
		int dimension = vectors[0].getDimension();
		double[] newComponents = new double[dimension];

		// Add the SpaceVectors by summing components
		for (int i = 0; i < dimension; i++)
		{
			double currentSum = 0.0;

			for (int j = 0; j < vectors.length; j++)
			{
				currentSum += vectors[j].getComponents()[i];
			}

			newComponents[i] = currentSum;
		}

		SpaceVector sum = new SpaceVector(newComponents);
		return sum;
	}

	/**
	 * Gets dimension (int)
	 * @return Dimension of the SpaceVector
	 */
	public int getDimension()
	{
		return this.dimension;
	}

	/**
	 * Gets components (double[])
	 * @return Components of the SpaceVector
	 */
	public double[] getComponents()
	{
		return this.components;
	}
}