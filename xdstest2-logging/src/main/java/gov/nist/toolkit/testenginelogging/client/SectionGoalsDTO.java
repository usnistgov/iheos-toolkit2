package gov.nist.toolkit.testenginelogging.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionGoalsDTO implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3222346795028663436L;
	public String sectionName;
	public List<StepGoalsDTO> stepGoalDTOs;
	
	public SectionGoalsDTO(String sectionName)	{
		this.sectionName = sectionName;
		stepGoalDTOs = new ArrayList<>();
	}
}
