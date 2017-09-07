package gov.nist.toolkit.xdstools2.client.abstracts;

public class GenericMVP<M, V extends AbstractView<P>, P extends AbstractPresenter<V>>
		extends AbstractMVP<M, V, P> {

	private final V view;
	private final P presenter;

	// instance
	public GenericMVP(V view, P presenter) {
		this.view = view;
		this.presenter = presenter;
	}

	@Override
	public P buildPresenter() {
		return presenter;
	}

	@Override
	public V buildView() {
		return view;
	}

	public V getView() { return view; }

	public P getPresenter() { return presenter; }

}
