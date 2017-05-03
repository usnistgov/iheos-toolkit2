package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import gov.nist.toolkit.desktop.client.content.TkShell;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Desktop implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    RootLayoutPanel root = RootLayoutPanel.get();
    TkShell shell = new TkShell();
    root.add(shell);
  }
}
