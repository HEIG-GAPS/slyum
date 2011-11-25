package utility;

import javax.swing.JOptionPane;

public class SMessageDialog
{
	public static void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Slyum", JOptionPane.ERROR_MESSAGE, PersonnalizedIcon.getErrorIcon());
	}
}
