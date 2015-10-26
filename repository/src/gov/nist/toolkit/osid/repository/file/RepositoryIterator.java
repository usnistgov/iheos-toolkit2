package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.repository.IRepository;

import java.util.Iterator;


public class RepositoryIterator implements Iterator<IRepository> {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public IRepository next() {
		return null;
	}

	@Override
	public void remove() {

	}

}
