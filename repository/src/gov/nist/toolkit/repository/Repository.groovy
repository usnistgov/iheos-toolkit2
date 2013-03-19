package gov.nist.toolkit.repository

import gov.nist.toolkit.installation.Installation

class Repository {
	RepositoryType type;
	RepositoryLocation location; // path relative to EC
	File root;   // directory holding repository
	
	def Repository(RepositoryType type) {
		this.type = type
		this.location = new SiteRepositoryLocation()
		this.root = new File(Installation.installation().externalCache.toString() 
			+ File.separator 
			+ location.getLocation())
	}
	
	def Repository(RepositoryType type, RepositoryLocation location) {
		this.type = type;
		this.location = location;
	}
	
	def exists() {	return root.exists()  }
	
	File getReqMetaFile() { return new File(root.toString() + File.separator + 'req.meta') }
	
	def isInitialized() {  return getReqMetaFile().exists() }
	
	def initialize() {
		if (exists() && isInitialized()) return
		
		root.mkdirs()
		
		ConfigSlurper config = new ConfigSlurper()
		config.setBinding(type.getRequiredMetadata())
		getReqMetaFile().withWriter { writer ->
			config.writeTo(writer)
		}
	}
	
	File getItemFile(RepositoryItem item) {
		initialize()
		def ext = item.props.format1
		return new File(root + File.separator + item.name + '.' + ext)
	}
	
	File getPropsFile(RepositoryItem item) {
		initialize()
		return new File(root + File.separator + item.name + '.prop')
	}
	
	def add(RepositoryItem item) {
		initialize()
		getPropsFile(item).withWriter { writer ->
			item.props.writeTo(writer)
		}
		getItemFile(item).withWriter { writer ->
			item.content.writeTo(writer)
		}
	}
	
	/**
	 * Return list item names in this repository
	 * @return
	 */
	def getItemNames() {
		def names = []
		if (!isInitialized()) return names
		root.eachFileMatch(~/.*.prop/) { file ->
			def name = getSimpleName(file)
			names.add(name)
		}
		return names
	}
	
	def getSimpleName(File f) {
		// remove extension
		def names = f.name.split("\\.")
		def name = names.size() > 1 ? (names - names[-1]).join('.') : names[0]
		// remove directory elements leaving just simple file name
		def dirs = name.split(File.separator)
		def simpleName = dirs.size() > 1 ? (dirs[-1]) : dirs[0]
		return simpleName
	}
}
