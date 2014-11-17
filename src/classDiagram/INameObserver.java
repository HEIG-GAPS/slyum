package classDiagram;

import java.util.Observer;

public interface INameObserver {
  public void setName(String name);
  public String getName();
  public void notifyObservers();
  public void addObserver(Observer o);
}
