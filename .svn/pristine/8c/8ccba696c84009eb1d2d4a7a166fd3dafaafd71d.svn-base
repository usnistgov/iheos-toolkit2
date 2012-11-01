package gov.nist.toolkit.xdstools2.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class FeatureManager {
	static List<String> features = null;
	
	public interface FeaturesLoadedNotificationRecipient {
		void featuresLoadedCallback();
	}
	
	static List<FeaturesLoadedNotificationRecipient> callbacks = new ArrayList<FeaturesLoadedNotificationRecipient>();
	
	public void addCallback(FeaturesLoadedNotificationRecipient caller) {
		if (features == null)
			callbacks.add(caller);    // not done loading yet
		else
			caller.featuresLoadedCallback();
	}
	
	public void load(ToolkitServiceAsync toolkitService) {
		if (features == null) {
			toolkitService.getFeatureList(loadFeatureListCallback);
		}
	}
	
	protected AsyncCallback<List<String>> loadFeatureListCallback = new AsyncCallback<List<String>> () {

		public void onFailure(Throwable caught) {
			new PopupMessage("FeatureManager: " + caught.getMessage());
		}

		public void onSuccess(List<String> result) {
			features = result;
			for (FeaturesLoadedNotificationRecipient callback : callbacks)
				callback.featuresLoadedCallback();
		}
		
	};
	
	public static boolean isFeatureEnabled(String featureName) {
		return features != null && features.contains(featureName);
	}
	
}
