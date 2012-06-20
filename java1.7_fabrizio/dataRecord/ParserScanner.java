package dataRecord;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import swing.Slyum;
import utility.SMessageDialog;
import utility.Utility;

public class ParserScanner
{
	private final ProjectManager project = ProjectManager.getInstance();
	private Scanner pageScanner;
	private String theNextLine;
	private File fFile;
	private String extendsInfo = ""; // nomClasse,superclasse% and so on
	private String implInfo = "";
	private String attrInfo = "";  
	private String paramInfo = ""; 
	private int currentElemId;
	private int currentMemberID;
	private boolean isInterface = false;

	public void printDebug() 
	{
		System.out.println("!!!!! DEBUG");
		for (CompilationUnit unit : project.getFilesRecord())
		{
			System.out.println("///// Debut de: " + unit.getName());
			for (Element e : unit.getElements())
			{
				System.out.println(e);
			}
			System.out.println("///////////////////////////////////////////////////////\n");
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param aFileName
	 *            full name of an existing, readable file.
	 */
	public ParserScanner(File[] files)
	{
		try
		{
			for (File file : files)
			{
				fFile = new File(file.getPath());

					if (fFile.isFile())
						project.getFilesRecord().add(processLineByLine(fFile));
					else
						parseFile(fFile);
			}
			
			setParent();
			setInterfaces();
			setAttributes();
			setParametres();
			
		} catch (Exception e)
		{
			//SMessageDialog.showErrorMessage("file is not readable");
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
			} 
			else 
			{
				if(Utility.getExtension(f).equals(Slyum.JAVA_EXTENSION))
					project.getFilesRecord().add(processLineByLine(f));
			}
				
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
		}
		catch(Exception e)
		{
			System.err.println("file: " + file.getName() + " could not be parsed");
			SMessageDialog.showErrorMessage("file: " + file.getName() + " could not be parsed");
			unit.setFile(file);
			return unit;
		}
		finally
		{
			System.out.println(file.getName());
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
	 * @throws Exception 
	 * 
	 */
	protected void processLine(String aLine, CompilationUnit compil) throws Exception
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
				//compil.addElement(getBrackets(aLine));
			} else
			{
				try
				{
					compil.addElement(getElementFromFile(aLine));
				} catch (Exception e)
				{
					throw new Exception();
				}
			}
				
		}
		// no need to call scanner.close(), since the source is a String
	}

	private Element getElementFromFile(String aLine)
	{
		while (aLine.trim().isEmpty() || aLine.contains("@")) // skip empty line
			aLine = pageScanner.nextLine();

		if (Pattern.matches(".*\\s*class\\s+.*", aLine))
		{
			return buildClass(aLine);
		} if (Pattern.matches(".*\\s*enum\\s+.*", aLine))
		{
			return buildEnum(aLine);
		} if (Pattern.matches(".*\\s*interface\\s+.*", aLine))
		{
			return buildInterface(aLine);
		} else if (aLine.contains("("))
		{
			return buildMember(aLine);
		}else if (aLine.contains("}"))
		{
			return null;
		}else if (aLine.contains("//") || aLine.contains("/*"))
			return buildComment(aLine);

		return builAttribute(aLine);
	}

