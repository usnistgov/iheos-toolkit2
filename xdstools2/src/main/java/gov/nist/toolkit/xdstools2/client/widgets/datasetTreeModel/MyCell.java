package gov.nist.toolkit.xdstools2.client.widgets.datasetTreeModel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import elemental.events.Event;

/**
 *
 */
class MyCell extends AbstractCell<Resource> {
    DatasetTreeModel model;

    MyCell(DatasetTreeModel model) {
        super(Event.CLICK);
        this.model = model;
    }

    @Override
    public void render(Context context, Resource resource, SafeHtmlBuilder safeHtmlBuilder) {
        if (resource == null) return;
        safeHtmlBuilder.appendEscaped(resource.getName());
    }

    @Override
    public void onBrowserEvent(Context context, com.google.gwt.dom.client.Element parent, Resource value, NativeEvent event, ValueUpdater<Resource> valueUpdater) {
        String eventType = event.getType();
        if ("click".equals(eventType)) {
            this.onClick(context, parent, value, event, valueUpdater);
        }

    }

    void onClick(Context context, com.google.gwt.dom.client.Element parent, Resource resource, NativeEvent event, ValueUpdater<Resource> valueUpdater) {
        model.doSelect(resource.getDatasetElement());
    }
}
