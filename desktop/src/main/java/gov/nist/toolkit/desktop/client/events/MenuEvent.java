package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

/**
 *
 */
public class MenuEvent extends Event<MenuEvent.MenuHandler> {

    public interface MenuHandler extends EventHandler {
        public void onMenuSelection(MenuEvent menuEvent);
    }


    public static final Type<MenuHandler> TYPE = new Type<MenuHandler>();
    private String menu;

    public MenuEvent(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return menu;
    }

    @Override
    public Type<MenuHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MenuHandler handler) {
        handler.onMenuSelection(this);
    }
}
