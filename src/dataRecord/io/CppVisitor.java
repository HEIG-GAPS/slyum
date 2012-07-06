package dataRecord.io;

import dataRecord.Keyword;
import dataRecord.elementType.APIclass;
import dataRecord.elementType.APIinterface;
import dataRecord.elementType.Extendable;
import dataRecord.elementType.Implementable;
import dataRecord.elements.Attribute;
import dataRecord.elements.ClassType;
import dataRecord.elements.Comment;
import dataRecord.elements.CompilationUnit;
import dataRecord.elements.Constructor;
import dataRecord.elements.Declaration;
import dataRecord.elements.Element;
import dataRecord.elements.EnumType;
import dataRecord.elements.ImportStmt;
import dataRecord.elements.InterfaceField;
import dataRecord.elements.InterfaceType;
import dataRecord.elements.Method;
import dataRecord.elements.PackageStmt;
import dataRecord.elements.Parametre;
import dataRecord.elements.Type;

public class CppVisitor implements ElementVisitor
{	
	private Keyword currentAccess = Keyword.PACKAGE; 
	
	public CppVisitor()
	{
	}
	
	@Override
	public String visit(PackageStmt ps)
	{
		return "namespace "+ ps.getPackageName();
	}

	@Override
	public String visit(ImportStmt importStmt)
	{
		return "#import <"+importStmt.getPackageName()+">";
	}

	@Override
	public String visit(Comment comment)
	{
		return comment.toString();
	}

	@Override
	public String visit(Type header)
	{
		return null;
	}

	@Override
	public String visit(EnumType enumType)
	{
		return enumType.toString();
	}

	@Override
	// actually there is no interface on C++
	public String visit(InterfaceType it)
	{
		String tmp = "class ";
		if (!it.getImplList().isEmpty())
		{
			tmp += " : ";
			for (Implementable ex : it.getImplList())
			{
				if (ex.getClass() == APIinterface.class)
					tmp += ((APIinterface)ex).getElementType();
				else
					tmp += ex.getClass().getSimpleName();
				if (!ex.equals(it.getImplList().get(it.getImplList().size() - 1)))
					tmp += ", ";
			}
		}
		tmp += "\n";
		for (int i = 0; i < Declaration.getDepth(); i++)
		{
			tmp += "\t";
		}
		tmp += "{";
		Declaration.setDepth(Declaration.getDepth() + 1);
		for (Element e : it.getElements())
		{
			tmp += "\n";
			for (int i = 0; i < Declaration.getDepth(); i++)
			{
				tmp += "\t";
			}
			tmp += e.accept(this);
		}
		Declaration.setDepth(Declaration.getDepth() - 1);
		tmp += "\n";
		for (int i = 0; i < Declaration.getDepth(); i++)
		{
			tmp += "\t";
		}

		tmp += "}";

		return tmp;
	}

	@Override
	public String visit(Constructor constructorHeader)
	{
		return null;
	}

	@Override
	public String visit(Method method)
	{
		String s = "";
		
		if (!method.getAccess().equals(currentAccess))
		{
			currentAccess = method.getAccess();
			s += method.getAccess() + " : \n\t";
		}
		s += "\t";

		if (method.isStatic())
			s += "static ";
		if (method.isAbstract())
			s += "virtual ";
		s += method.getReturnType();
		s += " ";
		s += method.getName();
		s += method.ParamToString();
		//s += throwClausesToString();
		if (method.isFinal())
			s += "const ";

		if (method.isAbstract())
			s+= method.getMethodBody();
		else
		{	
			String tabs = "";
			for (int i = 0; i < Declaration.getDepth(); i++)
			{
				tabs += "\t";
			}
			s += "\n\t"+ tabs + method.getMethodBody();
		}

		return s;
	}

	@Override
	public String visit(CompilationUnit compilationUnit)
	{
		String tmp = "";

		for (Element e : compilationUnit.getElements())
		{
			tmp += e.accept(this);
		}

		return tmp;
	}

	@Override
	public String visit(Attribute attr)
	{
		String tmp = "";
		
		if (!attr.getAccess().equals(currentAccess))
		{
			currentAccess = attr.getAccess();
			tmp += attr.getAccess() + " : \n\t";
		}
		tmp += "\t";
		
		if (attr.isStatic())
			tmp += "static ";
		tmp += attr.getType().getElementType();
		if (attr.isFinal())
			tmp += "const ";
		tmp += " ";
		tmp += attr.getName();

		if (!attr.getValue().isEmpty())
			tmp += " =" + attr.getValue();

		return tmp;
	}

	@Override
	public String visit(Parametre parametre)
	{
		String tmp = "";

		if (parametre.isFinal())
			tmp += "const";
		
		tmp += parametre.getType().getElementType();
		tmp += " ";
		tmp += parametre.getName();
		
		if (!parametre.getValue().isEmpty())
			tmp += " =" + parametre.getValue();

		return tmp;
	}

	@Override
	public String visit(InterfaceField interfaceField)
	{
		return null;
	}
	
	@Override
	public String visit(ClassType ct)
	{
		
		String tmp ="";

		tmp += " ";
		if (ct.isAbstract())
			tmp += "virtual ";
		if (ct.isStatic())
			tmp += "static ";
		tmp += "class " + ct.getName() + " ";
		if (!ct.getExtendList().isEmpty() || !ct.getImplList().isEmpty())
		{
			tmp += ": ";
			for (Extendable ex : ct.getExtendList())
			{
				if (ex.getClass() == APIclass.class)
				{
					tmp += "public ";
					tmp += ((APIclass)ex).getElementType();
				}
				else
				{
					tmp += ((ClassType) ex).getAccess() + " ";
					tmp += ((ClassType) ex).getName();
				}
				if (!ex.equals(ct.getExtendList().get(ct.getExtendList().size() - 1)) || !ct.getImplList().isEmpty())
					tmp += ", ";
			}
			for (Implementable ex : ct.getImplList())
			{
				if (ex.getClass() == APIinterface.class)
					tmp += ((APIinterface)ex).getElementType();
				else
					tmp += ((InterfaceType) ex).getName();
				if (!ex.equals(ct.getImplList().get(ct.getImplList().size() - 1)))
					tmp += ", ";
			}
			
			tmp += " ";
		}
		tmp += "\n";
		for (int i = 0; i < Declaration.getDepth(); i++)
		{
			tmp += "\t";
		}
		tmp += "{";
		Declaration.setDepth(Declaration.getDepth() + 1);
		for (Element e : ct.getElements())
		{
			tmp += "\n";
			for (int i = 0; i < Declaration.getDepth(); i++)
			{
				tmp += "\t";
			}
			tmp += e.accept(this);
		}
		Declaration.setDepth(Declaration.getDepth() - 1);
		tmp += "\n";
		for (int i = 0; i < Declaration.getDepth(); i++)
		{
			tmp += "\t";
		}
		tmp += "}";
		

		currentAccess = Keyword.PACKAGE;
		return tmp;
	}

}
