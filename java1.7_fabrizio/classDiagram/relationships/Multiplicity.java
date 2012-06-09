package classDiagram.relationships;

import java.util.Observable;

import utility.Utility;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;

/**
 * Represent a multiplicity in UML structure. A multiplicity is used by role to
 * representing the number of occurence for the given role.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Multiplicity extends Observable implements IDiagramComponent
{
	public static final Multiplicity ONE_ONLY = new Multiplicity(1);

	public static final Multiplicity ONE_OR_MORE = new Multiplicity(1, Integer.MAX_VALUE);
	public static final Multiplicity ZERO = new Multiplicity(0);
	public static final Multiplicity ZERO_OR_MORE = new Multiplicity(0, Integer.MAX_VALUE);
	public static final Multiplicity ZERO_OR_ONE = new Multiplicity(0, 1);
	protected final int id = ClassDiagram.getNextId();

	private int lowerBound, upperBound;

	/**
	 * Create a new multiplicity with a lower bound equals to upper bounds. It
	 * is represented by only one number.
	 * 
	 * @param exactNumber
	 *            the number for the multiplicity.
	 */
	public Multiplicity(int exactNumber)
	{
		this(exactNumber, exactNumber);
	}

	/**
	 * Create a new multiplicity with the lower bound and upper bound. The lower
	 * bound can't be greater than upper bounds. Upper bound and lower bound
	 * must be greater than zero.
	 * 
	 * @param lowerBound
	 *            the lower bound
	 * @param upperBound
	 *            the upper bound
	 * @throws IllegalArgumentException
	 *             - if the lower bound is greater than upper bound
	 * @throws IllegalArgumentException
	 *             - if the lower bounds or the upper bounds is less than zero.
	 */
	public Multiplicity(int lowerBound, int upperBound)
	{
		if (lowerBound > upperBound)
			throw new IllegalArgumentException("lower bound can't be highter than upper bound");

		if (lowerBound < 0 || upperBound < 0)
			throw new IllegalArgumentException("lower bound and/or upper bound is lesser than 0");

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Return the lower bound for this multiplicity.
	 * 
	 * @return the lower bound for this multiplicity
	 */
	public int getLowerBound()
	{
		return lowerBound;
	}

	/**
	 * Convert the lower bound in a character. Every bounds are converted in
	 * this equivalent in string and infinite (define by the value
	 * Integer.MAX_VALUE) is represented by the car '*'.
	 * 
	 * @return the lower bound car for this multiplicity
	 */
	public String getLowerBoundChar()
	{
		return lowerBound == Integer.MAX_VALUE ? "*" : String.valueOf(lowerBound);
	}

	/**
	 * Return the upper bound for this multiplicity.
	 * 
	 * @return the upper bound for this multiplicity
	 */
	public int getUpperBound()
	{
		return upperBound;
	}

	/**
	 * Convert the upper bound in a character. Every bounds are converted in
	 * this equivalent in string and infinite (define by the value
	 * Integer.MAX_VALUE) is represented by the car '*'.
	 * 
	 * @return the upper bound car for this multiplicity
	 */
	public String getUpperBoundChar()
	{
		return upperBound == Integer.MAX_VALUE ? "*" : String.valueOf(upperBound);
	}

	@Override
	public void select()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Set the lower bound. If the upper bound is lower than new lower bound,
	 * upper bound will be changed to the new bound too.
	 * 
	 * @param bound
	 *            the new upper bound.
	 */
	public void setLowerBound(int bound)
	{
		if (upperBound < bound)
			setUpperBound(bound);

		lowerBound = bound;

		setChanged();
	}

	/**
	 * Set the upper bound. If the lower bound is highter than new upper bound,
	 * lower bound will be changed to the new bound too.
	 * 
	 * @param bound
	 *            the new upper bound.
	 */
	public void setUpperBound(int bound)
	{
		if (lowerBound > bound)
			setLowerBound(bound);

		upperBound = bound;

		setChanged();
	}

	@Override
	public String toString()
	{
		final String upperBoundChar = upperBound == Integer.MAX_VALUE ? "*" : String.valueOf(upperBound);

		if (lowerBound == upperBound)
			return upperBoundChar;

		else
			return getLowerBoundChar() + ".." + getUpperBoundChar();
	}

	@Override
	public String toXML(int depth)
	{
		final String tab = Utility.generateTab(depth);
		return tab + "<multiplicity>\n" + tab + "\t<min>" + getLowerBound() + "</min>\n" + tab + "\t<max>" + getUpperBound() + "</max>\n" + tab + "</multiplicity>";
	}
}
