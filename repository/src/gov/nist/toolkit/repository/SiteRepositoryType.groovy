package gov.nist.toolkit.repository

class SiteRepositoryType extends RepositoryType {

	@Override
	public Map getRequiredMetadata() {
		return [
			'format1':'xml',    // fixed for all objects in this repository 
			'format2':'',       // not sure this is needed
			'owner':'',
			'scope':'Public|Private' // limited value set
			]
	}

	@Override
	public String getDefaultLocation() {
		return 'Sites';
	}

}
