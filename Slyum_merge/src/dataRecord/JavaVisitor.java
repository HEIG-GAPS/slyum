package dataRecord;

public class JavaVisitor implements ElementVisitor
{

	@Override
	public String visit(PackageStmt ps)
	{
		return ps.toString();
	}

	@Override
	public String visit(ImportStmt importStmt)
	{
		return importStmt.toString();
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
		return compilationUnit.toString();
	}

	@Override
	public String visit(Attribute attribute)
	{
		return attribute.toString();
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
		return ct.toString();
	}

}
