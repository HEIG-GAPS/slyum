package change;

import swing.PanelClassDiagram;
import swing.Slyum;

import java.util.LinkedList;

public class Change {

  private static Change instance;

  public static Change getInstance() {
    if (instance == null)
      instance = new Change();

    return instance;
  }

  private boolean _hasChange = false;
  private boolean addSinceLastRecord = false;
  private boolean block = false;
  private boolean isRecord = false;
  private int pointer = 0;

  private LinkedList<Boolean> record = new LinkedList<>();
  private LinkedList<Changeable> stack = new LinkedList<>();

  public void _clear() {
    stack.clear();
    record.clear();
    pointer = 0;
    setHasChange(false);

    printStackState();
  }

  public static void clear() {
    getInstance()._clear();
  }

  public void _setHasChange(boolean changed) {
    _hasChange = changed;

    Slyum.setStarOnTitle(changed);

    checkToolbarButtonState();
  }

  public static void setHasChange(boolean changed) {
    getInstance()._setHasChange(changed);
  }

  public static Changeable getLast() {
    return getInstance()._getLast();
  }

  public Changeable _getLast() {
    return stack.getLast();
  }

  public static int getSize() {
    return getInstance()._getSize();
  }

  public int _getSize() {
    return stack.size();
  }

  public boolean _hasChange() {
    return _hasChange;
  }

  public static boolean hasChange() {
    return getInstance()._hasChange();
  }

  public boolean _isBlocked() {
    return block;
  }

  public static boolean isBlocked() {
    return getInstance()._isBlocked();
  }

  public void _setBlocked(boolean blocked) {
    block = blocked;
  }

  public static void setBlocked(boolean blocked) {
    getInstance()._setBlocked(blocked);
  }

  public boolean _isRecord() {
    return isRecord;
  }

  public static boolean isRecord() {
    return getInstance()._isRecord();
  }

  public static void pop() {
    getInstance()._pop();
  }

  public void _pop() {
    if (pointer == stack.size() - 1) pointer--;

    stack.removeLast();
    record.removeLast();
  }

  public void _push(Changeable ch) {
    if (block) return;

    // Remove all elements positioned after index pointer.
    while (stack.size() > 1 && pointer < stack.size() - 1) {
      stack.removeLast();
      stack.removeLast();

      record.removeLast();
      record.removeLast();
    }

    stack.add(ch);
    record.add(isRecord);

    if (isRecord()) addSinceLastRecord = true;

    pointer = stack.size() - 1;

    printStackState();

    checkToolbarButtonState();

    setHasChange(true);
  }

  public static void push(Changeable ch) {
    getInstance()._push(ch);
  }

  /**
   * Begin a record. A record merge all new pushes in a same group. When undo / redo is called, all pushes into a group
   * will be undo / redo at the same time.
   */
  public void _record() {
    addSinceLastRecord = false;
    isRecord = true;
  }

  public static void record() {
    getInstance()._record();
  }

  public void _redo() {
    if (pointer >= stack.size() - 1) return;

    final int increment = pointer % 2 == 0 ? 1 : 2;

    final boolean isBlocked = isBlocked();
    setBlocked(true);
    stack.get(pointer += increment).restore();
    setBlocked(isBlocked);

    printStackState();

    checkToolbarButtonState();

    setHasChange(true);

    if (record.get(pointer)) redo();
  }

  /**
   * Stop the current record. If no record is currently running this method have no effect.
   */
  public void _stopRecord() {
    int size = stack.size();

    boolean b1 = addSinceLastRecord, b2 = isRecord;

    addSinceLastRecord = false;
    isRecord = false;

    if (b2 == false || size < 1 || !b1)
      return;

    int b = pointer - 2;
    while (b >= 0 && b < size - 1 && record.get(b))
      b--;

    record.set(b + 1, false);
    record.set(pointer, false);

    printStackState();
  }

  public static void stopRecord() {
    getInstance()._stopRecord();
  }

  public static void undo() {
    getInstance()._undo();
  }

  public static void redo() {
    getInstance()._redo();
  }

  public void _undo() {
    if (pointer <= 0) return;

    final int decrement = pointer % 2 > 0 ? 1 : 2;

    final boolean isBlocked = isBlocked();
    setBlocked(true);
    stack.get(pointer -= decrement).restore();
    setBlocked(isBlocked);

    printStackState();
    checkToolbarButtonState();
    setHasChange(true);

    if (record.get(pointer))
      undo();
  }

  public static void checkToolbarButtonState() {
    getInstance()._checkToolbarButtonState();
  }

  public void _checkToolbarButtonState() {
    if (PanelClassDiagram.getInstance() == null)
      return;

    Slyum.setEnableRedoButtons(pointer < stack.size() - 1);
    Slyum.setEnableUndoButtons(pointer > 0);
  }

  private void printStackState() {
    if (!Slyum.argumentIsChangeStackStatePrinted()) return;

    System.out.println("Etat de la pile");

    for (int i = 0; i < stack.size(); i++)
      System.out.println(i + " - " + record.get(i)
                         + (pointer == i ? " <--" : ""));

    System.out.println("--------------");
  }

}
