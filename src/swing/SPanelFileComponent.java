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

public class SPanelFileComponent extends JPanelRounded implements ActionListener
{
	private static final long serialVersionUID = -3219782414246923686L;
	
	private static final String TT_NEW_PROJECT = "New project " + Utility.keystrokeToString(Slyum.KEY_NEW_PROJECT);
	private static final String TT_OPEN = "Open " + Utility.keystrokeToString(Slyum.KEY_OPEN_PROJECT);
	private static final String TT_SAVE = "Save " + Utility.keystrokeToString(Slyum.KEY_SAVE);
	private static final String TT_EXPORT = "Export image " + Utility.keystrokeToString(Slyum.KEY_EXPORT);
	private static final String TT_CLIPBOARD = "Clipboard " + Utility.keystrokeToString(Slyum.KEY_KLIPPER);
	private static final String TT_PRINT = "Print " + Utility.keystrokeToString(Slyum.KEY_PRINT);
	private static final String TT_IMPORT_CODE = "Import code "+ Utility.keystrokeToString(Slyum.KEY_IMPORT_CODE);
	private static final String TT_EXPORT_DIAGRAM = "Export diagram "+ Utility.keystrokeToString(Slyum.KEY_EXPORT_DIAGRAM);
	
	private SButton newProject, open, save, export, klipper, print;
	
	private SButton importCode, exportDiagram;
	
	private static SPanelFileComponent instance;
	
	public static SPanelFileComponent getInstance()
	{
		if (instance == null)
			instance = new SPanelFileComponent();
		
		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		PanelClassDiagram p = PanelClassDiagram.getInstance();
		
		if (Slyum.ACTION_NEW_PROJECT.equals(e.getActionCommand()))
			p.newProject();

		else if (Slyum.ACTION_OPEN.equals(e.getActionCommand()))
			p.openFromXML();

		else if (Slyum.ACTION_SAVE.equals(e.getActionCommand()))
			p.saveToXML(false);
		
		if (Slyum.ACTION_EXPORT.equals(e.getActionCommand()))
			p.exportAsImage();
		
		else if (Slyum.ACTION_KLIPPER.equals(e.getActionCommand()))
			p.getCurrentGraphicView().copyDiagramToClipboard();
		
		if (Slyum.ACTION_PRINT.equals(e.getActionCommand()))
			p.initPrinting();
		
		if (Slyum.ACTION_IMPORT.equals(e.getActionCommand()))
			p.importCode();
		
		if (Slyum.ACTION_EXPORT_PROJECT.equals(e.getActionCommand()))
			p.exportCode();	
	}

	private SPanelFileComponent()
	{
		System.out.println(Slyum.ICON_PATH);
		setLayout(new GridLayout(1, 6, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(/*Color.WHITE*/ new Color(0, 0, 255, 10));
		setForeground(Color.GRAY);
		setMaximumSize(new Dimension(300, 50));

		add(newProject = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "new.png"), Slyum.ACTION_NEW_PROJECT, Color.BLUE, TT_NEW_PROJECT));
		add(open = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "open.png"), Slyum.ACTION_OPEN, Color.BLUE, TT_OPEN));
		add(save = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "save.png"), Slyum.ACTION_SAVE, Color.BLUE, TT_SAVE));
		add(export = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "export.png"), Slyum.ACTION_EXPORT, Color.BLUE, TT_EXPORT));
		add(klipper = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "klipper.png"), Slyum.ACTION_KLIPPER, Color.BLUE, TT_CLIPBOARD));
		add(print = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "print.png"), Slyum.ACTION_PRINT, Color.BLUE, TT_PRINT));
		add(importCode = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "import16.png"), Slyum.ACTION_IMPORT, Color.BLUE, TT_IMPORT_CODE));
		add(exportDiagram = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "exportCode.png"), Slyum.ACTION_EXPORT_PROJECT, Color.BLUE, TT_EXPORT_DIAGRAM));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	private SButton createSButton(ImageIcon ii, String a, Color c, String tt)
	{
		return new SButton(ii, a, c, tt, this);
	}
	
	public SButton getBtnNewProject()
	{
		return newProject;
	}
	
	public SButton getBtnOpen()
	{
		return open;
	}
	
	public SButton getBtnSave()
	{
		return save;
	}
	
	public SButton getBtnExport()
	{
		return export;
	}
	
	public SButton getBtnKlipper()
	{
		return klipper;
	}
	
	public SButton getBtnPrint()
	{
		return print;
	}
	
	public SButton getBtnImportCode()
	{
		return importCode;
	}
	
	public SButton getBtnExportDiagram()
	{
		return exportDiagram;
	}
}
