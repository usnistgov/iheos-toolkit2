package gov.nist.toolkit.dst.commands;

import gov.nist.toolkit.dst.AlphaIndex;
import gov.nist.toolkit.dst.Config;
import gov.nist.toolkit.dst.cmd.Runable;
import gov.nist.toolkit.dst.cmd.language.Interpreter;

public class Setsite implements Runable {

	@Override
	public void run() throws Exception {
		String index = (String) Interpreter.get().pop();
		Config.get().put("Site", Config.get().getCurrentList().get(AlphaIndex.intOf(index)));
	}

}
