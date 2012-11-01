package gov.nist.toolkit.testenginelogging;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionGoals implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3222346795028663436L;
	public String sectionName;
	public List<StepGoals> stepGoals;
	
	public SectionGoals(String sectionName)	{
		this.sectionName = sectionName;
		stepGoals = new ArrayList<StepGoals>();
	}
}
