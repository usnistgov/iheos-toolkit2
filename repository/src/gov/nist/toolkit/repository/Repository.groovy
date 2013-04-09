package gov.nist.toolkit.repository

import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.utilities.io.Io

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
	
	private def exists() {	return root.exists()  }
	
	private File getReqMetaFile() { return new File(root.toString() + File.separator + 'req.meta.txt') }
	
	private def isInitialized() {  return getReqMetaFile().exists() }
	
	private def initialize() {
		if (exists() && isInitialized()) return
		
		root.mkdirs()
		
		def props = mapToProperties(type.getRequiredMetadata())
		saveProperties(props, getReqMetaFile())
	}
	
	private File getItemFile(RepositoryItem item) {
		initialize()
		def ext = item.props.format1
		return new File(root, item.name + '.' + ext)
	}
	
	private File getPropsFile(RepositoryItem item) {
		initialize()
		return new File(root, item.name + '.prop.txt')
	}
	
	private File getPropsFile(String name) {
		initialize()
		return new File(root, name + '.prop.txt')
	}
	
	def addItem(RepositoryItem item) {
		initialize()
		saveProperties(item.props, getPropsFile(item))

		def fos = null
		try {
			fos = new FileOutputStream(getItemFile(item))
			fos.write(item.content)
		} finally {
			fos?.close()
		}
	}
	
	RepositoryItem getItem(String name) {
		initialize()
		def item = new RepositoryItem(name)
		
		item.props = loadProperties(getPropsFile(item))
		
		def fis = null
		try {
			fis = new FileInputStream(getItemFile(item))
			item.content = Io.getBytesFromInputStream(fis)
		} finally {
			fis?.close()
		}
		
		return item
	}
	
	/**
	 * Return list item names in this repository
	 * @return
	 */
	List<String> getItemNames() {
		def names = []
		if (!isInitialized()) return names
		root.eachFileMatch(~/.*.prop.txt/) { file ->
			def name = getSimpleName(file)
			names.add(name)
		}
		return names
	}
	
	List<String> getItemNamesWithProperty(String propName, String value) {
		def allItemNames = getItemNames()
		def namesWithProp = []
		allItemNames.each { name ->
			Properties props = loadProperties(getPropsFile(name))
			def propValue = props.getProperty(propName)
			if (propValue == value) namesWithProp << name
		}
		return namesWithProp
	}
	
	/*
	 * Return simple name of file. File names are formatted as
	 * /usr/dir/simpleName.a.b.c  etc.  Remove the /usr/dir/  
	 * and .a.b.c parts and return just simpleName
	 */
	private static String getSimpleName(File f) {
		// remove directory elements leaving just the file name
		def dirs = f.name.split(File.separator)
		def name = dirs.size() > 1 ? (dirs[-1]) : dirs[0]
		// remove extension
		def names = name.split("\\.")
		def simpleName = names.size() > 1 ? names[0] : name
		return simpleName
	}
	
	private static void saveProperties(Properties props, File file) {
		file.withWriter{ writer ->
			props.store(writer,'')
		}
	}
	
	private static Properties loadProperties(File file) {
		Properties props = new Properties()
		file.withReader { rdr ->
			props.load(rdr)
		}
		return props
	}

	private static Properties mapToProperties(Map map) {
		Properties p = new Properties()
		map.entrySet().each { entry ->
			p.put(entry.getKey(), entry.getValue())
		}
		return p
	}	
	
	private static Map<String, String> propertiesToMap(Properties p) {
		def map = [:]
		Enumeration e = p.keys()
		while(e.hasMoreElements()) {
			def key = e.nextElement()
			map.put(key, p.getProperty(key))
		}
		return map
	}
	
}
