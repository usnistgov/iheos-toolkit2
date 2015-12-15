package gov.nist.toolkit.actortransaction;

import gov.nist.toolkit.actortransaction.client.ErrorCode;
import gov.nist.toolkit.actortransaction.client.ProfileErrorCodesDb;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.nio.charset.Charset;

/**
 * The table loaded here contains all the errors defined by profiles.
 */
public class ProfileErrorCodeDbLoader {

    static public ProfileErrorCodesDb LOAD() throws Exception {
        File file = new File(Installation.installation().toolkitxFile(), "ProfileDefinedErrorCodes.txt");
        ProfileErrorCodesDb codes = new ProfileErrorCodesDb();

        CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(),CSVFormat.RFC4180);
        for (CSVRecord record : parser) {
            if (record.size() < 4) throw new Exception(String.format("Error parsing %s, first 4 fields are required", file));
            ErrorCode code = new ErrorCode();
            code.setTransaction(mapType(record.get("Transaction")));
            code.setCode(record.get("Code"));
            String severityString = record.get("Severity");
            if (severityString.equals("E")) code.setSeverity(Severity.Error);
            else if (severityString.equals("W")) code.setSeverity(Severity.Warning);
            else throw new Exception("Error loading ProfileDefinedErrorCodes.txt. Unknown severity code " + severityString);
            code.setText(record.get("Text"));
        }

        return codes;
    }

    static TransactionType mapType(String t) throws Exception {
        if ("P".equals(t)) return TransactionType.PROVIDE_AND_REGISTER;
        if ("R".equals(t)) return TransactionType.REGISTER;
        if ("SQ".equals(t)) return TransactionType.STORED_QUERY;
        if ("RS".equals(t)) return TransactionType.RETRIEVE;
        if ("XGQ".equals(t)) return TransactionType.XC_QUERY;
        if ("XGR".equals(t)) return TransactionType.XC_RETRIEVE;
        throw new Exception("Error loading ProfileDefinedErrorCodes.txt. Unknown transaction code " + t);
    }


}
