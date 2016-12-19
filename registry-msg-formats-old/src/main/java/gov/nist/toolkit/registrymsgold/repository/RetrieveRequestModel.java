package gov.nist.toolkit.registrymsgold.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class RetrieveRequestModel {
    List<RetrieveItemRequestModel> models = new ArrayList<>();

    public List<RetrieveItemRequestModel> getModel() {
        return models;
    }

    public void setModel(List<RetrieveItemRequestModel> model) {
        this.models = model;
    }

    public void add(RetrieveItemRequestModel aModel) {
        models.add(aModel);
    }

    public Set<String> getHomeCommunityIds() {
        Set<String> ids = new HashSet<>();

        for (RetrieveItemRequestModel item : models) {
            ids.add(item.getHomeId());
        }

        return ids;
    }

    public List<RetrieveItemRequestModel> getItemsForCommunity(String homeId) {
        List<RetrieveItemRequestModel> items = new ArrayList<>();
        if (homeId == null || homeId.equals("")) return items;
        for (RetrieveItemRequestModel model : models) {
            if (homeId.equals(model.getHomeId()))
                items.add(model);
        }
        return items;
    }
}
