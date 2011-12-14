package swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class NoRepopDialog extends JDialog
{
	private static final long serialVersionUID = 9004532439553406805L;
	private JCheckBox chckbxDontShowThis;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public NoRepopDialog(String message)
	{
		setModalityType(ModalityType.DOCUMENT_MODAL);
		setResizable(false);
		setTitle("Information");
		setBounds(100, 100, 361, 176);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		final GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0, 308, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			final JLabel lblImg = new JLabel("");
			lblImg.setIcon(new ImageIcon(NoRepopDialog.class.getResource(Slyum.ICON_PATH + "get_info.png")));
			final GridBagConstraints gbc_lblImg = new GridBagConstraints();
			gbc_lblImg.fill = GridBagConstraints.VERTICAL;
			gbc_lblImg.gridheight = 2;
			gbc_lblImg.insets = new Insets(0, 0, 0, 5);
			gbc_lblImg.gridx = 0;
			gbc_lblImg.gridy = 0;
			contentPanel.add(lblImg, gbc_lblImg);
		}
		{
			final JPanel panel = new JPanel();
			final GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.insets = new Insets(0, 0, 5, 5);
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 1;
			gbc_panel.gridy = 0;
			contentPanel.add(panel, gbc_panel);
		}
		{
			final JPanel panel = new JPanel();
			panel.setBackground(new Color(192, 192, 192));
			panel.setBorder(new LineBorder(new Color(192, 192, 192), 1, true));
			final GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.insets = new Insets(0, 0, 5, 0);
			gbc_panel.gridx = 2;
			gbc_panel.gridy = 0;
			contentPanel.add(panel, gbc_panel);
			final GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 308, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0 };
			gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			panel.setLayout(gbl_panel);
			{
				final JTextPane textPane = new JTextPane();
				final GridBagConstraints gbc_textPane = new GridBagConstraints();
				gbc_textPane.fill = GridBagConstraints.BOTH;
				gbc_textPane.gridx = 0;
				gbc_textPane.gridy = 0;
				panel.add(textPane, gbc_textPane);
				textPane.setText(message);
				textPane.setEditable(false);
				textPane.setBackground(new Color(220, 220, 220));
			}
		}
		{
			chckbxDontShowThis = new JCheckBox("Don't show this message again.");
			final GridBagConstraints gbc_chckbxDontShowThis = new GridBagConstraints();
			gbc_chckbxDontShowThis.fill = GridBagConstraints.HORIZONTAL;
			gbc_chckbxDontShowThis.anchor = GridBagConstraints.SOUTH;
			gbc_chckbxDontShowThis.gridx = 2;
			gbc_chckbxDontShowThis.gridy = 1;
			contentPanel.add(chckbxDontShowThis, gbc_chckbxDontShowThis);
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0)
					{
						final Properties properties = PropertyLoader.getInstance().getProperties();
						properties.put("showOpenJDKWarning", String.valueOf(!chckbxDontShowThis.isSelected()));
						PropertyLoader.getInstance().push();
		
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
