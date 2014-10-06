/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphic.textbox;

import classDiagram.INameObserver;
import graphic.GraphicView;
import graphic.entity.EntityView;
import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Observable;
import java.util.Observer;

/**
 * This class displays the diagram's name according to the UML specifications.
 * It permit to change it too.
 * @author David Miserez
 * @version 1.0
 */
public class TextBoxDiagramName extends TextBox implements Observer {
  
  final int DEPLACEMENT_CURVE = 30;
  final int MINIMAL_WIDTH = 30;
  
  final String EMPTY_MESSAGE = "Enter the diagram's name";
  
  final INameObserver modelName;

  public TextBoxDiagramName(GraphicView parent, INameObserver modelName) {
    super(parent, modelName.getName());
    setHideWhileEditing(false);
    this.modelName = modelName;
    modelName.addObserver(this);
  }

  @Override
  public void editing() {
    setMouseHover(false);
    super.editing();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    if (!isVisible()) return;
    super.gMouseClicked(e);
    editing();
  }

  @Override
  public void gMouseEntered(MouseEvent e) {
    if (!isVisible()) return;
    super.gMouseEntered(e);
  }

  @Override
  public void gMouseExited(MouseEvent e) {
    if (!isVisible()) return;
    super.gMouseExited(e);
  }

  @Override
  public boolean isAtPosition(Point mouse) {
    if (!isVisible()) return false;
    return super.isAtPosition(mouse);
  }

  @Override
  protected void createEffectivFont() {
    super.createEffectivFont();
    effectivFont = effectivFont.deriveFont(16f);
  }

  @Override
  public void delete() {
    // Cannot be deleted.
  }
  
  @Override
  public void paintComponent(Graphics2D g2) {
    
    // Compute text width.
    createEffectivFont();
    FontMetrics metrics = g2.getFontMetrics(effectivFont);
    int adv = metrics.stringWidth(getText());
    
    bounds = 
        new Rectangle(5, 5, (adv < MINIMAL_WIDTH ? MINIMAL_WIDTH : adv), 20);
    
    paintComponentAt(g2, new Point(0, 0));
  }
  
  @Override
  public void paintComponentAt(Graphics2D g2, Point location) {
    
    if (!isVisible())
      return;
    
    // Compute locations
    Rectangle bndBackground = new Rectangle(
        location.x,
        location.y,
        bounds.width + 10 + DEPLACEMENT_CURVE / 2, 
        bounds.height + 10);
    
    Point origin = location,
          deplacement = new Point(bndBackground.x + bndBackground.width,
                                  bndBackground.y + bndBackground.height);
    
    // Compute the shape of the background.
    Path2D background = new Path2D.Float();
    background.moveTo(origin.x, origin.y);
    background.lineTo(origin.x, deplacement.y);
    background.lineTo(deplacement.x - DEPLACEMENT_CURVE, deplacement.y);
    background.quadTo(deplacement.x, deplacement.y,
                      deplacement.x, origin.y);
    background.lineTo(origin.x, origin.y);
    
    // Draw background and border.
    g2.setColor(EntityView.getBasicColor());
    g2.fill(background);
    
    g2.setStroke(new BasicStroke(EntityView.BORDER_WIDTH));
    g2.setColor(EntityView.DEFAULT_BORDER_COLOR);
    g2.draw(background);
    
    super.paintComponentAt(g2, 
        new Point(origin.x + bounds.x, origin.y + bounds.y));
  }

  @Override
  public boolean isVisible() {
    return !(pictureMode && isEmpty()) && super.isVisible();
  }

  @Override
  public void setBounds(Rectangle bounds) {
    if (bounds == null) throw new IllegalArgumentException("bounds is null");

    this.bounds = new Rectangle(bounds);
  }

  @Override
  public void setText(String text) {
    modelName.setName(text);
    modelName.notifyObservers();
  }

  @Override
  public String getText() {
    String text = super.getText();
    if (isEmpty())
      text = EMPTY_MESSAGE;
    return text;
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof INameObserver)
      super.setText(((INameObserver)o).getName());
  }

  @Override
  public Rectangle getBounds() {
    Rectangle bnd = super.getBounds();
    if (bnd.width == 0) bnd.width = MINIMAL_WIDTH;
    return bnd;
  }

  @Override
  public String getEditingText() {
    if (isEmpty())
      return "";
    return super.getEditingText();
  }
  
  public boolean isEmpty() {
    String text = super.getText();
    return text.equals(EMPTY_MESSAGE) || text.isEmpty();
  }
}
