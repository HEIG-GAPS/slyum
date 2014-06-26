/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public interface WatchFileListener {
  void fileModified();
  void fileDeleted();
}
