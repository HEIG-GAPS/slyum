package dataRecord;

public interface Element
{
	void accept(ElementVisitor visitor);
	
	String getName();
	
	int getID();
}
