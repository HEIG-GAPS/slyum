package classDiagram.components;

/**
 * Represent a visibility in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public enum Visibility
{
	PACKAGE("Package", '~'), // Enum name must be in UPPER CASE and same as the
	// first argument (the name)
	// in lower
	// case (except the first car in CAPITAL)!! (otherwise toValues method will
	// not worked)
	PRIVATE("Private", '-'), PROTECTED("Protected", '#'), PUBLIC("Public", '+');

	/**
	 * Return the visibility corresponding to the car given in parameter or null
	 * if no visibility corresponding.
	 * 
	 * @param car
	 *            the car to find in visibility enum.
	 * @return the visibility corresponding to car.
	 */
	public static Visibility getVisibility(char car)
	{
		switch (car)
		{
			case '-':
				return PRIVATE;
			case '+':
				return PUBLIC;
			case '#':
				return PROTECTED;
			case '~':
				return PACKAGE;
			default:
				return null;
		}
	}

	private char car;
	private String name;

	/**
	 * Create a new visibility with the given name and car.
	 * 
	 * @param name
	 *            the name of the visibility
	 * @param car
	 *            the car representing the visibility in UML.
	 */
	private Visibility(String name, char car)
	{
		this.name = name;
		this.car = car;
	}

	/**
	 * Get the name of this visibility.
	 * 
	 * @return the name of this visibility
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the car for this visibility. Car is the character representing the
	 * visibility in UML and can be + : PUBLIC - : PRIVATE # : PROTECTED ~ :
	 * PACKAGE
	 * 
	 * @return
	 */
	public char toCar()
	{
		return car;
	}
}
