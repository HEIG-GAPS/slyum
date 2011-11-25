package graphic.factory;

import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.textbox.TextBoxCommentary;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import classDiagram.ClassDiagram;

/**
 * NoteFactory allows to create a new note. Give this factory at the graphic
 * view using the method initNewComponent() for initialize a new factory. Next,
 * graphic view will use the factory to allow creation of a new component,
 * according to the specificity of the factory.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class NoteFactory extends RelationFactory
{

	/**
	 * Create a new factory allowing the creation of a note.
	 * 
	 * @param parent
	 *            the graphic view
	 * @param classDiagram
	 *            the class diagram
	 */
	public NoteFactory(GraphicView parent, ClassDiagram classDiagram)
	{
		super(parent, classDiagram);
	}

	@Override
	public GraphicComponent create()
	{
		final TextBoxCommentary tb = new TextBoxCommentary(parent, "Double-click to edit note.", componentMousePressed);
		tb.setBounds(new Rectangle(mouseReleased.x, mouseReleased.y, 100, 100));

		parent.addNotes(tb);

		tb.repaint();

		return tb;
	}

	@Override
	protected void drawExtremity(Graphics2D g2)
	{
		TextBoxCommentary.drawNote(g2, new Rectangle(mouseLocation.x, mouseLocation.y, 30, 30), 8, new Color(254, 250, 220));
	}
}
