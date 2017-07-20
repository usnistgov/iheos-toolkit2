package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.user.client.ui.Widget;

/**
 * Generic class that handles the Model View Presenter design.
 * Used to build a set of View and Presenter handling a specific Model.
 *
 * @param <M> Model class
 * @param <V> Class that handles the View
 * @param <P> Class that handles the Presenter
 */
public abstract class AbstractMVP<M, V extends AbstractView<P>, P extends AbstractPresenter<V>> {

	// Variable for the instance of the View of the instantiated MVP
	private V view;
	// Variable for the instance of the Presenter of the instantiated MVP
	private P presenter;

	/**
	 * Default "constructor".
 	 */
	public AbstractMVP() {
	}

	/**
	 * Abstract method whose implementation must build the view for this MVP.
	 * @return View
	 */
	public abstract V buildView();

	/**
	 * Abstract method whose implementation must build the presenter for this MVP.
	 * @return Presenter
	 */
	public abstract P buildPresenter();

	/**
	 * Method that initialize the MVP, instancing the view and the presenter (using abstract methods).
	 */
	public void init() {
		// build
		view = buildView();
		presenter = buildPresenter();

		// init
		presenter.setView(view);
		view.setPresenter(presenter);

		presenter.init();
		view.init();

		// start
		start();
	}

	/**
	 * Method to start the view and the presenter.
	 * It calls for initialization method from both the presenter and the view.
	 */
	public void start() {
		presenter.start();
		view.start();
	}

	/**
	 * Method that return the view as a Widget
	 * @return View as a Widget
	 */
	public Widget getDisplay() {
		assert(view != null);
		return view.asWidget();
	}

	/**
	 * Getter that returns the View object of the MVP.
	 * @return View
	 */
	public V getView() {
		return view;
	}

	/**
	 * Setter that changes the view object of the MVP.
	 * @param view
	 */
	public void setView(V view) {
		this.view = view;
	}

	/**
	 * Getter that returns the Presenter object of the MVP.
	 * @return Presenter
	 */
	public P getPresenter() {
		return presenter;
	}

	/**
	 * Setter that changes the Presenter object of the MVP.
	 * @param presenter
	 */
	public void setPresenter(P presenter) {
		this.presenter = presenter;
	}

}
