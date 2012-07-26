package dataRecord.elements;

import classDiagram.ClassDiagram;
import dataRecord.io.ElementVisitor;
/**
 * This class represent a comment made in a source code file
 * A code can be of one single line or multiline
 *
 * 
 * @author Fabrizio Beretta Piccoli
 * @version 2.0 | 5-lug-2012
 */
public class Comment implements Element
{
	private String comment;
	private boolean isMultiLine;
	private int ID;

	public Comment(String comment, boolean ml)
	{
		this.setComment(comment);
		this.isMultiLine = ml;
		this.ID = ClassDiagram.getElementID();
	}
	
	public Comment(String comment, boolean ml, int id)
	{
		this.setComment(comment);
		this.isMultiLine = ml;
		this.ID = id;
	}

	/**
	 * this method will be called by the writer to know
	 * how to write this object.
	 * 
	 * @see ElementVisitor
	 * 
	 */
	@Override
	public String accept(ElementVisitor visitor)
	{
		return visitor.visit(this);
	}

	public boolean isMultiLine()
	{
		return isMultiLine;
	}

	public void setMultiLine(boolean isMultiLine)
	{
		this.isMultiLine = isMultiLine;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	@Override
	public String toString()
	{
		return comment;
	}

	@Override
	public String getName()
	{
		return "has no name";
	}

	@Override
	public int getID()
	{
		return ID;
	}
	
	public void setID(int id)
	{
		this.ID = id;
	}

	
}
