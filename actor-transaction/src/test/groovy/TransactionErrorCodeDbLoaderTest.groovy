import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader
import gov.nist.toolkit.actortransaction.client.TransactionErrorCodesDb
import spock.lang.Specification

import java.nio.file.Paths

/**
 * Created by bill on 12/16/15.
 */
class TransactionErrorCodeDbLoaderTest extends Specification {

    def 'Test'() {
        when:
        File file = Paths.get(getClass().getResource('/').toURI()).resolve('ProfileDefinedErrorCodes.txt').toFile()
//        File file = new File(getClass().getResource('/ProfileDefinedErrorCodes.txt').file)
        System.out.print("**** " + file.toString())
        TransactionErrorCodesDb db = TransactionErrorCodeDbLoader.LOAD(file)

        then:
        db.errorCodes.size() == 9
    }
}
