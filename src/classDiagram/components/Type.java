package classDiagram.components;

import java.util.LinkedList;
import java.util.Observable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;
import classDiagram.verifyName.TypeName;

/**
 * Represent a type in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Type extends Observable implements IDiagramComponent
{
    public static final String accents = "¿‡¡·¬‚√„ƒ‰≈Â“Ú”Û‘Ù’ı÷ˆÿ¯»Ë…È ÍÀÎ«ÁÃÏÕÌŒÓœÔŸ˘⁄˙€˚‹¸—Ò";
    public static final String CARACTERES_VALID = "a-zA-Z_" + accents;
    public final static String REGEX_DIGIT = "[0-9]*";
    private final static String REGEXP_GENERIC = 
        Variable.REGEX_SEMANTIC_ATTRIBUTE + 
        "(<("+Variable.REGEX_SEMANTIC_ATTRIBUTE+")(,\\s*("+Variable.REGEX_SEMANTIC_ATTRIBUTE+"))*>)?";
    private final static String REGEXP_GENERIC_2 = 
        "(<("+REGEXP_GENERIC+")(,\\s*("+REGEXP_GENERIC+"))*>)";
    public final static String REGEX_SEMANTIC_TYPE = 
        Variable.REGEX_SEMANTIC_ATTRIBUTE + 
        REGEXP_GENERIC_2 + "?((\\["+REGEX_DIGIT+"])*)*";
	
	public static boolean checkSemantic(String type) {
	    return type.matches(REGEX_SEMANTIC_TYPE);
	}
	
	protected final int id;

	protected String name = "void";
	
	LinkedList<Integer> arraysSize = new LinkedList<>();

	/**
	 * Create a new type with the specified name.
	 * 
	 * @param name
	 *            the name of the type
	 */
	public Type(String name)
	{
		initialize(name);
		
		id = ClassDiagram.getNextId();
	}

	/**
	 * Create a new type with the specified name and id.
	 * 
	 * @param name
	 *            the name of the type
	 * @param id
	 *            the id of the type
	 */
	public Type(String name, int id)
	{
		initialize(name);
		
		this.id = id;
	}
	
	private void initialize(String name)
	{
		if (!TypeName.getInstance().verifyName(name))
			throw new IllegalArgumentException("semantic incorrect");
		
		boolean isBlocked = Change.isBlocked();
		Change.setBlocked(true);
		
		setName(name);
		
		Change.setBlocked(isBlocked);
	}

	@Override
	public int getId()
	{
		return id;
	}

	/**
	 * Get the name of the type.
	 * 
	 * @return the name of the type
	 */
	public String getName()
	{
		String n = name;		
		
		for (Integer i : arraysSize)
		
			n += "[" + (i < 1 ? "" : i) + "]";
		
		return n;
	}

	@Override
	public void select()
	{
		setChanged();
	}

	/**
	 * Set the name for this type.
	 * 
	 * @param name
	 *            the new name.
	 */
	public boolean setName(String name)
	{
		if (!TypeName.getInstance().verifyName(name))
			return false;

		int state = 0; // 0 = name, 1 = array
		String buff = "", n = "";
		LinkedList<Integer> a = new LinkedList<>();
		char c;
		for (int i = 0; i < name.length(); i++)
		{
			c = name.charAt(i);
			switch (state)
			{
			case 0:				
				if (c == '[')
				{
					n = buff;
					buff = "";
					state = 1;
					continue;
				}
				else if (i == name.length()-1)
				{
					n = buff + name.charAt(i);
					continue;
				}

				buff += name.charAt(i);
				break;
				
			case 1:
				
				if (c == ']')
				{
					int s = -1;
					if (!buff.isEmpty())
						s = Integer.parseInt(buff);
					
					a.add(s);
					buff = "";
					continue;
				}
				else if (c == '[')
				{
					// Only last dimension can be empty.
					if (a.getLast() == -1)
						return false;
					
					buff = "";
					
					continue;
				}
				else if (!String.valueOf(c).matches(REGEX_DIGIT))
                    
                    return false;
				
				buff += name.charAt(i);
				break;
			}
		}

        if (name.equals(getName()) && arraysSize.containsAll(a))
            return false;
		
		arraysSize = a;

		this.name = n;

		setChanged();

		return true;
	}
	
	/**
	 * Return if this type represent an array.
	 * @return if this type represent an array; false otherwise.
	 */
	public boolean isArray()
	{
		return arraysSize.size() > 0;
	}
	
	/**
	 * Return the number of dimension of this type, or 0 if the type
	 * doesn't represent an array.
	 * @return the number of dimension of this type, or 0 if the type
	 * doesn't represent an array.
	 */
	public int nbDimension()
	{
		return arraysSize.size();
	}
	
	/**
	 * Return a LinkedList containing the size of the dimensions in the array.
	 * If the LinkedList is empty, that means this type doesn't represent an array.
	 * The number -1 means the size is left blank (only possible in last dimension).
	 * @return sizes of dimensions
	 */
	@SuppressWarnings("unchecked")
	public LinkedList<Integer> getDimensions()
	{
		return (LinkedList<Integer>) arraysSize.clone();
	}

	@Override
	public String toString() {
		return getName();
	}

  @Override
  public Element getXmlElement(Document doc) {
    return null;
  }

  @Override
  public String getXmlTagName() {
    return null;
  }
}
