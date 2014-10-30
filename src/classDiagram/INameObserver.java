/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package classDiagram;

import java.util.Observer;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public interface INameObserver {
  public void setName(String name);
  public String getName();
  public void notifyObservers();
  public void addObserver(Observer o);
}
