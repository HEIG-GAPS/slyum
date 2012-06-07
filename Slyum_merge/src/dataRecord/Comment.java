package dataRecord;

import classDiagram.ClassDiagram;

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

	/**
	 * a comment
	 */

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
