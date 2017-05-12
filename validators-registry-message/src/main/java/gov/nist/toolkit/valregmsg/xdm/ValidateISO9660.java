package gov.nist.toolkit.valregmsg.xdm;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;

/**
 * https://en.wikipedia.org/wiki/ISO_9660
 * http://www.ecma-international.org/publications/standards/Ecma-119.htm
 */
public class ValidateISO9660 {
    IErrorRecorder er;

    public ValidateISO9660(IErrorRecorder er) {
        this.er  = er;
    }

    public void run(String uri) {
        String[] parts = uri.split("/");

        if (parts.length == 1) {
            verifyFileName(parts[0]);
        } else {
            verifyDepth(parts);
            verifyDirectoryNames(parts);
            verifyFileName(parts);
        }
    }

    private void verifyDepth(String[] parts) {
        int dirCount = parts.length - 1;
        if (dirCount > 8) error("The depth of the directory hierarchy must not exceed 8", dirCount + " directories in path");
    }

    private void verifyDirectoryNames(String[] parts) {
        for (int i=1; i<parts.length-1; i++) {
            String s = parts[i];
            if (!goodFileChars(s)) error("Directory name contains invalid characters", s);
            if (s.length() > 8) error("Directory names are limited to 8 characters", s);
        }
    }

    private void verifyFileName(String[] parts) {
        String s = parts[parts.length-1];
        verifyFileName(s);
    }

    private void verifyFileName(String s) {
        String[] parts = s.split("\\.");
        String goodCharError = "Filename must be upper case, digits, or _";

        if (parts.length < 2) {
            // no .
            if (!goodFileChars(parts[0])) error(goodCharError, s);
            if (parts[0].length() > 8) error("Filenames are limited to 8 characters", parts[0]);
            if (parts[0].length() == 0) error("If no characters are specified for the File Name Extension then the File Name shall consist of at least one character.", s);
        }
        else if (parts.length == 2) {
            if (!goodFileChars(parts[0])) error(goodCharError, s);
            if (!goodFileChars(parts[1])) error(goodCharError, s);
            if (parts[0].length() > 8)
                error("Filename is limited to 8 characters", parts[0]);
            if (parts[1].length() > 3) error("Filename extension is limited to 8 characters", parts[1]);
        } else {
            error("File names shall not have more than one dot", s);
        }
    }

    private boolean goodFileChars(String s) {
        return s.matches("[A-Z0-9_]*");
    }

    private void error(String err, String context) {
        er.err(XdsErrorCode.Code.XDSRegistryMetadataError, err, context, MetadataSupport.error_severity, null);
    }
}
