package dataRecord.io;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import dataRecord.Keyword;
import dataRecord.ProjectManager;
import dataRecord.elementType.*;
import dataRecord.elements.*;

import utility.SMessageDialog;
import utility.Utility;
/**
 * This class scan one or many C++ source code file(s) to extract all the informations.
 * The informations are stored in several compilation unit (one compilation unit for file)
 * Once the work done it gives all the compilation units to the project manager.
 * The next step will be to display those compilation units, this work will be done by 
 * the class named : Layout
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 18-lug-2012
 *
 */
public class ParserCpp implements Parser
{
	private final ProjectManager project = ProjectManager.getInstance();
	private Scanner pageScanner;
	private String theNextLine;
	private File fFile;
	private String extendsInfo = ""; 
	private String attrInfo = "";  
	private String paramInfo = ""; 
	private int currentElemId;
	private int currentMemberID;
	private Keyword currentAccess = Keyword.PRIVATE;

	/**
	 * Constructor.
	 * 
	 */
	public ParserCpp()
	{
	}
	
	/**
	 * Extract all the file and add them to the project 
	 * 
	 * @param files
	 * @return the compilation units
	 */
	public LinkedList<CompilationUnit> parse(File[] files)
	{
		LinkedList<CompilationUnit> myUnits = new LinkedList<>();
		
		try
		{
			for (File file : files)
			{
				fFile = new File(file.getPath());

					if (fFile.isFile() && Utility.getExtension(fFile).equals("h"))
						myUnits.add(processLineByLine(fFile));
					else
						myUnits.addAll(parseFile(fFile));
			}
			
			project.getFilesRecord().addAll(myUnits);
			
			setParent();
			setAttributes();
			setParametres();
			
		} catch (Exception e)
		{
			//SMessageDialog.showErrorMessage("file is not readable");
		}
		
		return myUnits;
	}

	/**
	 * Extract the file recursively from sub diretories
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private LinkedList<CompilationUnit> parseFile(File file) throws FileNotFoundException
	{
		LinkedList<CompilationUnit> myUnits = new LinkedList<>();
		File[] dir = file.listFiles();
		for (File f : dir)
		{
			if (f.isDirectory())
			{
				myUnits.addAll(parseFile(f));
			} 
			else 
			{
				if(Utility.getExtension(f).equals("h"))
					myUnits.add(processLineByLine(f));
			}
		}
		return myUnits;
	}

	/**
	 * set the scanner to the file 
	 */
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
	 * Start scanning a line.
	 * Overridable method for processing lines in different ways.
	 * 
	 * @throws Exception 
	 */
	protected void processLine(String aLine, CompilationUnit compil) throws Exception
	{
		// use a second Scanner to parse the content of each line
		Scanner scanner = new Scanner(aLine);
		if (scanner.hasNext())
		{
			String key = scanner.next();
			if (key.contains("#"))
			{
				compil.addElement(buildImport(aLine));
			} else if (key.contains("namespace"))
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
		
		if (aLine.contains("public") && !Pattern.matches(".*\\s*class\\s+.*", aLine) && !Pattern.matches(".*\\s*enum\\s+.*", aLine))
		{
			currentAccess = Keyword.PUBLIC;
			aLine = pageScanner.nextLine();
		}
		else if (aLine.contains("private") && !Pattern.matches(".*\\s*class\\s+.*", aLine) && !Pattern.matches(".*\\s*enum\\s+.*", aLine))
		{
			currentAccess = Keyword.PRIVATE;
			aLine = pageScanner.nextLine();
		}
		else if (aLine.contains("protected") && !Pattern.matches(".*\\s*class\\s+.*", aLine) && !Pattern.matches(".*\\s*enum\\s+.*", aLine))
		{
			currentAccess = Keyword.PUBLIC;
			aLine = pageScanner.nextLine();
		}

		if (Pattern.matches(".*\\s*class\\s+.*", aLine))
		{
			return buildClass(aLine);
		} 
		else if (Pattern.matches(".*\\s*enum\\s+.*", aLine))
		{
			SMessageDialog.showInformationMessage("enum are not supported yet.");
			return null;
			//return buildEnum(aLine);
		}
		
		else if (aLine.contains("(") && !aLine.contains("="))
		{
			return buildMember(aLine);
		}
		else if (aLine.contains("}"))
		{
			return null;
		}
		else if (aLine.contains("//") || aLine.contains("/*"))
			return buildComment(aLine);
		
		return builAttribute(aLine);
	}

	@SuppressWarnings("unused")
	private Attribute getBrackets(String aLine)
	{
		return builAttribute(aLine);
	}

	private PackageStmt buildPackage(String line)
	{
		String[] tab = line.split("\\s+");
		int index = tab[tab.length-1].lastIndexOf(";");

		return new PackageStmt(tab[tab.length-1].substring(0, index));
	}

	private PreprocessorStmt buildImport(String line)
	{
		String[] tab = line.trim().split("\\s+");
		
		if (tab.length > 1)
			return new PreprocessorStmt(Keyword.IMPORT, tab[1], tab[0]);
			
		return new PreprocessorStmt(Keyword.IMPORT, "", tab[0]);
	}

	private ClassType buildClass(String line)
	{
		if(!line.contains("{"))
		{
			theNextLine = pageScanner.nextLine();
			while(!theNextLine.contains("{"))
			{
				line += theNextLine.trim();
				theNextLine = pageScanner.nextLine();
			}
		}
		
		String[] tab = line.trim().split("\\s+");
		boolean isAbstract = false;
		String name = "";
		Keyword access = Keyword.PUBLIC;
		int i;

		for (i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("virtual"))
				isAbstract = true;
			if (tab[i].equals("class"))
			{
				name = tab[i + 1];
				break;
			}
		}
		i++;

		ClassType ct = new ClassType(name, access);
		ct.setAbstract(isAbstract);
		currentElemId = ct.getID();

		if (line.contains(":"))
		{
			int index = line.indexOf(":");
			String ex = line.substring(index);
			ex = ex.replaceAll(",", " ");
			String[] tabex = ex.trim().split("\\s+");
			for(int e=0; e<tabex.length/2; e++)
			{
				switch(tabex[e])
				{
					case "private":
						ct.getExtendList().add(new ClassType(tabex[e++], Keyword.PRIVATE));
						break;
					case "public":
						ct.getExtendList().add(new ClassType(tabex[e++], Keyword.PUBLIC));
						break;
					case "protected":
						ct.getExtendList().add(new ClassType(tabex[e++], Keyword.PROTECTED));
						break;
				}
			}
			
		}
		
		if(!line.contains("}") && !theNextLine.contains("}"))
		{
			theNextLine = pageScanner.nextLine();
			while (!theNextLine.contains("}"))
			{
				if (theNextLine.contains("public"))
					currentAccess = Keyword.PUBLIC;
				else if (theNextLine.contains("private"))
					currentAccess = Keyword.PRIVATE;
				else if (theNextLine.contains("protected"))
					currentAccess = Keyword.PROTECTED;
				else if(!theNextLine.isEmpty())
				{
					Element elementToAdd = getElementFromFile(theNextLine);
					if (elementToAdd == null)
						break;
					else
						ct.addElement(elementToAdd);
				}
				theNextLine = pageScanner.nextLine();
			}
		}
		
		return ct;
	}

