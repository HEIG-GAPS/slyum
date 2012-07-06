package dataRecord.elements;

import dataRecord.io.ElementVisitor;

public interface Element
{
	String accept(ElementVisitor visitor);
	
	String getName();
	
	int getID();
}
