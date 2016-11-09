package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import utility.Utility;


public class SearchEngine {
  private static List<GraphicComponent> searchResults = new LinkedList<>();
  private static GraphicComponent current = null;
  
  public static GraphicComponent initialize(String searchQuery) {
    return SearchEngine.initialize(searchQuery, MultiViewManager.getSelectedGraphicView());
  }
  
  public static GraphicComponent initialize(String searchQuery, GraphicView graphicView) {
    if (searchQuery.isEmpty()) {
      searchResults.clear();
      return null;
    }
    
    searchResults = graphicView.getChildsRecursively().stream().filter(c -> 
        Utility.stripAccents(c.getFullString().toLowerCase()).contains(
            Utility.stripAccents(searchQuery.toLowerCase()))).collect(Collectors.toList());
    
    if (!searchResults.isEmpty()) {
      if (current == null || !searchResults.contains(current))
        current = searchResults.get(0);
    } else {
      current = null;
    }
    
    return current;
  }
  
  public static GraphicComponent current() {
    return current;
  }
  
  public static GraphicComponent next() {
    if (searchResults.isEmpty())
      return null;
    
    return current = searchResults.get((searchResults.indexOf(current) + 1) % searchResults.size());
  }
  
  public static GraphicComponent previous() {
    if (searchResults.isEmpty())
      return null;
    
    int currentIndex = searchResults.indexOf(current);
      
    return current = searchResults.get(currentIndex == 0 ? searchResults.size() - 1 : currentIndex - 1);
  }
}
