package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;

/**
 * Event thrown when another environment is selected.
 * @see TransactionOfferingsReloadedEventHandler
 * Created by onh2 on 8/30/16.
 */
public class TransactionOfferingsReloadedEvent extends GwtEvent<TransactionOfferingsReloadedEvent.TransactionOfferingsReloadedEventHandler> {

    public static final Type<TransactionOfferingsReloadedEventHandler> TYPE = new Type<>();
    private final TransactionOfferings transactionOfferings;

    /**
     * Event constructor.
     * @param transactionOfferings
     */
    public TransactionOfferingsReloadedEvent(TransactionOfferings transactionOfferings) {
        this.transactionOfferings = transactionOfferings;
    }

    /**
     * @return currently selected environment.
     */
    public TransactionOfferings getTransactionOfferings(){
        return transactionOfferings;
    }

    @Override
    public Type<TransactionOfferingsReloadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TransactionOfferingsReloadedEventHandler handler) {
        handler.onTransactionOfferingsReloaded(this);
    }

    /**
     * Event handler interface for an Environment change (environment selection changed).
     * @see TransactionOfferingsReloadedEvent
     */
    public interface TransactionOfferingsReloadedEventHandler extends EventHandler {
        /**
         * Actions to be executed on the class that catches the event {@link TransactionOfferingsReloadedEvent EnvironmentChangedEvent}
         * when a new environment is selected.
         */
        void onTransactionOfferingsReloaded(TransactionOfferingsReloadedEvent event);
    }
}
