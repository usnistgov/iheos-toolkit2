package gov.nist.toolkit.dst.commands;

import gov.nist.toolkit.dst.Config;
import gov.nist.toolkit.dst.cmd.Runable;

public class Sites implements Runable {

	@Override
	public void run() throws Exception {
		Config.get().setCurrentList(Config.get().siteLoader.getSiteNames());
	}

}
