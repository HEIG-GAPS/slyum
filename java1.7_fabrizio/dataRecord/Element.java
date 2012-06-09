package dataRecord;

public interface Element
{
	String accept(ElementVisitor visitor);
	
	String getName();
	
	int getID();
}