	private Attribute getBrackets(String aLine)
	{
		return builAttribute(aLine);
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

		ClassType ct = new ClassType(name, access);
		ct.setAbstract(isAbstract);
		ct.setStatic(isStatic);
		ct.setFinal(isFinal);
		currentElemId = ct.getID();

		if (i + 1 < tab.length && tab[i + 1].equals("extends"))
		{
			extendsInfo += ct.getID();
			extendsInfo += ",";
			extendsInfo += tab[i + 2];
			extendsInfo += "%";
			i += 2;
		}

		if (i + 1 < tab.length && tab[i + 1].equals("implements"))
		{
			implInfo += ct.getID();

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
			Element elementToAdd = getElementFromFile(theNextLine);
			if (elementToAdd == null)
				break;
			else
				ct.addElement(elementToAdd);
			
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

		
		EnumType et = new EnumType(name, access);
		et.setStatic(isStatic);
		currentElemId = et.getID();
		
		theNextLine = pageScanner.nextLine();
		while (!theNextLine.contains("}"))
		{
			Element elementToAdd = getElementFromFile(theNextLine);
			if (elementToAdd == null)
				break;
			else
				et.addElement(elementToAdd);

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

		
		InterfaceType it = new InterfaceType(name, access);
		currentElemId = it.getID();
		isInterface = true;
		
		theNextLine = pageScanner.nextLine();
		while (!theNextLine.contains("}"))
		{
			
			Element elementToAdd = getElementFromFile(theNextLine);
			if (elementToAdd == null)
				break;
			else
				it.addElement(elementToAdd);

			theNextLine = pageScanner.nextLine();
			
		}
		isInterface = false;
		
		return it;
	}
	
	private Comment buildComment(String line)
	{
		line = line.trim();
		if (line.contains("//"))
		{
			return new Comment(line, false);	
		}
		else
		{
			if (line.contains("*/"))
				return new Comment(line, true);
			else
			{
				theNextLine = pageScanner.nextLine();
				line += "\n";
				while(true)
				{
					if(theNextLine.contains("*/"))
					{
						line += theNextLine;
						break;
					}
					line += theNextLine + "\n";
					theNextLine = pageScanner.nextLine();
				}	
				return new Comment(line, true);
			}
		}
	}

	private Member buildMember(String line)
	{
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

		// it's a method
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
		} 
		else // it's a constructor
		{
			int index = tab[i].indexOf("(");
			if (index == -1)
				m = new Constructor(tab[i], access);
			else
				m = new Constructor(tab[i].substring(0, index), access);
			// System.out.println("C:"+line+"--"+m);
		}
		// System.out.println("D:"+line+m);
		currentMemberID = m.getID();
		
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
		
		
		if (!isAbstract && !isInterface)
		{
			m.methodBody = "";
			
			//System.out.println("meth line: "+line);
			
			int brackets = 0;
			
			if(line.contains("{"))
			{
				brackets++;
				m.methodBody += "{";
			}
			if(line.contains("}"))
			{
				brackets--;
				m.methodBody += "}";
			}
			
			theNextLine = pageScanner.nextLine();
			do
			{
				if (theNextLine.contains("{"))
					brackets++;
				if (theNextLine.contains("}"))
					brackets--;	
				
				m.methodBody += theNextLine;
				m.methodBody += "\n";
				if(brackets == 0)
					break;
				else
					theNextLine = pageScanner.nextLine();
			}while(true);
		}
		else
			m.methodBody =";";
		
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
		aParam = aParam.trim();
		Association ass;
		boolean isEllipse = false;
		int dimension = 0;

		if (aParam.contains("..."))
		{
			ass = Association.N_FIXED;
			aParam = aParam.replace("...", "");
			isEllipse = true;
		} 
		else if (aParam.contains("<"))
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
				paramInfo += currentElemId + ",";
				paramInfo += currentMemberID + ",";
				paramInfo += p.getID() + ","; // the param name
				paramInfo += smalltab[index] + ","; // the param type
				paramInfo += ass + "%";
			}
			break;

		case N_FIXED:
			if (isPrimitive(smalltab[index]))
				p = new Parametre(smalltab[index + 1], new ArrayType(getPrimitive(smalltab[index]), dimension, isEllipse));
			
			else if (smalltab[index].equals("String"))
			{
				try
				{
					p = new Parametre(smalltab[index + 1], new ArrayType(new APIclass(Class.forName("java.lang.String"),"String"),dimension,isEllipse));
				} 
				catch (ClassNotFoundException e1){e1.printStackTrace();}
			}
			else
			{
				p = new Parametre(smalltab[index + 1], new ArrayType(PrimitiveType.BYTE,dimension, isEllipse));
				paramInfo += currentElemId + ",";
				paramInfo += currentMemberID + ",";
				paramInfo += p.getID() + ",";
				paramInfo += smalltab[index] + ",";
				paramInfo += ass + "%";
			}
			break;
		case ONE_MANY:
			//System.out.println("param is a list");
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
				p = new Parametre(aParam.substring(aParam.indexOf(">") + 2), new ListType(PrimitiveType.BYTE));
				paramInfo += currentElemId + ",";
				paramInfo += currentMemberID + ",";
				paramInfo += p.getID() + ",";
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
				attrInfo += currentElemId + ",";
				attrInfo += t + ","; // the type to set
				attrInfo += a.getID()+ ",";
				attrInfo += ass + "%";
			}
			
