package classDiagram.relationships;

import classDiagram.components.Entity;

/**
 * This class, extent from Binary, represent an aggregation in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Aggregation extends Binary {

  /**
   * Create a new agregation between the entity source and target. Two news
   * roles will be created containing this association and an entity (source or
   * target). Entities will add this new role corresponding to this association.
   * The parameter directed define if the association is directed or not. An
   * association directed means, in UML, that an association can be read only in
   * one direction (represented by an arrow).
   * 
   * @param source
   *          the source entity
   * @param target
   *          the target entity
   * @param directed
   *          true if the association is directed; false otherwise
   */
  public Aggregation(Entity source, Entity target, NavigateDirection directed) {
    super(source, target, directed);
  }

  /**
   * Create a new agregation between the entity source and target. Two news
   * roles will be created containing this association and an entity (source or
   * target). Entities will add this new role corresponding to this association.
   * The parameter directed define if the association is directed or not. An
   * association directed means, in UML, that an association can be read only in
   * one direction (represented by an arrow). The id is not generated and must
   * be passed in parameter.
   * 
   * @param source
   *          the source entity
   * @param target
   *          the target entity
   * @param directed
   *          true if the association is directed; false otherwise
   * @param id
   *          the id for this association
   */
  public Aggregation(Entity source, Entity target, NavigateDirection directed,
          int id) {
    super(source, target, directed, id);
  }

  @Override
  public String getAssociationType() {
    return swing.XMLParser.Aggregation.AGGREGATE.toString();
  }
}
