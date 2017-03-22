package com.google.gwt.user.client.rpc;

/**
 * Since gwt-user cannot be loaded into a normal (non-GWT) WAR because it re-defines javax/servlet/Servlet.class
 * this interface is defined here to satisfy the classloader.  This WAR does not use
 * any GWT features but is does rely on server code that includes the isSerializable marker interface.
 */
public interface IsSerializable {
}
