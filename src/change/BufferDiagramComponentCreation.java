/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package change;

import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;
import classDiagram.relationships.Binary;
import java.awt.font.GlyphMetrics;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import swing.PanelClassDiagram;


public class BufferDiagramComponentCreation implements Changeable {

  private boolean isCreated;
  private IDiagramComponent diagramComponent;

  public BufferDiagramComponentCreation(boolean isCreated, IDiagramComponent diagramComponent) {
    this.isCreated = isCreated;
    this.diagramComponent = diagramComponent;
  }

  @Override
  public void restore() {
    ClassDiagram classDiagram = PanelClassDiagram.getInstance().getClassDiagram();
    
    if (isCreated) {
      try {
        String className = diagramComponent.getClass().getSimpleName();
        classDiagram.getClass().getMethod("add" + className.substring(0, 1).toUpperCase() + className.substring(1), 
                                          diagramComponent.getClass(),
                                          boolean.class).invoke(classDiagram, diagramComponent, false);
        
      } catch (NoSuchMethodException ex) {
        Logger.getLogger(BufferDiagramComponentCreation.class.getName()).log(Level.SEVERE, null, ex);
      } catch (SecurityException ex) {
        Logger.getLogger(BufferDiagramComponentCreation.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
        Logger.getLogger(BufferDiagramComponentCreation.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalArgumentException ex) {
        Logger.getLogger(BufferDiagramComponentCreation.class.getName()).log(Level.SEVERE, null, ex);
      } catch (InvocationTargetException ex) {
        Logger.getLogger(BufferDiagramComponentCreation.class.getName()).log(Level.SEVERE, null, ex);
      }
    } else {
      final boolean isBlocked = Change.isBlocked();
      Change.setBlocked(true);
      classDiagram.removeComponent(diagramComponent);
      Change.setBlocked(isBlocked);
    } 
  }

  @Override
  public Object getAssociedComponent() {
    return diagramComponent;
  }
}

