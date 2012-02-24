package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import graphic.GraphicComponent;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;

import java.awt.GridBagLayout;
import javax.swing.JList;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import swing.SButton;
import swing.Slyum;
import utility.PersonalizedIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import swing.JPanelRounded;

public class NoteProperties extends GlobalPropreties
{
	private static final long serialVersionUID = -8359058855177837879L;
	private static NoteProperties instance;
	
	private JList<LineCommentary> list;
	private SButton btnDelete;
	
	public static NoteProperties getInstance()
	{
		if (instance == null)
			instance = new NoteProperties();
		
		return instance;
	}

	public NoteProperties()
	{
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel p = new JPanel();
		
		p.setBorder(new EmptyBorder(5, 10, 10, 10));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		p.setLayout(gridBagLayout);
		
		JPanelRounded panel = new JPanelRounded();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		p.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);
		
		list = new JList<>();
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.setViewportView(list);
		list.setModel(new ListLineCommentaryModel());
		list.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				super.mousePressed(e);

				List<LineCommentary> l = list.getSelectedValuesList();
				
				for (LineCommentary lc : getLineCommentary())
					
					lc.setSelected(l.contains(lc));
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				btnDelete.setEnabled(list.getSelectedIndex() != -1);
			}
		});
		
		btnDelete = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_red_delete.png"), Color.RED, "Remove link");
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				final int i = list.getSelectedIndex();

				for (LineCommentary lc : list.getSelectedValuesList())
					lc.delete();
				
				updateComponentInformations(null);
				
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run()
					{
						int j = i;
						if (i >= list.getModel().getSize())
							j--;

						list.setSelectedIndex(j);
					}
				});
			}
		});
		btnDelete.setEnabled(false);
		panel.add(btnDelete);
		
		add(p);
	}

	@Override
	public void updateComponentInformations(UpdateMessage msg)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run()
			{
				list.setModel(new ListLineCommentaryModel());
			}
		});
	}
	
	public void setSelectedItem(final LineCommentary lc)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run()
			{
				list.setSelectedValue(lc, true);
			}
		});
	}
	
	private class ListLineCommentaryModel extends AbstractListModel<LineCommentary>
	{
		private static final long serialVersionUID = -2384833149044855296L;

		@Override
		public LineCommentary getElementAt(int i)
		{
			if (currentObject == null)
				return null;
			
			return (LineCommentary)getLineCommentary().get(i);
		}

		@Override
		public int getSize()
		{
			if (currentObject == null)
				return 0;
			
			return getLineCommentary().size();
		}
	}
	
	private LinkedList<LineCommentary> getLineCommentary()
	{
		GraphicComponent gc = (GraphicComponent)currentObject;
		
		LinkedList<LineCommentary> ll = new LinkedList<LineCommentary>();
		
		for (LineView lv : gc.getGraphicView().getLinesViewAssociedWith(gc))
			
			ll.add((LineCommentary)lv);
		
		return ll;
	}
}
