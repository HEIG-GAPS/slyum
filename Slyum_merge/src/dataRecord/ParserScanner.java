package dataRecord;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ParserScanner
{
	private LinkedList<CompilationUnit> project = new LinkedList<CompilationUnit>();
	private Scanner pageScanner;
	private String theNextLine;
	private File fFile;
	private String extendsInfo = ""; // nomClasse,superclasse% and so on
	private String implInfo = "";
	private String attrInfo = "";  //  nom de classe d'appartenance, etype, nom attr,  asssoc%
	private String paramInfo = ""; // nomElement, nom memeber, nom param, etype,  asssoc%
	private String currentElemName = "";
	private String currentMembername = "";

	// private boolean isEnum = false;
	// private boolean isInterface = false;

	public void printDebug() 
	{
		 System.out.println("!!!!! DEBUG EXPORT");
		for (CompilationUnit unit : project)
		{
			System.out.println("///// Debut de: " + unit.getName());
			for (Element e : unit.getElements())
			{
				System.out.println(e);
			}
			System.out.println("///////////////////////////////////////////////////////\n");
		}
	}

	public LinkedList<CompilationUnit> getProjectFromFiles()
	{
		return project;
	}

	/**
	 * Constructor.
	 * 
	 * @param aFileName
	 *            full name of an existing, readable file.
	 */
	public ParserScanner(String aFileName)
	{
		fFile = new File(aFileName);

		try
		{
			if (fFile.isFile())
				project.add(processLineByLine(fFile));
			else
				parseFile(fFile);

			setParent();
			setInterfaces();
			setAttributes();
			setParametres();

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private void parseFile(File file) throws FileNotFoundException
	{
		File[] dir = file.listFiles();
		for (File f : dir)
		{
			if (f.isDirectory())
			{
				parseFile(f);
			} else
				project.add(processLineByLine(f));
		}
	}

	/** Template method that calls {@link #processLine(String)}. */
	public CompilationUnit processLineByLine(File file) throws FileNotFoundException
	{
		CompilationUnit unit = new CompilationUnit();
		// Note that FileReader is used, not File, since File is not Closeable
		pageScanner = new Scanner(new FileReader(file));
		try
		{
			// first use a Scanner to get each line
			while (pageScanner.hasNextLine())
			{
				theNextLine = pageScanner.nextLine();
				processLine(theNextLine, unit);
			}
		} finally
		{
			// ensure the underlying stream is always closed
			// this only has any effect if the item passed to the Scanner
			// constructor implements Closeable (which it does in this case).
			pageScanner.close();
		}
		unit.setFile(file);

		return unit;
	}

	/**
	 * Overridable method for processing lines in different ways.
	 * 
	 * @param <T>
	 * 
	 */
	protected void processLine(String aLine, CompilationUnit compil)
	{
		// use a second Scanner to parse the content of each line
		Scanner scanner = new Scanner(aLine);
		if (scanner.hasNext())
		{
			String key = scanner.next();
			if (key.contains("import"))
			{
				compil.addElement(buildImport(aLine));
			} else if (key.contains("package"))
			{
				compil.addElement(buildPackage(aLine));
			} else if (Pattern.matches("(\\{|\\s+\\{|\\}|\\s+\\}|\\s*)", aLine))
			{
				getBrackets();
			} else
				compil.addElement(getElementFromFile(aLine));
		}
		// no need to call scanner.close(), since the source is a String
	}

	private Element getElementFromFile(String aLine)
	{
		while (aLine.trim().isEmpty())
			// skip empty line
			aLine = pageScanner.nextLine();

		if (aLine.contains("class"))
		{
			return buildClass(aLine);
		} else if (aLine.contains("enum"))
		{
			return buildEnum(aLine);
		} else if (aLine.contains("interface"))
		{
			return buildInterface(aLine);
		} else if (aLine.contains("("))
		{
			return buildMember(aLine);
		}
		return builAttribute(aLine);
	}

	private void getBrackets()
	{

	}

	private PackageStmt buildPackage(String line)
	{
		String[] tab = line.split("\\s+");
		int index = tab[1].lastIndexOf(";");

		return new PackageStmt(tab[1].substring(0, index));
	}

	private ImportStmt buildImport(String line)
	{
		String[] tab = line.split("\\s+");

		if (tab[1].equals("static"))
		{
			int index = tab[2].lastIndexOf(";");
			return new ImportStmt(tab[2].substring(0, index), true);
		}
		int index = tab[1].lastIndexOf(";");
		return new ImportStmt(tab[1].substring(0, index), false);
	}

	private ClassType buildClass(String line)
	{
		theNextLine = pageScanner.nextLine();
		if (Pattern.matches("(\\{|\\s+\\{)", line))
			line = theNextLine;

		String[] tab = line.trim().split("\\s+");
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		String name = "";
		Keyword access = Keyword.PACKAGE;
		int i;

		for (i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("protected"))
				access = Keyword.PROTECTED;
			if (tab[i].equals("private"))
				access = Keyword.PRIVATE;
			if (tab[i].equals("public"))
				access = Keyword.PUBLIC;
			if (tab[i].equals("abstract"))
				isAbstract = true;
			if (tab[i].equals("static"))
				isStatic = true;
			if (tab[i].equals("final"))
				isFinal = true;
			if (tab[i].equals("class"))
			{
				name = tab[i + 1];
				break;
			}
		}
		i++;

		currentElemName = name;
		ClassType ct = new ClassType(name, access);
		ct.setAbstract(isAbstract);
		ct.setStatic(isStatic);
		ct.setFinal(isFinal);

		if (i + 1 < tab.length && tab[i + 1].equals("extends"))
		{
			extendsInfo += name;
			extendsInfo += ",";
			extendsInfo += tab[i + 2];
			extendsInfo += "%";
			i += 2;
		}

		if (i + 1 < tab.length && tab[i + 1].equals("implements"))
		{
			implInfo += name;

			for (i++; i < tab.length - 1; i++)
			{
				implInfo += ",";
				implInfo += tab[i + 1].replace(",", "");
			}

			implInfo += "%";
		}

		theNextLine = pageScanner.nextLine();
		while (!theNextLine.contains("}"))
		{
			ct.addElement(getElementFromFile(theNextLine));
			theNextLine = pageScanner.nextLine();
		}
		return ct;
	}

	private EnumType buildEnum(String line)
	{
		theNextLine = pageScanner.nextLine();
		if (Pattern.matches("(\\{|\\s+\\{)", line))
			line = theNextLine;

		String[] tab = line.split("\\s+");
		boolean isStatic = false;
		String name = "";
		Keyword access = Keyword.PACKAGE;

		for (int i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("protected"))
				access = Keyword.PROTECTED;
			if (tab[i].equals("private"))
				access = Keyword.PRIVATE;
			if (tab[i].equals("public"))
				access = Keyword.PUBLIC;
			if (tab[i].equals("static"))
				isStatic = true;
			if (tab[i].equals("enum"))
			{
				name = tab[i + 1];
				break;
			}
		}

		currentElemName = name;
		EnumType et = new EnumType(name, access);
		et.setStatic(isStatic);

		theNextLine = pageScanner.nextLine();
		while (!theNextLine.contains("}"))
		{
			et.addElement(getElementFromFile(theNextLine));
			theNextLine = pageScanner.nextLine();
		}

		return et;
	}

	private InterfaceType buildInterface(String line)
	{
		theNextLine = pageScanner.nextLine();
		if (Pattern.matches("(\\{|\\s+\\{)", line))
			line = theNextLine;

		String[] tab = line.split("\\s+");
		String name = "";
		Keyword access = Keyword.PACKAGE;

		for (int i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("protected"))
				access = Keyword.PROTECTED;
			if (tab[i].equals("private"))
				access = Keyword.PRIVATE;
			if (tab[i].equals("public"))
				access = Keyword.PUBLIC;
			if (tab[i].equals("interface"))
			{
				name = tab[i + 1];
				break;
			}
		}

		currentElemName = name;
		InterfaceType it = new InterfaceType(name, access);

		theNextLine = pageScanner.nextLine();
		while (!theNextLine.contains("}"))
		{
			it.addElement(getElementFromFile(theNextLine));
			theNextLine = pageScanner.nextLine();
		}

		return it;
	}

	private Member buildMember(String line)
	{
		// method regex =
		// (public|protected|private|\s|static|final|abstract|synchronized)*
		// +[\w\<\>\[\]]+\s+(\w+) *\([^\)]*\) *(\{?|[^;])
		String[] tab = line.trim().split("\\s+");
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isFinal = false;
		boolean isSync = false;
		Keyword access = Keyword.PACKAGE;
		int i;

		for (i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("protected"))
				access = Keyword.PROTECTED;
			else if (tab[i].equals("private"))
				access = Keyword.PRIVATE;
			else if (tab[i].equals("public"))
				access = Keyword.PUBLIC;
			else if (tab[i].equals("abstract"))
				isAbstract = true;
			else if (tab[i].equals("static"))
				isStatic = true;
			else if (tab[i].equals("final"))
				isFinal = true;
			else if (tab[i].equals("synchronized"))
				isSync = true;
			else
			{
				// System.out.println( i +" sortie " + tab[i]);
				break;
			}
		}
		if (line.contains(";"))
			line = line.replace(";", "");

		Member m = null;
		// if
		// (Pattern.matches("(public|protected|private|\\s|static|final|abstract|synchronized)* [\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\)*(\\s+|\\w|,)*\\{?\\}?",
		// line))
		if (!Pattern.matches("(public|protected|private|\\s)*[A-Z]+\\w*\\s*\\(+.*", line))
		{
			int index = tab[i + 1].indexOf("(");
			if (index == -1)
				m = new Method(tab[i + 1], access, tab[i]);
			else
				m = new Method(tab[i + 1].substring(0, index), access, tab[i]);
			((Method) m).setAbstract(isAbstract);
			((Method) m).setStatic(isStatic);
			((Method) m).setFinal(isFinal);
			((Method) m).setSynchronized(isSync);
			// System.out.println( "M:"+line+"--"+m);
		} else
		// if(Pattern.matches("(public|protected|private|\\s)* [\\w\\<\\>\\[\\]]+\\s+ *\\([^\\)]*\\)*(\\s+|\\w|,)*\\{?\\}?",
		// line))
		{
			int index = tab[i].indexOf("(");
			if (index == -1)
				m = new Constructor(tab[i], access);
			else
				m = new Constructor(tab[i].substring(0, index), access);
			// System.out.println("C:"+line+"--"+m);
		}
		// System.out.println("D:"+line+m);
		currentMembername = m.getName();
		
		String params = (String) line.subSequence(line.indexOf("(") + 1, line.indexOf(")"));

		if (params.length() != 0)
			m.setParams(buildParams(params));

		String excpt = (String) line.substring(line.indexOf(")") + 1);

		if (excpt.length() > 3)
		{
			excpt = excpt.replace("throws", "");
			String[] tabExcpt = excpt.trim().split(",");
			for (int j = 0; j < tabExcpt.length; j++)
			{
				m.getThrowClauses().add(tabExcpt[j]);
			}
		}
		
		return m;
	}

	private List<Parametre> buildParams(String params)
	{
		List<Parametre> tmp = new LinkedList<Parametre>();

		// plusieurs params
		if (params.contains(","))
		{
			String[] tab = params.trim().split(",");

			// parcourir tous les params
			for (int i = 0; i < tab.length; i++)
			{
				tmp.add(buildParam(tab[i]));
			}
		} else
			// un seul param
			tmp.add(buildParam(params));

		return tmp;
	}

	private enum Association {ONE, N_FIXED, ONE_MANY}

	private Parametre buildParam(String aParam)
	{
		Association ass;
		boolean isEllipse = false;
		int dimension = 0;

		if (aParam.contains("..."))
		{
			ass = Association.N_FIXED;
			aParam = aParam.replace("...", "");
			isEllipse = true;
		} else if (aParam.contains("<"))
			ass = Association.ONE_MANY;
		else
			ass = Association.ONE;

		while (aParam.contains("["))
		{
			ass = Association.N_FIXED;
			aParam = aParam.replaceFirst("\\[", "");
			aParam = aParam.replaceFirst("\\]", "");
			dimension++;
		}

		String[] smalltab = aParam.trim().split("\\s+");
		boolean isFinal = smalltab[0].equals("final");
		int index = isFinal ? 1 : 0;

		Parametre p = null;
		switch (ass)
		{
		case ONE:
			if (isPrimitive(smalltab[index]))
				p = new Parametre(smalltab[index + 1], getPrimitive(smalltab[index]));
			else if (smalltab[index] == "String")
			{
				try
				{
					p = new Parametre(smalltab[index + 1], new APIclass(Class.forName("java.lang.String"), "String"));
				} 
				catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				p = new Parametre(smalltab[index+1], PrimitiveType.BYTE);
				paramInfo += currentElemName + ",";
				paramInfo += currentMembername + ",";
				paramInfo += smalltab[index+1] + ","; // the param name
				paramInfo += smalltab[index] + ","; // the param type
				paramInfo += ass + "%";
			}
			break;

		case N_FIXED:
			if (isPrimitive(smalltab[index]))
				p = new Parametre(smalltab[index + 1], new ArrayType(getPrimitive(smalltab[index]), dimension, isEllipse));
			else if (smalltab[index] == "String")
			{
				try
				{
					p = new Parametre(smalltab[index + 1], new ArrayType(new APIclass(Class.forName("java.lang.String"),"String"),dimension,isEllipse));
				} 
				catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				p = new Parametre(smalltab[index + 1], new ArrayType(dimension, isEllipse));
				paramInfo += currentElemName + ",";
				paramInfo += currentMembername + ",";
				paramInfo += smalltab[index + 1] + ",";
				paramInfo += smalltab[index] + ",";
				paramInfo += ass + "%";
			}
			break;
		case ONE_MANY:
			String t = (String) aParam.subSequence(aParam.indexOf("<") + 1, aParam.indexOf(">"));
			if (t.trim() == "String")
			{
				try
				{
					p = new Parametre(aParam.substring(aParam.indexOf(">") + 2), new ListType(new APIclass(Class.forName("java.lang.String"), "String")));
				}
				catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				p = new Parametre(aParam.substring(aParam.indexOf(">") + 2), new ListType());
				paramInfo += currentElemName + ",";
				paramInfo += currentMembername + ",";
				paramInfo += smalltab[index + 1] + ",";
				paramInfo += t + ",";
				paramInfo += ass + "%";
			}
			
		}

		if (aParam.contains("="))
			p.setValue(aParam.substring(aParam.indexOf("=") + 1));

		return p;
	}

	private Attribute builAttribute(String aline)
	{
		Keyword access = Keyword.PACKAGE;
		boolean isStatic = false;
		boolean isFinal = false;
		int index = 0;
		Association ass;
		int dims = 0;

		String line = aline.replace(";", "");

		if (line.contains("protected"))
		{
			access = Keyword.PROTECTED;
			index++;
		} else if (line.contains("private"))
		{
			access = Keyword.PRIVATE;
			index++;
		} else if (line.contains("public"))
		{
			access = Keyword.PUBLIC;
			index++;
		}
		if (line.contains("static"))
		{
			isStatic = true;
			index++;
		}
		if (line.contains("final"))
		{
			isFinal = true;
			index++;
		}
		if (line.contains("<"))
			ass = Association.ONE_MANY;
		else
			ass = Association.ONE;
		while (line.contains("["))
		{
			ass = Association.N_FIXED;
			line = line.replaceFirst("\\[]", "");
			dims++;
		}

		String[] smalltab = line.trim().split("\\s+");
		Attribute a = null;

		if (smalltab.length == 1) // enum field
			a = new Attribute(smalltab[index], Keyword.PACKAGE, new EnumType(smalltab[index],Keyword.PACKAGE)); //TODO a refaire

		else if (ass.equals(Association.ONE_MANY))
		{
			String t = (String) line.subSequence(line.indexOf("<") + 1, line.indexOf(">"));
			if (t.equals("String"))
			{
				try{
					a = new Attribute(smalltab[index + 1], access, new ListType(new APIclass(Class.forName("java.lang.String"),"String")));
				}catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				a = new Attribute(line.substring(line.indexOf(">") + 2), access, new ListType());
				attrInfo += currentElemName + ",";
				attrInfo += t + ","; // the type to set
				attrInfo += line.substring(line.indexOf(">") + 2) + ","; //the name
				attrInfo += ass + "%";
			}
			
		}
		else if (ass.equals(Association.N_FIXED))
		{
			if (isPrimitive(smalltab[index]))
				a = new Attribute(smalltab[index + 1], access, new ArrayType(getPrimitive(smalltab[index]), dims));
			else if (smalltab[index].equals("String"))
			{
				try
				{
					a = new Attribute(smalltab[index + 1], access, new ArrayType(new APIclass(Class.forName("java.lang.String"), "String"),dims));
				}catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				a = new Attribute(smalltab[index + 1], access, new ArrayType(dims, false));
				attrInfo += currentElemName + ",";
				attrInfo += smalltab[index]+ ",";
				attrInfo += smalltab[index+1]+ ",";
				attrInfo += ass + "%";
			}
		}
		else // ass = one
		{
			if (isPrimitive(smalltab[index]))
				a = new Attribute(smalltab[index + 1], access, getPrimitive(smalltab[index]));
			else if (smalltab[index].equals("String"))
			{
				try
				{
					a = new Attribute(smalltab[index + 1], access, new APIclass(Class.forName("java.lang.String"), "String"));
				}catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				a = new Attribute(smalltab[index + 1], access, PrimitiveType.BYTE);
				attrInfo += currentElemName + ",";
				attrInfo += smalltab[index]+ ",";
				attrInfo += smalltab[index+1]+ ",";
				attrInfo += ass + "%";
			}
		}
		
		a.setFinal(isFinal);
		a.setStatic(isStatic);

		if (line.contains("="))
			a.setValue(line.substring(line.indexOf("=") + 1));

		return a;
	}

	private boolean isPrimitive(String key)
	{
		return key.equals("int") || key.equals("boolean") || key.equals("short") || key.equals("long")
				|| key.equals("float") || key.equals("double") || key.equals("char") || key.equals("byte");
	}

	private PrimitiveType getPrimitive(String p)
	{
		if (p.equals("int"))
			return PrimitiveType.INT;
		if (p.equals("boolean"))
			return PrimitiveType.BOOLEAN;
		if (p.equals("short"))
			return PrimitiveType.SHORT;
		if (p.equals("long"))
			return PrimitiveType.LONG;
		if (p.equals("float"))
			return PrimitiveType.FLOAT;
		if (p.equals("double"))
			return PrimitiveType.DOUBLE;
		if (p.equals("char"))
			return PrimitiveType.CHAR;
		return PrimitiveType.BYTE;
	}

	private ClassType getClassType(String name)
	{
		for (CompilationUnit cuts : project)
		{
			for (Element e : cuts.getElements())
			{
				if (e.getClass() == ClassType.class)
					if (((ClassType) e).getName().equals(name))
						return (ClassType) e;
			}
		}
		return null;
	}

	private InterfaceType getInterface(String name)
	{
		for (CompilationUnit cuts : project)
		{
			for (Element e : cuts.getElements())
			{
				if (e.getClass() == InterfaceType.class)
					if (((InterfaceType) e).getName().equals(name))
						return (InterfaceType) e;
			}
		}
		return null;
	}

	private void setParent()
	{
		String[] tabAT = extendsInfo.split("%");

		try
		{
			for (int i = 0; i < tabAT.length; i++)
			{
				String[] tabColumn = tabAT[i].split(",");
				// add the super class to his child
				getClassType(tabColumn[0]).getExtendList().add(getClassType(tabColumn[1]));
			}

		} catch (Exception e)
		{
			// TODO: look to imports must be a java api class
			System.err.println("error setParent");
		}
	}

	private void setInterfaces()
	{
		String[] tabAT = implInfo.split("%");

		try
		{
			for (int i = 0; i < tabAT.length; i++)
			{
				String[] tabColumn = tabAT[i].split(",");
				// add the interface(s)
				for (int j = 1; j < tabColumn.length; j++)
				{
					getClassType(tabColumn[0]).getImplList().add(getInterface(tabColumn[j]));
				}
			}

		} catch (Exception e)
		{
			// TODO: look to imports must be a java api interface
			System.err.println("error Interface");
		}
	}
	
	private Element getElementFromProject(String name)
	{
		for (CompilationUnit cuts : project)
		{
			for (Element e : cuts.getElements())
			{
				if (e.getName().equals(name))
						return e;
			}
		}
		return null;
	}
	
	private void setAttributes()
	{
		String[] tabAT = attrInfo.split("%");
		
		// [0]: nom de classe d'appartenance, [1]: etype, [2]: nom attr, [3]:  asssoc%
		try
		{
			for (int i = 0; i < tabAT.length; i++)
			{
				String[] tabColumn = tabAT[i].split(",");
				
				for (int j = 1; j < tabColumn.length; j++)
				{
					Type from = (Type)getElementFromProject((tabColumn[0]));
					ElementType madeOf = (ElementType)getElementFromProject((tabColumn[1]));
					Attribute ax = (Attribute)from.getElement(tabColumn[2]);
					
					if(tabColumn[3].equals("ONE"))
						ax.setType(madeOf);
					else if(tabColumn[3].equals("N_FIXED"))
						((ArrayType)ax.getType()).seteType(madeOf);
					else 
						((ListType)ax.getType()).seteType(madeOf);
					
					//System.out.println("attr:" +ax);
				}
			}

		} catch (Exception e)
		{
			// TODO: look to imports must be a java api whatever
			System.err.println("error Attributes");
		}
	}
	
	private void setParametres()
	{
		String[] tabAT = paramInfo.split("%");
		
		// [0]: nom de classe d'appartenance, [1]: nom de la methode, [2]: nom param, [3]:  etype, [4]: assoc%
		try
		{
			for (int i = 0; i < tabAT.length; i++)
			{
				String[] tabColumn = tabAT[i].split(",");
				
				for (int j = 1; j < tabColumn.length; j++)
				{
					Type tx = (Type)getElementFromProject((tabColumn[0]));
					ElementType madeOf = (ElementType)getElementFromProject((tabColumn[3]));
					Member mx = (Member)tx.getElement(tabColumn[1]);
					Parametre px = null;
					for (Parametre p : mx.getParams()) 
					{
						if(p.getName().equals(tabColumn[2]))
						{
							px = p;
							break;
						}
					}
					
					if(tabColumn[4].equals("ONE"))
						px.setType(madeOf);
					else if(tabColumn[4].equals("N_FIXED"))
						((ArrayType)px.getType()).seteType(madeOf);
					else 
						((ListType)px.getType()).seteType(madeOf);
					
					//System.out.println("param:" +px);
				}
			}
			

		} catch (Exception e)
		{
			// TODO: look to imports must be a java api whatever
			System.err.println("error Params");
		}
	}
}
