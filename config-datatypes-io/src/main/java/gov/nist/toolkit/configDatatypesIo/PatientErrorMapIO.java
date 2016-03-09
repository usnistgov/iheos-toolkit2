package gov.nist.toolkit.configDatatypesIo;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;

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
