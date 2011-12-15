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

		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "class.png"), Slyum.ACTION_NEW_CLASS, Color.RED, TT_CLASS));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "interface.png"), Slyum.ACTION_NEW_INTERFACE, Color.RED, TT_INTERFACE));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "classAssoc.png"), Slyum.ACTION_NEW_CLASS_ASSOCIATION, Color.RED, TT_CLASS_ASSOC));
		
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "generalize.png"), Slyum.ACTION_NEW_GENERALIZE, Color.RED, TT_GENERALIZE));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "dependency.png"), Slyum.ACTION_NEW_DEPENDENCY, Color.RED, TT_DEPENDENCY));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "innerClass.png"), Slyum.ACTION_NEW_INNER_CLASS, Color.RED, TT_INNER_CLASS));
		
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "association.png"), Slyum.ACTION_NEW_ASSOCIATION, Color.RED, TT_ASSOCIATION));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "aggregation.png"), Slyum.ACTION_NEW_AGGREGATION, Color.RED, TT_AGGREGATION));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "composition.png"), Slyum.ACTION_NEW_COMPOSITION, Color.RED, TT_COMPOSITION));
		
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "multi.png"), Slyum.ACTION_NEW_MULTI, Color.RED, TT_MULTI));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "note.png"), Slyum.ACTION_NEW_NOTE, Color.RED, TT_NOTE));
		panelBottom.add(new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "linkNote.png"), Slyum.ACTION_NEW_LINK_NOTE, Color.RED, TT_LINK_NOTE));
		
		setMaximumSize(new Dimension(200, 150));
		
		add(panelTop);
		add(panelBottom);
	}
}
