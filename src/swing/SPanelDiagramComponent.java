package swing;

import graphic.textbox.TextBox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.PersonnalizedIcon;

public class SPanelDiagramComponent extends JPanelRounded
{
	private static final long serialVersionUID = -8198486630670114549L;

	public SPanelDiagramComponent()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
		setBackground(new Color(255, 0, 0, 10));
		setForeground(Color.GRAY);
		
		JPanel panelTop = new JPanel();
		panelTop.setOpaque(false);
		
		JPanel panelBottom = new JPanel();
		panelBottom.setLayout(new GridLayout(4, 3));
		panelBottom.setOpaque(false);
		
		JLabel labelTitle = new JLabel("UML components");
		labelTitle.setFont(TextBox.getFont());
		panelTop.add(labelTitle);

		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/class.png"), "newClass", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/interface.png"), "newInterface", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/classAssoc.png"), "newClassAssoc", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/generalize.png"), "newGeneralize", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/dependency.png"), "newDependency", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/innerClass.png"), "newInnerClass", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/association.png"), "newAssociation", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/aggregation.png"), "newAggregation", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/composition.png"), "newComposition", Color.RED));
		
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/multi.png"), "newMulti", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/note.png"), "newNote", Color.RED));
		panelBottom.add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/linkNote.png"), "linkNote", Color.RED));
		
		setMaximumSize(new Dimension(200, 150));
		
		add(panelTop);
		add(panelBottom);
	}
}
