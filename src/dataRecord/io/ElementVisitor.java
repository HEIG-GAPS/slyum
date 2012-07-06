package dataRecord.io;

import dataRecord.elements.Attribute;
import dataRecord.elements.ClassType;
import dataRecord.elements.Comment;
import dataRecord.elements.CompilationUnit;
import dataRecord.elements.Constructor;
import dataRecord.elements.EnumType;
import dataRecord.elements.ImportStmt;
import dataRecord.elements.InterfaceField;
import dataRecord.elements.InterfaceType;
import dataRecord.elements.Method;
import dataRecord.elements.PackageStmt;
import dataRecord.elements.Parametre;
import dataRecord.elements.Type;

public interface ElementVisitor
{
	String visit(PackageStmt ps);

	String visit(ImportStmt importStmt);

	String visit(Comment comment);

	String visit(Type header);

	String visit(EnumType eh);

	String visit(InterfaceType ih);

	String visit(Constructor constructorHeader);

	String visit(Method methodHeader);

	String visit(CompilationUnit compilationUnit);

	String visit(Attribute attribute);

	String visit(Parametre parametre);

	String visit(InterfaceField interfaceField);
	
	String visit(ClassType ct);

	//String visit(ListType listType);
}