			if (isCollection(line))
				setCollectionType(line, a);
			
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
				attrInfo += currentElemId + ",";
				attrInfo += smalltab[index]+ ",";
				attrInfo += a.getID()+ ",";
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
				attrInfo += currentElemId + ",";
				attrInfo += smalltab[index]+ ",";
				attrInfo += a.getID()+ ",";
				attrInfo += ass + "%";
			}
		}
		
		a.setFinal(isFinal);
		a.setStatic(isStatic);

		if (line.contains("="))
			a.setValue(line.substring(line.indexOf("=") + 1));

		return a;
	}

	private void setCollectionType(String line, Attribute a) 
	{
		ListType lt = (ListType) a.getType();
		
		for (Collection c : Collection.values())
		{
			if(line.matches(".*\\D\\W"+c+".*"))
				lt.setCollection(c);
		}
	}

	private boolean isCollection(String line)
	{
		line = line.toLowerCase();
		
		for (Collection c : Collection.values())
		{
			if(line.contains(c.toString().toLowerCase()))
				return true;
		}
		
		return false;
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
		for (CompilationUnit cuts : project.getFilesRecord())
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
		for (CompilationUnit cuts : project.getFilesRecord())
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
			if(!extendsInfo.isEmpty())
			for (int i = 0; i < tabAT.length; i++)
			{
				String[] tabColumn = tabAT[i].split(",");
				// add the super class to his child
				if(getClassType(tabColumn[1]) == null)
					((ClassType) project.getElementByID(Integer.valueOf(tabColumn[0]))).getExtendList().add(new APIclass(Object.class, tabColumn[1]));
				else
					((ClassType) project.getElementByID(Integer.valueOf(tabColumn[0]))).getExtendList().add(getClassType(tabColumn[1]));
			}

		} catch (Exception e)
		{
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
					if(getInterface(tabColumn[1]) == null)
						((ClassType) project.getElementByID(Integer.valueOf(tabColumn[0]))).getImplList().add(new APIinterface(Object.class, tabColumn[j]));
					else
						((ClassType) project.getElementByID(Integer.valueOf(tabColumn[0]))).getImplList().add(getInterface(tabColumn[j]));
				}
			}

		} catch (Exception e)
		{
			// TODO: look to imports must be a java api interface
			System.err.println("error Interface");
		}
	}
	
	private void setAttributes()
	{
		if (attrInfo.trim().isEmpty())
				return;
		
		String[] tabAT = attrInfo.split("%");
		
		// [0]: id de classe d'appartenance, [1]: etype, [2]: id attr, [3]:  asssoc%

		for (int i = 0; i < tabAT.length; i++)
		{
			String[] tabColumn = tabAT[i].split(",");
			
			for (int j = 1; j < tabColumn.length; j++)
			{
				try
				{
					Type from = (Type)project.getElementByID(Integer.valueOf(tabColumn[0]));
					ElementType madeOf = null; 
					Attribute ax = (Attribute)from.getElement(Integer.valueOf(tabColumn[2]));
					
					if ((madeOf = (ElementType)project.getElementFromProject(tabColumn[1])) == null);
						madeOf = new APIclass(Object.class, tabColumn[1]);
						
					if(tabColumn[3].equals("ONE"))
						ax.setType(madeOf);
					else if(tabColumn[3].equals("N_FIXED"))
						((ArrayType)ax.getType()).seteType(madeOf);
					else if(tabColumn[3].equals("ONE_MANY"))
						((ListType)ax.getType()).seteType(madeOf);	
				
				} catch (Exception e)
				{
					System.err.println("error Attributes "+ fFile.getName() + e.getMessage());		
				}
				
		
				//System.out.println("attr:" +ax);
			}
		}
	}
	
	private void setParametres()
	{
		if (paramInfo.isEmpty())
			return;
		
		String[] tabAT = paramInfo.split("%");
		
		// [0]: id de classe d'appartenance, [1]: id de la methode, [2]: id param, [3]:  etype, [4]: assoc%

		for (int i = 0; i < tabAT.length; i++)
		{
			String[] tabColumn = tabAT[i].split(",");

			Type tx = (Type)project.getElementByID(Integer.valueOf(tabColumn[0]));
			ElementType madeOf = (ElementType)project.getElementFromProject((tabColumn[3]));
			Member mx = (Member)tx.getElement(Integer.valueOf(tabColumn[1]));
	
			try
			{
				if (madeOf == null);
					madeOf = new APIclass(Object.class, tabColumn[3]);
			}
			catch(Exception e)
			{
				System.err.println("param error " + e.getMessage());
			}
			
			for (Parametre p : mx.getParams()) 
			{
				if(p.getID() == Integer.valueOf(tabColumn[2]))
				{
					if(tabColumn[4].equals("ONE"))
						p.setType(madeOf);
					else if(tabColumn[4].equals("N_FIXED"))
						((ArrayType)p.getType()).seteType(madeOf);
					else 
						((ListType)p.getType()).seteType(madeOf);
				}
			}
		}
	}
}
