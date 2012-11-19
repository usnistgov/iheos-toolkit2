package gov.nist.toolkit.testengine;

public interface ErrorReportingInterface {
	public void fail(String msg);
	public void setInContext(String title, Object value);
}
