package gov.nist.toolkit.repository

class RepositoryItem {
	String name
	Properties props
	byte[] content

	RepositoryItem(name) { this.name = name }

	RepositoryItem(String name, Properties props, byte[] content) {
		assert name.indexOf('.') == -1
		this.name = name
		this.props = props
		this.content = content
	}

	boolean equals(RepositoryItem item) {
		assert name == item.name
		assert props == item.props
		assert content == item.content
		return name == item.name &&
				props == item.props &&
				content == item.content
	}
}
