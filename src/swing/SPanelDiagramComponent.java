package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelDiagramComponent extends JPanelRounded
{
	private static final String TITLE = "Class diagram";
	
	private static final String TT_CLASS = "Class " + Utility.keystrokeToString(Slyum.KEY_CLASS);
	private static final String TT_INTERFACE = "Interface " + Utility.keystrokeToString(Slyum.KEY_INTERFACE);
	private static final String TT_CLASS_ASSOC = "Association class " + Utility.keystrokeToString(Slyum.KEY_ASSOCIATION_CLASS);
	
	private static final String TT_GENERALIZE = "Generalize & Realize " + Utility.keystrokeToString(Slyum.KEY_INHERITANCE);
	private static final String TT_DEPENDENCY = "Dependency " + Utility.keystrokeToString(Slyum.KEY_DEPENDENCY);
	private static final String TT_INNER_CLASS = "Inner class " + Utility.keystrokeToString(Slyum.KEY_INNER_CLASS);
	
	private static final String TT_ASSOCIATION = "Association " + Utility.keystrokeToString(Slyum.KEY_ASSOCIATION);
	private static final String TT_AGGREGATION = "Aggregation " + Utility.keystrokeToString(Slyum.KEY_AGGREGATION);
	private static final String TT_COMPOSITION = "Composition " + Utility.keystrokeToString(Slyum.KEY_COMPOSITION);
	
	private static final String TT_MULTI = "Multi-association " + Utility.keystrokeToString(Slyum.KEY_MULTI_ASSOCIATION);
	private static final String TT_NOTE = "Note " + Utility.keystrokeToString(Slyum.KEY_NOTE);
	private static final String TT_LINK_NOTE = "Link note " + Utility.keystrokeToString(Slyum.KEY_LINK_NOTE);
	
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
		
		JLabel labelTitle = new JLabel(TITLE);
		panelTop.add(labelTitle);

		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/class.png"), "newClass", Color.RED, TT_CLASS));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/interface.png"), "newInterface", Color.RED, TT_INTERFACE));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/classAssoc.png"), "newClassAssoc", Color.RED, TT_CLASS_ASSOC));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/generalize.png"), "newGeneralize", Color.RED, TT_GENERALIZE));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/dependency.png"), "newDependency", Color.RED, TT_DEPENDENCY));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/innerClass.png"), "newInnerClass", Color.RED, TT_INNER_CLASS));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/association.png"), "newAssociation", Color.RED, TT_ASSOCIATION));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/aggregation.png"), "newAggregation", Color.RED, TT_AGGREGATION));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/composition.png"), "newComposition", Color.RED, TT_COMPOSITION));
		
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/multi.png"), "newMulti", Color.RED, TT_MULTI));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/note.png"), "newNote", Color.RED, TT_NOTE));
		panelBottom.add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/linkNote.png"), "linkNote", Color.RED, TT_LINK_NOTE));
		
		setMaximumSize(new Dimension(200, 150));
		
		add(panelTop);
		add(panelBottom);
	}
}
