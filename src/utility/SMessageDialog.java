package utility;

import javax.swing.JOptionPane;

import swing.Slyum;

public class SMessageDialog
{
	private static final String TITLE_WINDOW = Slyum.getInstance().getName();
	
	public static void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void showWarningMessage(String message)
	{
		JOptionPane.showMessageDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.WARNING_MESSAGE);
	}
	
	public static void showInformationMessage(String message)
	{
		JOptionPane.showMessageDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int showQuestionMessageYesNo(String message)
	{
		return JOptionPane.showConfirmDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.YES_NO_OPTION);
	}
	
	public static int showQuestionMessageOkCancel(String message)
	{
		return JOptionPane.showConfirmDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.OK_CANCEL_OPTION);
	}
	
	public static int showQuestionMessageYesNoCancel(String message)
	{
		return JOptionPane.showConfirmDialog(Slyum.getInstance(), message, TITLE_WINDOW, JOptionPane.YES_NO_CANCEL_OPTION);
	}
}
