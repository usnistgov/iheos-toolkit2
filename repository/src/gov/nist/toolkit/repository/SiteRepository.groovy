package gov.nist.toolkit.repository

class SiteRepository {
	// This gives a connection to site repository
	private SiteRepositoryType siteType = new SiteRepositoryType()
	private Repository rep = new Repository(siteType)
	
	enum Scope {PUBLIC, PRIVATE};
	
	SiteRepositoryItem addItem(String name, Scope scope, String owner, byte[] content) {
		def props = new Properties(siteType.getRequiredMetadata())
		props.setProperty('owner', owner)
		props.setProperty('scope', scope.toString())
		def item = new RepositoryItem(name, props, content)
		rep.addItem(item)
		return new SiteRepositoryItem(item)
	}
	
	SiteRepositoryItem getItem(String itemName) {
		RepositoryItem item = rep.getItem(itemName)
		return new SiteRepositoryItem(item)
	}
	
	List<String> getPublicSiteNames() {
		return rep.getItemNamesWithProperty('scope', Scope.PUBLIC.toString())
	}
	
	List<String> getSiteNamesFor(String owner) {
		def owned = rep.getItemNamesWithProperty('owner', owner)
		Set ownedSet = []
		ownedSet.addAll(owned)
		def pub = getPublicSiteNames()
		ownedSet.addAll(pub)
		return new ArrayList<String>(ownedSet)
	}
	
}
