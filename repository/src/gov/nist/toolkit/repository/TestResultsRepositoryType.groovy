package gov.nist.toolkit.repository

import java.util.Map;

class TestResultsRepositoryType extends RepositoryType {
	@Override
	public Map getRequiredMetadata() {
		return [
			format1:'',    	// may vary
			format2:'',    	// 
			testId:''      	// this is really assigned to the entire test event, 
							// not just this test artifact
			]
	}

	@Override
	public String getDefaultLocation() {
		return 'TestResults';
	}

}
