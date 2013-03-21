package utility;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import swing.Slyum;

public class SDialogProjectLoading extends JDialog
{
	private static final long serialVersionUID = 1L;
	private static final String TITLE_DIALOG_PROJECT_LOADING = "Slyum - Project loading...";
	private static final Dimension MINIMIZED_SIZE = new Dimension(400, 60);
	private static final Dimension NORMAL_SIZE = new Dimension(400, 250);
	
	private static Slyum s = Slyum.getInstance();
	
	private JLabel lblCurrentPhase;
	private JList<String> list;
	private JProgressBar progressBar;
	private JScrollPane scrollPane;
	private JButton btnCancel;
	
	public SDialogProjectLoading(String projectName)
	{
		super(s, TITLE_DIALOG_PROJECT_LOADING, true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(400, 250);
		setLocationRelativeTo(s);
		setResizable(false);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 0};
		gbl_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblOpening = new JLabel("Opening : ");
		GridBagConstraints gbc_lblOpening = new GridBagConstraints();
		gbc_lblOpening.anchor = GridBagConstraints.WEST;
		gbc_lblOpening.insets = new Insets(0, 0, 5, 5);
		gbc_lblOpening.gridx = 0;
		gbc_lblOpening.gridy = 0;
		panel.add(lblOpening, gbc_lblOpening);
		
		JLabel lblProjectName = new JLabel(projectName);
		GridBagConstraints gbc_lblProjectName = new GridBagConstraints();
		gbc_lblProjectName.anchor = GridBagConstraints.WEST;
		gbc_lblProjectName.insets = new Insets(0, 0, 5, 0);
		gbc_lblProjectName.gridx = 1;
		gbc_lblProjectName.gridy = 0;
		panel.add(lblProjectName, gbc_lblProjectName);
		
		lblCurrentPhase = new JLabel("currentPhase");
		GridBagConstraints gbc_lblCurrentPhase = new GridBagConstraints();
		gbc_lblCurrentPhase.anchor = GridBagConstraints.WEST;
		gbc_lblCurrentPhase.gridwidth = 2;
		gbc_lblCurrentPhase.insets = new Insets(0, 0, 5, 0);
		gbc_lblCurrentPhase.gridx = 0;
		gbc_lblCurrentPhase.gridy = 1;
		panel.add(lblCurrentPhase, gbc_lblCurrentPhase);
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.insets = new Insets(0, 0, 5, 0);
		gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
		gbc_progressBar.gridwidth = 2;
		gbc_progressBar.gridx = 0;
		gbc_progressBar.gridy = 2;
		panel.add(progressBar, gbc_progressBar);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 3;
		panel.add(scrollPane, gbc_scrollPane);
		
		list = new JList<String>();
		scrollPane.setViewportView(list);
		list.setModel(new StringListModel());
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				setVisible(false);
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.EAST;
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 4;
		panel.add(btnCancel, gbc_btnCancel);
		minimized(true);
	}
	
	public void minimized(boolean enable)
	{
		btnCancel.setVisible(!enable);
		list.setVisible(!enable);
		progressBar.setVisible(!enable);
		lblCurrentPhase.setVisible(!enable);
		scrollPane.setVisible(!enable);
		
		if(enable)
			setSize(MINIMIZED_SIZE);
		else
			setSize(NORMAL_SIZE);
	}
	
	public void setProgressBarMaximum(int value)
	{
		progressBar.setMaximum(value);
	}
	
	public void setPhase(String phase)
	{
		lblCurrentPhase.setText(phase);
		list.ensureIndexIsVisible(list.getModel().getSize()-1);
	}
	
	public void addStep(String step)
	{
		((StringListModel)list.getModel()).add(step);
		list.ensureIndexIsVisible(list.getModel().getSize()-1);
		
		progressBar.setValue(progressBar.getValue()+1);
	}

	private class StringListModel extends AbstractListModel<String>
	{
		private static final long serialVersionUID = -3126417577301749576L;
		
		LinkedList<String> values = new LinkedList<String>();

		@Override
		public String getElementAt(int index)
		{
			return values.get(index);
		}

		public void add(String s)
		{
			values.add(s);
			fireIntervalAdded(this, values.size()-1, values.size()-1);
		}

		@Override
		public int getSize()
		{
			return values.size();
		}		
	}
}
