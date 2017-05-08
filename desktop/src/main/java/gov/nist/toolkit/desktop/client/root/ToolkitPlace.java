package gov.nist.toolkit.desktop.client.root;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class ToolkitPlace extends Place {

	private String toolkitName;

	public ToolkitPlace(String token) {
		this.toolkitName = token;
	}

	public ToolkitPlace() {
		super();
	}

	public String getWelcomeName() {
		return this.toolkitName;
	}

	public static class Tokenizer implements PlaceTokenizer<ToolkitPlace> {
		@Override
		public String getToken(ToolkitPlace place) {
			return place.getWelcomeName();
		}

		@Override
		public ToolkitPlace getPlace(String token) {
			return new ToolkitPlace(token);
		}
	}

}
