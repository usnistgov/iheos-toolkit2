package gov.nist.toolkit.configDatatypes.server;

import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 *
 */
public class PatientErrorMapIO {
    static private ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    public static String marshal(PatientErrorMap patientErrorMap) throws IOException {
        return mapper.writeValueAsString(patientErrorMap);
    }

    public static PatientErrorMap unmarshal(String stream) throws IOException {
        return mapper.readValue(stream, PatientErrorMap.class);
    }
}
