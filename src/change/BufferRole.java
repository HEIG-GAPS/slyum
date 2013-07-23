package change;

import classDiagram.components.Visibility;
import classDiagram.relationships.Multiplicity;
import classDiagram.relationships.Role;

public class BufferRole implements Changeable
{
  private Role role;
  private String name;
  private String visibility;
  private int multiLower, multiHigher;

  public BufferRole(Role role, String name, String visibility, int lower, int higher)
  {
    this.role = role;
    this.name = name;
    this.visibility = visibility;
    this.multiLower = lower;
    this.multiHigher = higher;
  }
  
  @Override
  public void restore()
  {
    role.setName(name);
    role.setVisibility(Visibility.valueOf(visibility));
    role.setMultiplicity(new Multiplicity(multiLower, multiHigher));
    
    role.notifyObservers();
    role.getMultiplicity().notifyObservers();
  }

}
