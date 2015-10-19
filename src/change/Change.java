package change;

import graphic.GraphicView;
import java.util.HashMap;
import java.util.LinkedList;
import swing.MultiViewManager;
import swing.PanelClassDiagram;

import swing.Slyum;

public class Change {
  
  private static HashMap<GraphicView, Change> changes = new HashMap<>();
  
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

  public void _setHasChange(boolean changed) {
    _hasChange = changed;
    
    Slyum.setStarOnTitle(changed);
    
    checkToolbarButtonState();
  }

  public Changeable _getLast() {
    return stack.getLast();
  }

  public int _getSize() {
    return stack.size();
  }

  public boolean _hasChange() {
    return _hasChange;
  }

  public boolean _isBlocked() {
    return block;
  }

  public void _setBlocked(boolean blocked) {
    block = blocked;
  }

  public boolean _isRecord() {
    return isRecord;
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

  /**
   * Begin a record. A record merge all new pushes in a same group. When undo /
   * redo is called, all pushes into a group will be undo / redo at the same
   * time.
   */
  public void _record() {
    addSinceLastRecord = false;
    isRecord = true;
  }
  
  public void _redo() {
    if (pointer >= stack.size() - 1) return;

    final int increment = pointer % 2 == 0 ? 1 : 2;

    final boolean isBlocked = Change.isBlocked();
    setBlocked(true);
    stack.get(pointer += increment).restore();
    setBlocked(isBlocked);

    printStackState();

    checkToolbarButtonState();

    setHasChange(true);

    if (record.get(pointer)) redo();
  }

  /**
   * Stop the current record. If no record is currently running this method have
   * no effect.
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
  
  public void _undo() {
    if (pointer <= 0) return;
    
    final int decrement = pointer % 2 > 0 ? 1 : 2;
    
    final boolean isBlocked = Change.isBlocked();
    setBlocked(true);
    stack.get(pointer -= decrement).restore();
    setBlocked(isBlocked);
    
    printStackState();
    
    checkToolbarButtonState();

    setHasChange(true);
    
    if (record.get(pointer))
      
      undo();
  }

  public void _checkToolbarButtonState() {
    if (PanelClassDiagram.getInstance() == null)
      return;
    
    Slyum.setEnableRedoButtons(pointer < stack.size() - 1);
    Slyum.setEnableUndoButtons(pointer > 0);
  }
  
  private void _printStackState() {
    if (!Slyum.argumentIsChangeStackStatePrinted()) return;
    
    System.out.println("Etat de la pile");
    
    for (int i = 0; i < stack.size(); i++)
      
      System.out.println(i + " - " + record.get(i)
              + (pointer == i ? " <--" : ""));

    System.out.println("--------------");
  }

  public static Change getCurrentChangeObject() {
    GraphicView currentGraphicView = MultiViewManager.getSelectedGraphicView();
    
    if (!changes.containsKey(currentGraphicView))
      changes.put(currentGraphicView, new Change());
    
    return changes.get(currentGraphicView);
  }

  public static void clear() {
    getCurrentChangeObject()._clear();
  }

  public static void setHasChange(boolean changed) {
    getCurrentChangeObject()._setHasChange(changed);
  }

  public static Changeable getLast() {
    return getCurrentChangeObject()._getLast();
  }

  public static int getSize() {
    return getCurrentChangeObject()._getSize();
  }

  public static boolean hasChange() {
    return getCurrentChangeObject()._hasChange();
  }

  public static boolean isBlocked() {
    return getCurrentChangeObject()._isBlocked();
  }

  public static void setBlocked(boolean blocked) {
    getCurrentChangeObject()._setBlocked(blocked);
  }

  public static boolean isRecord() {
    return getCurrentChangeObject()._isRecord();
  }

  public static void pop() {
    getCurrentChangeObject()._pop();
  }

  public static void push(Changeable ch) {
    getCurrentChangeObject()._push(ch);
  }

  /**
   * Begin a record. A record merge all new pushes in a same group. When undo /
   * redo is called, all pushes into a group will be undo / redo at the same
   * time.
   */
  public static void record() {
    getCurrentChangeObject()._record();
  }
  
  public static void redo() {
    getCurrentChangeObject()._redo();
  }

  /**
   * Stop the current record. If no record is currently running this method have
   * no effect.
   */
  public static void stopRecord() {
    getCurrentChangeObject()._stopRecord();
  }
  
  public static void undo() {
    getCurrentChangeObject()._undo();
  }
  
  private static void printStackState() {
    getCurrentChangeObject()._printStackState();
  }

  public static void checkToolbarButtonState() {
    getCurrentChangeObject()._checkToolbarButtonState();
  }
}
