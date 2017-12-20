package gov.nist.toolkit.xdstools2.client.abstracts;


import com.google.gwt.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Generic class that handles the Presenter of the MVP design.
 * Used to build a Presenter for a given view.
 *
 * @param <V> Class that handles the View binded to this presenter
 */
public abstract class AbstractPresenter<V extends AbstractView<?>> {
    // Logger
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    // EventBus to catch and fire events
    @Inject
    protected EventBus eventBus;

    private ActivityDisplayer activityDisplayer = null;

    public ActivityDisplayer getActivityDisplayer() {
        return activityDisplayer;
    }

    public void setActivityDisplayer(ActivityDisplayer activityDisplayer) {
        this.activityDisplayer = activityDisplayer;
    }

    // Variable that handles the instance of the view binded to this presenter
    protected V view;

    private String title = "Tab";

    /**
     * Default "constructor"
     */
//    @Inject
    public AbstractPresenter() {
    }

    /**
     * Constructor.
     * @param eventBus Application eventbus bus.
     */
    @Inject
    public AbstractPresenter(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * Abstract method called by mvp before view.init() does not necessarily need to do something.
     */
    public abstract void init();

    /**
     * The title that will appear on the tab.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        if (activityDisplayer != null && title != null && !title.equals(""))
            activityDisplayer.setTitle(title);
    }

    public String getTitle() { return title; }

    /**
     * Abstract method called by mvp just before view.getDisplay()
     */
    public void start() {
    }

    /**
     * Getter that returns the view object binded to this presenter.
     * @return View
     */
    public V getView() {
        return view;
    }

    /**
     * Setter to change the view object binded to this presenter.
     * @param view
     */
    public void setView(V view) {
        this.view = view;
    }

    /**
     * Getter that return the eventbus bus of the presenter.
     * @return Event bus of the Presenter (same than the one of the Application)
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Method that sets the eventbus bus of the presenter (should be the same than the one of the Application)
     * @param eventBus
     */
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }


    // move to utilities class????
    public CommandContext getCommandContext() {
        return ClientUtils.INSTANCE.getCommandContext();
    }


    // This is meant to be overridden by sub-classes.  Is called when tab is revealed (selected).
    public void reveal() {}

}
