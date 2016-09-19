package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

/**
 *
 */
public class TestStatistics {
    private int testCount = 0;
    private int testsRun = 0;
    private int testsWithErrors = 0;

    public void setTestCount(int count) {
        this.testCount = count;
    }

    public void addSuccessfulTest() {
        testsRun++;
    }

    public void addUnrunTest() {
    }

    public void addTestWithError() {
        testsWithErrors++;
    }

    public boolean isAllRun() {
        return testCount == testsRun;
    }

    public boolean isErrors() {
        return testsWithErrors > 0;
    }

    public boolean isNoneRun() {
        return testsRun == 0;
    }

    public String getReport() {
        StringBuilder buf = new StringBuilder();

        buf.append("Overview: ");
        buf.append(" Successful: ").append(testsRun);
        buf.append(" With Errors: ").append(testsWithErrors);
        buf.append(" Not Run: ").append(testCount - testsRun - testsWithErrors);

        return buf.toString();
    }

    public void clear() {
        testsRun = 0;
        testsWithErrors = 0;
    }

}
