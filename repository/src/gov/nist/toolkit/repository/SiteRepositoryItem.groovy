package gov.nist.toolkit.repository

class SiteRepositoryItem {
	private RepositoryItem item
	
	SiteRepositoryItem(RepositoryItem item) { this.item = item }
	
	String getName() { 
		def name =  item.name
		assert name != null
		return name 
	}
	
	String getOwner() { 
		def owner = item?.props?.getProperty('owner')
		assert owner != null
		return owner
	}
	
	SiteRepository.Scope getScope() { 
		def scope = item?.props?.getProperty('scope')
		assert scope != null
		return scope  
	}
	
	String getXMLAsString() {  
		byte[] content = item?.content
		assert content != null
		return new String(content)
	}
	
	// since this is just a wrapper for RepositoryItem so
	// rely on RepositoryItem to do comparison
	boolean equals(SiteRepositoryItem siteRepItem) {
		return siteRepItem.item.equals(this.item)
	}
}
