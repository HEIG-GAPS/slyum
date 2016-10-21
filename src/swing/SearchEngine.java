package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;
import java.util.LinkedList;


public class SearchEngine {
  private static LinkedList<GraphicComponent> searchResults;
  
  
  public static GraphicComponent searchComponent(String searchQuery) {
    return searchComponent(searchQuery, MultiViewManager.getSelectedGraphicView());
  }
  
  public static GraphicComponent searchComponent(String searchQuery, GraphicView graphicView) {
    graphicView.getAllComponents().stream().forEach(c -> System.out.println(c.toString()));
    return null;
  }
}
