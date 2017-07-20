package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Generic class that handles the View of the MVP design.
 * Used to build a View that work with a given type of presenter.
 * @param <P> Class that handles the Presenter binded to this View.
 */
public abstract class AbstractView<P extends AbstractPresenter<?>> implements IsWidget {
    // class logger
    protected final Logger logger = Logger.getLogger(this.getClass().getName());

    protected P presenter;
    protected Widget ui;
    private SimpleLayoutPanel cp;

    protected Map<String, Widget> pathToWidgetsMap;

    // instance
    public AbstractView() {
    }

    /**
     * This method initialize the view by building the view.
     * (it calls the abstract method {@link #buildUI()} and {@link #bindUI()} ).
     */
    public void init() {
        cp = new SimpleLayoutPanel();
//        cp.setHeaderVisible(false);
//        cp.setBorders(false);
        pathToWidgetsMap = getPathToWidgetsMap();
        ui = buildUI();
        cp.setWidget(ui);
        bindUI();

        // FIXME find a way to resolve resizing issues with fields Map solution is supposed to work but I don't know how...
        /*
        ResizeHandler resizeHandler=new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent eventbus) {
                for(Widget w : pathToWidgetsMap.values()){
                    //  w.setWidth("200");
                    cp.forceLayout();
                    cp.setResize(true);
                }
            }
        };
        cp.addResizeHandler(resizeHandler);
        Window.addResizeHandler(resizeHandler);
        */
    }

    /**
     * This is an abstract method supposed to build a collection of objects mapping a String key
     * to a Widget.
     * @return Map of widgets.
     */
    protected abstract Map<String, Widget> getPathToWidgetsMap();

    public void start() {
    }

    /**
     * This is an abstract method supposed to construct the view as a widget.
     * @return view as a Widget.
     */
    protected abstract Widget buildUI();

    /**
     * This is an abstract method supposed to bind the different widgets of the view
     * with action defined in the presenter.
     */
    protected abstract void bindUI();


    // impl
    @Override
    public Widget asWidget() {
        if (ui == null || cp == null) {
            init();
        }
        return cp;
    }

    // getter / setter
    public P getPresenter() {
        return presenter;
    }

    public void setPresenter(P presenter) {
        this.presenter = presenter;
    }
}
