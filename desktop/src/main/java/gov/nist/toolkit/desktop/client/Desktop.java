package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import gov.nist.toolkit.desktop.client.content.TkShell;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Desktop implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */

  private final Messages messages = GWT.create(Messages.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    RootLayoutPanel root = RootLayoutPanel.get();
    TkShell shell = new TkShell();
    root.add(shell);
  }
}
