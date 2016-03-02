import gov.nist.toolkit.actortransaction.TransactionErrorCodeDbLoader
import gov.nist.toolkit.actortransaction.client.TransactionErrorCodesDb
import spock.lang.Specification
/**
 * Created by bill on 12/16/15.
 */
class TransactionErrorCodeDbLoaderTest extends Specification {

    def 'Test'() {
        when:
        File file = new File(getClass().getResource('ProfileDefinedErrorCodes.txt').file)
        TransactionErrorCodesDb db = TransactionErrorCodeDbLoader.LOAD(file)

        then:
        db.errorCodes.size() == 9
    }
}
