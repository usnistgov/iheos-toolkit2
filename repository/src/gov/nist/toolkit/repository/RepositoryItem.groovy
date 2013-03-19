package gov.nist.toolkit.repository

class RepositoryItem {
	String name
	ConfigSlurper props
	Object content
	
	RepositoryItem(String name, ConfigSlurper props, Object content) {
		this.name = name
		this.props = props
		this.content = content
	}
}
