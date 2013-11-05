package gov.nist.toolkit.dst.cmd.language;

import gov.nist.toolkit.dst.Config;
import gov.nist.toolkit.dst.cmd.Runable;

import java.util.ArrayList;
import java.util.List;

/**
 * Set site:  <sitename> site
 * 	where <sitename> is the name of the site to select
 * Selectr site: sites <b> site
 *  where sitesel displays sites by index (a, b, c), <b> choose site index b putting sitename on stack, and site selects 
 * @author bmajur
 *
 */

public class Interpreter {
	List<Object> stack = new ArrayList<Object>();
	static Interpreter interpreter = null;

	static public Interpreter get() {
		if (interpreter == null)
			interpreter = new Interpreter();
		return interpreter;
	}

	public Object pop() throws Exception {
		if (stack.size() == 0)
			throw new Exception("Stack empty");
		return stack.remove(stack.size()-1);
	}

	public void newWord(String token) {
		String capToken = Character.toUpperCase(token.charAt(0)) + token.substring(1);
		if (capToken.startsWith("["))
			return;
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass("gov.nist.toolkit.dst.commands." + capToken);
			System.out.println("found " + clazz.getName());
			Runable runable = (Runable) clazz.newInstance();
			runable.run();
			displayConfig();
			displayStack();
			return;
		} catch (ClassNotFoundException e) {
			stack.add(token);
		} catch (InstantiationException e) {
			stack.add(token);
		} catch (IllegalAccessException e) {
			stack.add(token);
		} catch (Exception e) {
			System.out.println("Runtime error: " + e.getMessage());
		}
		displayStack();
	}
	
	void displayStack() {
		Config.get().dst.displayStack(stack.toString() + " ");
	}

	void displayConfig() {
		Config.get().dst.displayTop(Config.get().forDisplay());
	}
	
}
