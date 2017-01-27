package gov.nist.toolkit.actortransaction;

import gov.nist.toolkit.actortransaction.shared.ErrorCode;
import gov.nist.toolkit.actortransaction.shared.Severity;
import gov.nist.toolkit.actortransaction.shared.TransactionErrorCodesDb;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.installation.Installation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.charset.Charset;

/**
 * The table loaded here contains all the errors defined by transactions.
 */
public class TransactionErrorCodeDbLoader {
    static Logger logger = Logger.getLogger(TransactionErrorCodeDbLoader.class);

    static public TransactionErrorCodesDb LOAD() throws Exception {
        File file = new File(Installation.instance().toolkitxFile(), "TransactionDefinedErrorCodes.txt");
        return LOAD(file);
    }

    static public TransactionErrorCodesDb LOAD(File db) throws Exception {
        TransactionErrorCodesDb codes = new TransactionErrorCodesDb();

        CSVParser parser;
        try {
            parser = CSVParser.parse(db, Charset.defaultCharset(), CSVFormat.RFC4180.withHeader());
        } catch (Exception e) {
            logger.error("ProfileErrorCodesDb.LOAD: " + e.getMessage());
            throw e;
        }
        for (CSVRecord record : parser) {
            if (record.size() < 4) throw new Exception(String.format("Error parsing %s, first 4 fields are required", db));
            ErrorCode code = new ErrorCode();
            code.setTransaction(mapType(record.get("Transaction").trim()));
            code.setCode(record.get("Code").trim());
            String severityString = record.get("Severity").trim();
            if (severityString.equals("E")) code.setSeverity(Severity.Error);
            else if (severityString.equals("W")) code.setSeverity(Severity.Warning);
            else throw new Exception("Error loading ProfileDefinedErrorCodes.txt. Unknown severity code " + severityString);
            code.setText(record.get("Text").trim());
            codes.add(code);
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
