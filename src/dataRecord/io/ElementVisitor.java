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
import dataRecord.elements.Parameter;
import dataRecord.elements.PreprocessorStmt;
import dataRecord.elements.Type;
/**
 * To be able to write a souce code file, the syntax of the choosen
 * language must be known, this interface represent all the element that have to 
 * be redefined.
 *  
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 11-lug-2012
 *
 */
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

	String visit(Parameter parametre);

	String visit(InterfaceField interfaceField);
	
	String visit(ClassType ct);

	String visit(PreprocessorStmt preprocessorStmt);

	//String visit(ListType listType);
}
