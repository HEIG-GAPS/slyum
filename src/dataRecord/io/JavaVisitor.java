package dataRecord.io;

import dataRecord.Keyword;
import dataRecord.elementType.APIclass;
import dataRecord.elementType.APIinterface;
import dataRecord.elementType.Extendable;
import dataRecord.elementType.Implementable;
import dataRecord.elementType.ListType;
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

public class JavaVisitor implements ElementVisitor
{

	@Override
	public String visit(PackageStmt ps)
	{
		return ps.toString() + "\n";
	}

	@Override
	public String visit(ImportStmt importStmt)
	{
		return importStmt.toString() + "\n";
	}

	@Override
	public String visit(Comment comment)
	{
		return comment.toString();
	}

	@Override
	public String visit(Type header)
	{
		return header.toString();
	}

	@Override
	public String visit(EnumType eh)
	{
		return eh.toString();
	}

	@Override
	public String visit(InterfaceType ih)
	{
		return ih.toString();
	}

	@Override
	public String visit(Constructor constructorHeader)
	{
		return constructorHeader.toString();
	}

	@Override
	public String visit(Method methodHeader)
	{
		return methodHeader.toString();
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
		String s = "";
		s += attr.getAccess().toString();
		if (attr.getAccess() != Keyword.PACKAGE)
			s += " ";
		if (attr.isStatic())
			s += "static ";
		if (attr.isFinal())
			s += "final ";
		s += " ";
		if(attr.getType().getClass()==ListType.class)
		{
			ListType lt = (ListType) attr.getType();
			s += lt.getCollection()==null?"List":lt.getCollection();
			s += "<"+attr.getType().getElementType()+">";
			//System.out.println("visiting ListType  " + attr.getName() + "\t "+ lt.getCollection() +"\t" + attr.type.getElementType());
		}
		else		
			s+= attr.getType().getElementType();
		
		s+= " ";
		s += attr.getName();

		if (!attr.getValue().isEmpty())
			s += " =" + attr.getValue();
		

		return s + ";";
	}

	@Override
	public String visit(Parametre parametre)
	{
		return parametre.toString();
	}

	@Override
	public String visit(InterfaceField interfaceField)
	{
		return interfaceField.toString();
	}

	@Override
	public String visit(ClassType ct)
	{
		String tmp = ct.getAccess().toString();

		tmp += " ";
		if (ct.isFinal())
			tmp += "final ";
		if (ct.isAbstract())
			tmp += "abstract ";
		if (ct.isStatic())
			tmp += "static ";
		tmp += "class " + ct.getName() + " ";
		if(!ct.getGeneric().isEmpty())
			tmp += "<"+ct.getGeneric()+">";
		if (!ct.getExtendList().isEmpty())
		{
			tmp += "extends ";
			for (Extendable ex : ct.getExtendList())
			{
				if (ex.getClass() == APIclass.class)
					tmp += ((APIclass)ex).getElementType();
				else
					tmp += ((ClassType) ex).getName();
				if (!ex.equals(ct.getExtendList().get(ct.getExtendList().size() - 1)))
					tmp += ", ";
			}
			tmp += " ";
		}
		if (!ct.getImplList().isEmpty())
		{
			tmp += "implements ";
			for (Implementable ex : ct.getImplList())
			{
				if (ex.getClass() == APIinterface.class)
					tmp += ((APIinterface)ex).getElementType();
				else
					tmp += ((InterfaceType) ex).getName();
				if (!ex.equals(ct.getImplList().get(ct.getImplList().size() - 1)))
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

		return tmp;
	}

}
