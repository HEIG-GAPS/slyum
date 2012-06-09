package dataRecord;

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
