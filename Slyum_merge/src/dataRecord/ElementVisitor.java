package dataRecord;

public interface ElementVisitor
{
	void visit(PackageStmt ps);

	void visit(ImportStmt importStmt);

	void visit(Comment comment);

	void visit(Type header);

	void visit(EnumType eh);

	void visit(InterfaceType ih);

	void visit(Constructor constructorHeader);

	void visit(Method methodHeader);

	void visit(CompilationUnit compilationUnit);

	void visit(Attribute attribute);

	void visit(Parametre parametre);

	void visit(InterfaceField interfaceField);
}
