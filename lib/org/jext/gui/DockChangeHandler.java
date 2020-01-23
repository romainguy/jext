package org.jext.gui;

public interface DockChangeHandler {
  /**
   * Override to do special handling of the change of docking status.
   * @param where    old docking status
   * @param newWhere new docking status
   */
  public void dockChangeHandler(int where, int newWhere);
}
