package change;

import graphic.GraphicView;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
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
    _setHasChange(false);
    
    _printStackState();
  }

  public void _setHasChange(boolean changed) {
    _hasChange = changed;
    
    Slyum.setStarOnTitle(changed);
    
    _checkToolbarButtonState();
  }

  @Deprecated
  public Changeable _getLast() {
    return stack.getLast();
  }

  @Deprecated
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

    if (_isRecord()) addSinceLastRecord = true;

    pointer = stack.size() - 1;

    _printStackState();

    _checkToolbarButtonState();

    _setHasChange(true);
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

    final boolean isBlocked = _isBlocked();
    _setBlocked(true);
    stack.get(pointer += increment).restore();
    _setBlocked(isBlocked);

    _printStackState();

    _checkToolbarButtonState();

    _setHasChange(true);

    if (record.get(pointer)) _redo();
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

    _printStackState();
  }
  
  public void _undo() {
    if (pointer <= 0) return;
    
    final int decrement = pointer % 2 > 0 ? 1 : 2;
    
    final boolean isBlocked = _isBlocked();
    _setBlocked(true);
    stack.get(pointer -= decrement).restore();
    _setBlocked(isBlocked);
    
    _printStackState();
    _checkToolbarButtonState();
    _setHasChange(true);
    
    if (record.get(pointer))
      _undo();
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

  public static Change getCurrentChangeObject(GraphicView gv) {
    GraphicView currentGraphicView = MultiViewManager.getSelectedGraphicView();
    
    if (!changes.containsKey(currentGraphicView))
      changes.put(currentGraphicView, new Change());
    
    return changes.get(currentGraphicView);
  }
  
  public static GraphicView getGraphicViewAssociedWith(Object o) {
    List<GraphicView> gvs = MultiViewManager.getAllGraphicViews()
        .stream()
        .filter(gv -> gv.containsComponent(o) != null)
        .collect(Collectors.toList());
    
    if (gvs.size() > 0)
      return gvs.get(0);
    else
      return null;
  }
  
  public static Change getCurrentChangeObject() {
    return getCurrentChangeObject(MultiViewManager.getSelectedGraphicView());
  }

  public static Change getCurrentChangeObject(Changeable ch) {
    GraphicView gv = getGraphicViewAssociedWith(ch.getAssociedComponent());
    
    if (gv != null)
      return getCurrentChangeObject(gv);
    else
      return getCurrentChangeObject(MultiViewManager.getSelectedGraphicView());
  }

  public static void clearAll() {
    MultiViewManager.getAllGraphicViews().forEach(gv -> Change.clear(gv));
  }
  
  public static void clear(GraphicView gv) {
    getCurrentChangeObject(gv)._clear();
  }
  
  public static void setHasChangeGlobal(boolean changed) {
    MultiViewManager.getAllGraphicViews().forEach(gv -> setHasChange(changed, gv));
  }

  public static void setHasChange(boolean changed, GraphicView gv) {
    getCurrentChangeObject(gv)._setHasChange(changed);
  }

  @Deprecated
  public static Changeable getLast() {
    return getCurrentChangeObject()._getLast();
  }

  @Deprecated
  public static int getSize() {
    return getCurrentChangeObject()._getSize();
  }
  
  public static boolean hasChangeGlobal() {
    return MultiViewManager.getAllGraphicViews().stream().anyMatch(gv -> Change.hasChange(gv));
  }

  public static boolean hasChange(GraphicView gv) {
    return getCurrentChangeObject(gv)._hasChange();
  }

  public static boolean isBlocked(GraphicView gv) {
    return getCurrentChangeObject(gv)._isBlocked();
  }

  public static void setBlocked(boolean blocked, GraphicView gv) {
    getCurrentChangeObject(gv)._setBlocked(blocked);
  }

  public static boolean isRecord(GraphicView gv) {
    return getCurrentChangeObject(gv)._isRecord();
  }

  public static void pop(GraphicView gv) {
    getCurrentChangeObject(gv)._pop();
  }

  public static void push(Changeable ch) {
    getCurrentChangeObject(ch)._push(ch);
  }

  /**
   * Begin a record. A record merge all new pushes in a same group. When undo /
   * redo is called, all pushes into a group will be undo / redo at the same
   * time.
   */
  public static void record(GraphicView gv) {
    getCurrentChangeObject(gv)._record();
  }
  
  public static void redo(GraphicView gv) {
    getCurrentChangeObject(gv)._redo();
  }

  /**
   * Stop the current record. If no record is currently running this method have
   * no effect.
   */
  public static void stopRecord(GraphicView gv) {
    getCurrentChangeObject(gv)._stopRecord();
  }
  
  public static void undo(GraphicView gv) {
    getCurrentChangeObject(gv)._undo();
  }
  
  private static void printStackState(GraphicView gv) {
    getCurrentChangeObject(gv)._printStackState();
  }

  public static void checkToolbarButtonState() {
    getCurrentChangeObject()._checkToolbarButtonState();
  }
}
