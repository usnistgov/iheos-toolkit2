package gov.nist.toolkit.valsupport.engine;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.message.NullMessageValidator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Maintain collection of validation steps and run them on request.  New steps are added to the end of the list. 
 * Calling run will execute all un-executed steps in the order they are queued up. New
 * steps can be added any time and will be executed by the next call to the run method.
 * @author bill
 *
 */
public class MessageValidatorEngine {
	static Logger logger = Logger.getLogger(MessageValidatorEngine.class);

	public class ValidationStep {
		String stepName;
		MessageValidator validator;
		ErrorRecorder er;
		boolean ran = false;

		public String getStepName() { return stepName; }
		public ErrorRecorder getErrorRecorder() { return er; }
		public boolean hasErrors() { return er.hasErrors(); }

		public String toString() {
			StringBuffer buf = new StringBuffer();

			String className = validator.getClass().getName();
			try {
				className = className.substring(className.lastIndexOf(".") + 1);
			} catch (Exception e) {
				className = "Unknown";
			}


			buf
			//			.append("Step ")
			//			.append(stepName)
			//			.append(" type ")
			.append(className).append(" - ").append(stepName)
			.append(validator.toString());

			return buf.toString();
		}

		public String simLogHtmlToString() {
			StringBuffer buf = new StringBuffer();
			
			String className = validator.getClass().getName();
			try {
				className = className.substring(className.lastIndexOf(".") + 1);
			} catch (Exception e) {
				className = "Unknown";
			}
			
			
			buf
			//		.append("Step ")
			//		.append(stepName)
			//		.append(" type ")
			.append("<br /><br /><u>")
			.append(className).append(" - ").append(stepName).append("</u><br />")
			.append("<span style=\"margin-left: 20px\">").append(validator.toString()).append("</span>");
			
			return buf.toString();
		}
		
	}


	public MessageValidatorEngine() {}

	public class ValidationStepEnumeration implements Enumeration<ValidationStep> {
		List<ValidationStep> steps;
		int i = 0;

		ValidationStepEnumeration(List<ValidationStep> steps) {
			this.steps = steps;
		}

		public boolean hasMoreElements() {
			return i < steps.size();
		}

		public ValidationStep nextElement() {
			ValidationStep v = steps.get(i);
			i++;
			return v;
		}

	}

	List<ValidationStep> validationSteps = new ArrayList<ValidationStep>();

	public boolean hasErrors() {
		boolean error = false;
		for (ValidationStep vs : validationSteps) {
			if (vs.hasErrors())
				return true;
		}
		return false;
	}

	public int getErroredStepCount() {
		int cnt = 0;
		for (ValidationStep vs : validationSteps) {
			if (vs.hasErrors())
				if (vs.er.hasErrors())
					cnt++;
		}
		return cnt;
	}

	public ValidationStep getLastValidationStep() {
		int n = validationSteps.size();
		if (n == 0)
			return null;
		return validationSteps.get(n-1);
	}


	public Enumeration<ValidationStep> getValidationStepEnumeration() {
		return new ValidationStepEnumeration(validationSteps);
	}

	public MessageValidator findMessageValidator(String className) {
		for (ValidationStep vs : validationSteps) {
			MessageValidator mv = vs.validator;
			if (mv == null)
				continue;
			String clasname = mv.getClass().getName();
			if (clasname.endsWith(className))
				return mv;
		}
		return null;
	}

	public List<String> getValidatorNames() {
		List<String> names = new ArrayList<String>();
		for (ValidationStep vs : validationSteps) {
			MessageValidator mv = vs.validator;
			if (mv == null)
				continue;
			String clasname = mv.getClass().getName();
			int lastdot = clasname.lastIndexOf('.');
			if (lastdot > 0)
				clasname = clasname.substring(lastdot+1);
			names.add(clasname);
		}
		return names;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("MessageValidatorContext:\n");

		for (ValidationStep step : validationSteps) {
			buf.append(step.toString()).append("\n");
		}

		return buf.toString();
	}

	/**
	 * Add a new message validator to the validation queue.
	 * @param stepName Name of the validation step
	 * @param v the validator
	 * @param er its private ErrorRecorder
	 * @return the ValidationStep structure which is used internally to the engine
	 */
	public ValidationStep addMessageValidator(String stepName, MessageValidator v, ErrorRecorder er) {
		ValidationStep step = new ValidationStep();
		step.stepName = stepName;
		step.validator = v;
		step.er = er;
		validationSteps.add(step);
		logger.debug("MVC: Adding: " + v.getClass().getSimpleName());
		return step;
	}

	/**
	 * Short cut way to add an ErrorRecorder to the output stream without performing any validation.
	 * @param stepName name of the validation step
	 * @param er its private ErrorRecorder
	 */
	public void addErrorRecorder(String stepName, ErrorRecorder er) {
		ValidationStep step = addMessageValidator(stepName, new NullMessageValidator(new ValidationContext()), er);
		step.ran = true;
	}

	/**
	 * Execute all validators that are queued up but never run.
	 */
	public void run() {
		// this iteration is tricky since a step can add
		// new steps. This causes ConcurrentModificationException
		// if typical iterator is used.
		for (int i=0; i<validationSteps.size(); i++ ) {
			ValidationStep step = validationSteps.get(i);
			if (step.ran)
				continue;
			//			System.out.println("printf: MVC: Running: " + step.validator.getClass().getSimpleName());
			logger.debug("MVC: Running: " + step.validator.getClass().getSimpleName());
			step.ran = true;
			step.validator.run(step.er, this);
		}

	}

	public int getValidationStepCount() {
		return validationSteps.size();
	}

	public ValidationStep getValidationStep(int i) {
		return validationSteps.get(i);
	}
}
