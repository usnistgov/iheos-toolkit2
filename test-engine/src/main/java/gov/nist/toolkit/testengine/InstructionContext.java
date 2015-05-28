package gov.nist.toolkit.testengine;

import gov.nist.toolkit.testengine.transactions.BasicTransaction;


public class InstructionContext extends BasicContext {
	BasicTransaction transaction = null;
	
	public String getName() {
		return get("instruction_name");
	}

	public void setName(String name) {
		set("instruction_name", name);
	}

	public String error(String msg) {
		return "[Step " + get("step_id") + " Instruction " + get("instruction_name") + "]" + msg;
	}
	
//	public String getExpectedErrorMessage() {
//		return step().getExpectedErrorMessage();
//	}
//
//	public void setExpectedErrorMessage(String expectedErrorMessage) {
//		step().setExpectedErrorMessage(expectedErrorMessage);
//	}

	public InstructionContext(StepContext step) {
		super(step);   // parent_context
	}
	
	StepContext step() { return (StepContext) parent_context; }


}
