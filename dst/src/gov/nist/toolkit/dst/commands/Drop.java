package gov.nist.toolkit.dst.commands;

import gov.nist.toolkit.dst.cmd.Runable;
import gov.nist.toolkit.dst.cmd.language.Interpreter;

public class Drop implements Runable {

	@Override
	public void run() throws Exception {
		Interpreter.get().pop();	}

}
