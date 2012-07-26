package dataRecord.elements;

import dataRecord.io.ElementVisitor;
/**
 * An Element is whatever is written in a source code file (except for the member body)
 * So a comment, a class, a method or even an attribute are all element of a file 
 * (represented by a compilation unit)
 * Every element have at least a name and an ID. they have also a function named <i>accept </i>
 * for knowing how to represent it (how to write it on a file). 
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public interface Element
{
	String accept(ElementVisitor visitor);
	
	String getName();
	
	int getID();
}
