package utility;

import javax.swing.JOptionPane;

public class SMessageDialog
{
	public static void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Slyum", JOptionPane.ERROR_MESSAGE, PersonnalizedIcon.getErrorIcon());
	}
	
	public static void showWarningMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Slyum", JOptionPane.WARNING_MESSAGE, PersonnalizedIcon.getWarningIcon());
	}
	
	public static void showInformationMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Slyum", JOptionPane.INFORMATION_MESSAGE, PersonnalizedIcon.getInfoIcon());
	}
}
