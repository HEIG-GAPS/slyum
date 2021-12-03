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
   * @param relation the {@link Relation} to change.
   * @param source the source or the target who need to be updated.
   * @param target the new {@link Entity} to be changed.
   */
  public static void changeRelation(final Relation relation, final boolean source, final Entity target) {
    if (source)
      relation.setSource(target);
    else
      relation.setTarget(target);
    relation.notifyObservers();
  }

  /**
   * Change the entities for the specified association.
   *
   * @param association the association to change.
   * @param source the current Role that must be changed.
   * @param target the new Role to be changed.
   */
  public static void changeAssociation(final Association association, final Role source, final Role target) {
    /* Nothing to do */
  }

}
