package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelIOComponent extends JPanelRounded implements ActionListener
{

	private static final long serialVersionUID = -3219782414246923688L;

	private static final String TT_IMPORT_CODE = "Import code "+ Utility.keystrokeToString(Slyum.KEY_IMPORT_CODE);
	private static final String TT_EXPORT_DIAGRAM = "Export diagram "+ Utility.keystrokeToString(Slyum.KEY_EXPORT_DIAGRAM);
	private static final String TT_EXPORT_CPP = "Export diagram cpp" + Utility.keystrokeToString(Slyum.KEY_EXPORT_CPP);
	
	private SButton importCode, exportDiagram, exportDiagramCpp;
		
	private static SPanelIOComponent instance;
		
	public static SPanelIOComponent getInstance()
	{
		if (instance == null)
			instance = new SPanelIOComponent();
		
		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		PanelClassDiagram p = PanelClassDiagram.getInstance();
		
		if (Slyum.ACTION_IMPORT.equals(e.getActionCommand()))
			p.importCode();
		
		if (Slyum.ACTION_EXPORT_PROJECT.equals(e.getActionCommand()))
			p.exportCode(Slyum.JAVA_EXTENSION);	
		
		if (Slyum.ACTION_EXPORT_CPP.equals(e.getActionCommand()))
			p.exportCode("cpp");
	}

	private SPanelIOComponent()
	{
		System.out.println(Slyum.ICON_PATH);
		setLayout(new GridLayout(1, 3, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(/*Color.WHITE*/ new Color(0, 0, 255, 10));
		setForeground(Color.GRAY);
		setMaximumSize(new Dimension(300, 50));

		add(importCode = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "import16.png"), Slyum.ACTION_IMPORT, Color.BLUE, TT_IMPORT_CODE));
		add(exportDiagram = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "exportCode.png"), Slyum.ACTION_EXPORT_PROJECT, Color.BLUE, TT_EXPORT_DIAGRAM));
		add(exportDiagramCpp = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "ExportCpp.png"), Slyum.ACTION_EXPORT_CPP, Color.BLUE, TT_EXPORT_CPP));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	private SButton createSButton(ImageIcon ii, String a, Color c, String tt)
	{
		return new SButton(ii, a, c, tt, this);
	}
	
	public SButton getBtnImportCode()
	{
		return importCode;
	}
	
	public SButton getBtnExportDiagram()
	{
		return exportDiagram;
	}
	
	public SButton getBtnExportCpp()
	{
		return exportDiagramCpp;
	}
}
