package dataRecord;

public class Comment implements Element
{
	private String comment;
	private boolean isMultiLine;

	public Comment(String comment, boolean ml)
	{
		this.setComment(comment);
		this.isMultiLine = ml;
	}

	@Override
	public void accept(ElementVisitor visitor)
	{
		visitor.visit(this);
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
		if (isMultiLine)
			return "/** " + comment + " */";
		else
			return "//" + comment;
	}

	@Override
	public String getName()
	{
		return "has no name";
	}

	
}
