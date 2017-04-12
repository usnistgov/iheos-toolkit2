package gov.nist.toolkit.valregmetadata.datatype;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

/**
 *
 */
public class DtmFormatWithMinSize extends DtmFormat {
    int minSize;

    public DtmFormatWithMinSize(ErrorRecorder er, String context, String resource, int minSize) {
        super(er, context, resource);
        this.minSize = minSize;
    }

    public void validate(String input) {
        super.validate(input);
        int size = input.length();
        if (size < minSize)
            er.err(XdsErrorCode.Code.XDSRegistryMetadataError, context + ": " + input + " HL7 DateTime required to be " + minSize + " digits long", this, getResource("ITI TF-3: Table 4.1-3"));
    }
}
