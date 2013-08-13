package classDiagram.relationships;

import classDiagram.components.Entity;

/**
 * This class allow to change component's relations.
 * 
 * @author David Miserez
 */
public class RelationChanger {

  /**
   * Change the entities for the specified relation.
   * 
   * @param association
   *          the association to change.
   * @param source
   *          is the source or the target who need to be update?
   * @param target
   *          the new Entity to be changed.
   * @return if the change were successful.
   */
  public static void changeRelation(Relation relation, boolean source,
          Entity target) {
    if (source)
      relation.setSource(target);
    else
      relation.setTarget(target);
    relation.notifyObservers();
  }

  /**
   * Change the entities for the specified association.
   * 
   * @param association
   *          the association to change.
   * @param source
   *          the current Role that must be changed.
   * @param target
   *          the new Role to be changed.
   * @return if the change were successful.
   */
  public static void changeAssociation(Association association, Role source,
          Role target) {

  }
}
