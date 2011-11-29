package change;

import java.util.LinkedList;

import swing.PanelClassDiagram;
import swing.Slyum;

public class Change
{
	private static boolean block = false;
	private static int pointer = 0;
	
	private static boolean _hasChange = false;

	private static LinkedList<Changeable> stack = new LinkedList<Changeable>();

	private static void printStackState()
	{
		System.out.println("Etat de la pile");

		for (int i = 0; i < stack.size(); i++)
		
			System.out.println(i + (pointer == i ? "<--" : ""));
		

		System.out.println("--------------");
	}

	public static void push(Changeable ch)
	{
		if (block)
			return;
		
		// Remove all elements positioned after index pointer.
		while (stack.size() > 1 && pointer < stack.size() - 1)
		{
			stack.removeLast();
			stack.removeLast();
		}

		stack.add(ch);
		pointer = stack.size() - 1;

		printStackState();
		
		checkToolbarButtonState();
		
		setHasChange(true);
	}

	public static void redo()
	{
		final int increment = pointer % 2 == 0 ? 1 : 2;

		if (pointer >= stack.size() - 1)
			return;

		setBlocked(true);
		stack.get(pointer += increment).restore();
		setBlocked(false);

		printStackState();
		
		checkToolbarButtonState();
		
		setHasChange(true);
	}

	public static void undo()
	{
		final int decrement = pointer % 2 > 0 ? 1 : 2;
		
		if (pointer <= 0)
			return;

		setBlocked(true);
		stack.get(pointer -= decrement).restore();
		setBlocked(false);

		printStackState();

		checkToolbarButtonState();
		
		setHasChange(true);
	}
	
	protected static void checkToolbarButtonState()
	{
		Slyum.setEnableRedoButtons(pointer < stack.size() - 1);
		Slyum.setEnableUndoButtons(pointer > 0);
	}

	public static void setBlocked(boolean blocked)
	{
		block = blocked;
	}
	
	public static void clear()
	{
		stack.clear();
		
		printStackState();
	}

	public static boolean hasChange()
	{
		return _hasChange;
	}
	
	public static void setHasChange(boolean changed)
	{
		_hasChange = changed;
		Slyum.setStarOnTitle(changed);
	}
	
}