	@SuppressWarnings("unused")
	private EnumType buildEnum(String line)
	{
		return null;
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
		//if(line.contains("virtual"))
		//{
			if(!line.contains(";"))
			{
				theNextLine = pageScanner.nextLine();
				while(!theNextLine.contains(";"))
				{
					line += theNextLine.trim();
					theNextLine = pageScanner.nextLine();
				}
			}
		//}
//		else 
//		{
//			if(!line.contains("{"))
//			{
//				theNextLine = pageScanner.nextLine();
//				while(!theNextLine.contains("{"))
//				{
//					line += theNextLine.trim();
//					theNextLine = pageScanner.nextLine();
//				}
//			}
//		}
		
		int indexFinal = line.indexOf(")");
		boolean isFinal = false;
		String checkFinal = line.substring(indexFinal);
		if(checkFinal.contains("const"))
			isFinal = true;
		
		String[] tab = line.trim().split("\\s+");
		boolean isAbstract = false;
		boolean isStatic = false;
		boolean isSync = false;
		
		int i;

		for (i = 0; i < tab.length; i++)
		{
			if (tab[i].equals("virtual"))
				isAbstract = true;
			else if (tab[i].equals("static"))
				isStatic = true;
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
		if (!Pattern.matches("(virtual|\\s)*[A-Z]+\\w*\\s*\\(+.*", line))
		{
			int index = tab[i + 1].indexOf("(");
			if (index == -1)
				m = new Method(tab[i + 1], currentAccess, tab[i]);
			else
				m = new Method(tab[i + 1].substring(0, index), currentAccess, tab[i]);
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
				m = new Constructor(tab[i], currentAccess);
			else
				m = new Constructor(tab[i].substring(0, index), currentAccess);
			// System.out.println("C:"+line+"--"+m);
		}
		// System.out.println("D:"+line+m);
		currentMemberID = m.getID();
		
		String params = (String) line.subSequence(line.indexOf("(") + 1, line.indexOf(")"));

		if (params.length() != 0)
			m.setParams(buildParams(params));
		
		
//		if (!isAbstract)
//		{
//			m.setMethodBody("");
//			
//			//System.out.println("meth line: "+line);
//			
//			int brackets = 0;
//			
//			if(line.contains("{"))
//			{
//				brackets++;
//				m.setMethodBody(m.getMethodBody() + "{");
//			}
//			if(line.contains("}"))
//			{
//				brackets--;
//				m.setMethodBody(m.getMethodBody() + "}");
//			}
//
//			do
//			{
//				if (theNextLine.contains("{"))
//					brackets++;
//				if (theNextLine.contains("}"))
//					brackets--;	
//				
//				m.setMethodBody(m.getMethodBody() + theNextLine);
//				m.setMethodBody(m.getMethodBody() + "\n");
//				if(brackets == 0)
//					break;
//				else
//					theNextLine = pageScanner.nextLine();
//			}while(true);
//		}
//		else
		{
			m.setMethodBody(";");
		}
		
		return m;
	}

	private List<Parameter> buildParams(String params)
	{
		List<Parameter> tmp = new LinkedList<Parameter>();

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

	private Parameter buildParam(String aParam)
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
		boolean isFinal = smalltab[0].equals("const");
		int index = isFinal ? 1 : 0;

		Parameter p = null;
		switch (ass)
		{
		case ONE:
			if (isPrimitive(smalltab[index]))
				p = new Parameter(smalltab[index + 1], getPrimitive(smalltab[index]));
			else
			{
				p = new Parameter(smalltab[index+1], PrimitiveType.BYTE);
				paramInfo += currentElemId + ",";
				paramInfo += currentMemberID + ",";
				paramInfo += p.getID() + ","; // the param name
				paramInfo += smalltab[index] + ","; // the param type
				paramInfo += ass + "%";
			}
			break;

		case N_FIXED:
			if (isPrimitive(smalltab[index]))
				p = new Parameter(smalltab[index + 1], new ArrayType(getPrimitive(smalltab[index]), dimension, isEllipse));
			
			else
			{
				p = new Parameter(smalltab[index + 1], new ArrayType(PrimitiveType.BYTE,dimension, isEllipse));
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

			p = new Parameter(aParam.substring(aParam.indexOf(">") + 2), new ListType(PrimitiveType.BYTE));
			paramInfo += currentElemId + ",";
			paramInfo += currentMemberID + ",";
			paramInfo += p.getID() + ",";
			paramInfo += t + ",";
			paramInfo += ass + "%";
			
		}

		if (aParam.contains("="))
			p.setValue(aParam.substring(aParam.indexOf("=") + 1));

		return p;
	}

	private Attribute builAttribute(String aline)
	{
		boolean isStatic = false;
		boolean isFinal = false;
		int index = 0;
		Association ass;
		int dims = 0;
		String right = "";

		String line = aline.replace(";", "");
		String[] lr = line.split("=");
		// get the left side of the assignement
		line = lr[0];
		if (aline.contains("="))
			right = lr[1];
		
		if (line.contains("static"))
		{
			isStatic = true;
			index++;
		}
		if (line.contains("const"))
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
		String name = smalltab[smalltab.length-1];
		String elemtypename = line.trim();
		elemtypename = elemtypename.replace("static", "");
		elemtypename = elemtypename.replace("const", "");
		elemtypename = elemtypename.substring(0, elemtypename.length()-name.length());
			
		Attribute a = null;

		if (smalltab.length == 1) // enum field
			a = new Attribute(name, currentAccess, new EnumType(smalltab[index],Keyword.PACKAGE)); //TODO a refaire

		else if (ass.equals(Association.ONE_MANY))
		{
			String t = (String) line.subSequence(line.indexOf("<") + 1, line.indexOf(">"));

			a = new Attribute(line.substring(line.indexOf(">") + 2), currentAccess, new ListType());
			attrInfo += currentElemId + ",";
			attrInfo += t + ","; // the type to set
			attrInfo += a.getID()+ ",";
			attrInfo += ass + "%";
		}
		else if (ass.equals(Association.N_FIXED))
		{
			if (isPrimitive(smalltab[index]))
				a = new Attribute(name, currentAccess, new ArrayType(getPrimitive(smalltab[index]), dims));
			else
			{
				a = new Attribute(name, currentAccess, new ArrayType(dims, false));
				attrInfo += currentElemId + ",";
				attrInfo += elemtypename+ ",";
				attrInfo += a.getID()+ ",";
				attrInfo += ass + "%";
			}
		}
		else // ass = one
		{
			if (isPrimitive(smalltab[index]))
				a = new Attribute(name, currentAccess, getPrimitive(smalltab[index]));
			else
			{
				a = new Attribute(name, currentAccess, PrimitiveType.BYTE);
				attrInfo += currentElemId + ",";
				attrInfo += elemtypename+ ",";
				attrInfo += a.getID()+ ",";
				attrInfo += ass + "%";
			}
		}
		
		a.setFinal(isFinal);
		a.setStatic(isStatic);

		if (!right.isEmpty())
			a.setValue(right);

		return a;
	}

	private boolean isPrimitive(String key)
	{
		return key.equals("int") || key.equals("bool") || key.equals("short") || key.equals("long")
				|| key.equals("float") || key.equals("double") || key.equals("char") || key.equals("byte");
	}

	private PrimitiveType getPrimitive(String p)
	{
		if (p.equals("int"))
			return PrimitiveType.INT;
		if (p.equals("bool"))
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
					System.err.println("error Attributes on file:"+ fFile.getName() +",error  "+ e.getMessage());		
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
			
			for (Parameter p : mx.getParams()) 
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
	
	/**
	 * print the data structure. For debugging only
	 */
	public void printDebug() 
	{
		System.out.println("!!!!! DEBUG " + project.getFilesRecord().size() );
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
}
