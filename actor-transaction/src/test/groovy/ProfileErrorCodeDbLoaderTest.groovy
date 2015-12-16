import gov.nist.toolkit.actortransaction.ProfileErrorCodeDbLoader
import gov.nist.toolkit.actortransaction.client.ProfileErrorCodesDb
import spock.lang.Specification
/**
 * Created by bill on 12/16/15.
 */
class ProfileErrorCodeDbLoaderTest extends Specification {

    def 'Test'() {
        when:
        File file = new File(getClass().getResource('ProfileDefinedErrorCodes.txt').file)
        ProfileErrorCodesDb db = ProfileErrorCodeDbLoader.LOAD(file)

        then:
        db.errorCodes.size() == 9
    }
}
