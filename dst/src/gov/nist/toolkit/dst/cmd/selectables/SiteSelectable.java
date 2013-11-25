package gov.nist.toolkit.dst.cmd.selectables;

import gov.nist.toolkit.dst.cmd.Runable;

/**
 * Display site list, take selection, add to State.
 * @author bmajur
 *
 */
public class SiteSelectable  implements Runable  {
	
	public SiteSelectable() {
	}

	public int size() {
		return new SiteLoader().getSiteNames().size();
	}

	public String get(int i) {
		SiteLoader loader = new SiteLoader();
		if (i >= loader.getSiteNames().size())
			return null;
		return loader.getSiteNames().get(i);
	}

	@Override
	public void run() {
		// pop up selection, allow choosing, record choice
	}

}
