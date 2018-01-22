package gov.nist.toolkit.testenginelogging.logrepository;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.results.client.LogIdIOFormat;
import gov.nist.toolkit.results.client.LogIdType;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.IOException;

public class InfrastructureLogRepositoryFactory {

    /**
     * Orchestration is being used to run background services (like a FHIR server) that need
     * to be referenced by utilities.  But, tests use TestlogCache and utilites use sessionCache
     * so there is a search problem.  This builds a LogRepository (which is actually a form
     * of accessor function TO a log respository) representing a testSession (aka user). This is
     * turning into an element of a multipart search space for logs to support the UseReport
     * facility in the test client.
     * @param user
     * @param id
     * @return
     * @throws IOException
     */
    static public LogRepository getLogRepository(String user, TestInstance id) throws IOException {
        return LogRepositoryFactory.
                getLogRepository(
                        Installation.instance().testLogCache(),
                        user,
                        LogIdIOFormat.JAVA_SERIALIZATION,
                        LogIdType.SPECIFIC_ID,
                        id
                );
    }
}
