package gov.nist.toolkit.fhir.simulators.sim.reg
import gov.nist.toolkit.errorrecording.TextErrorRecorder
import gov.nist.toolkit.registrymetadata.Metadata
import gov.nist.toolkit.fhir.simulators.sim.reg.sq.GetAllSim
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType
import gov.nist.toolkit.valregmsg.registry.storedquery.support.SqParams
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport
import gov.nist.toolkit.xdsexception.client.MetadataValidationException
import spock.lang.Specification
/**
 * Created by bill on 8/24/15.
 */
class GetAllTest extends Specification {
    File resourcesDir
    RegIndex regIndex
    TextErrorRecorder er
    SqParams parms

    def setup() {
        resourcesDir = new File(getClass().getResource('/root.txt').file).parentFile
        er = new TextErrorRecorder()
        parms = new SqParams()
    }

    def 'Test DE submission'() {
        when:
        regIndex = RegIndexFactory.buildRegIndex(new File(resourcesDir, 'registry-data/submission1/Registry'))
        println 'RegIndex:'
        println regIndex.mc.statsToString()

        parms.addStringParm('$patientId', '123^^^&1.2.343&ISO')
        parms.addListParm('$XDSDocumentEntryStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSSubmissionSetStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSFolderStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])

        StoredQuerySupport sqs = new StoredQuerySupport(parms, QueryReturnType.LEAFCLASS, er, null, false)
        GetAllSim sim = new GetAllSim(sqs)
        sim.setRegIndex(regIndex)
        Metadata m
        try {
            m = sim.runSpecific()
        } catch (MetadataValidationException mve) {
            er.showErrorInfo()
        }

        then:
        er.getNbErrors() == 0
        m.getExtrinsicObjects().size() == 1
        m.getSubmissionSets().size() == 1
        m.getAssociations().size() == 1
        m.getFolders().size() == 0
    }

    def 'Test deprecated DE submission'() {
        when:
        regIndex = RegIndexFactory.buildRegIndex(new File(resourcesDir, 'registry-data/submission2/Registry'))
        println 'RegIndex:'
        println regIndex.mc.statsToString()


        parms.addStringParm('$patientId', '123^^^&1.2.343&ISO')
        parms.addListParm('$XDSDocumentEntryStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated'])
        parms.addListParm('$XDSSubmissionSetStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSFolderStatus',['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])

        StoredQuerySupport sqs = new StoredQuerySupport(parms, QueryReturnType.LEAFCLASS, er, null, false)
        GetAllSim sim = new GetAllSim(sqs)
        sim.setRegIndex(regIndex)
        Metadata m
        try {
            m = sim.runSpecific()
        } catch (MetadataValidationException mve) {
            er.showErrorInfo()
        }

        then:
        er.getNbErrors() == 0
        m.getExtrinsicObjects().size() == 1
        m.getSubmissionSets().size() == 1
        m.getAssociations().size() == 1
        m.getFolders().size() == 0
    }

    def 'Test any status DE submission'() {
        when:
        regIndex = RegIndexFactory.buildRegIndex(new File(resourcesDir, 'registry-data/submission2/Registry'))
        println 'RegIndex:'
        println regIndex.mc.statsToString()


        parms.addStringParm('$patientId', '123^^^&1.2.343&ISO')
        parms.addListParm('$XDSDocumentEntryStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated'])
        parms.addListParm('$XDSSubmissionSetStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSFolderStatus',['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])

        StoredQuerySupport sqs = new StoredQuerySupport(parms, QueryReturnType.LEAFCLASS, er, null, false)
        GetAllSim sim = new GetAllSim(sqs)
        sim.setRegIndex(regIndex)
        Metadata m
        try {
            m = sim.runSpecific()
        } catch (MetadataValidationException mve) {
            er.showErrorInfo()
        }

        then:
        er.getNbErrors() == 0
        m.getExtrinsicObjects().size() == 1
        m.getSubmissionSets().size() == 1
        m.getAssociations().size() == 1
        m.getFolders().size() == 0
    }

    def 'Test 2 submissions'() {
        when:
        regIndex = RegIndexFactory.buildRegIndex(
                [
                new File(resourcesDir, 'registry-data/submission1/Registry'),
                new File(resourcesDir, 'registry-data/submission2/Registry')
                        ]
        )
        println 'RegIndex:'
        println regIndex.mc.statsToString()


        parms.addStringParm('$patientId', '123^^^&1.2.343&ISO')
        parms.addListParm('$XDSDocumentEntryStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated'])
        parms.addListParm('$XDSSubmissionSetStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSFolderStatus',['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])

        StoredQuerySupport sqs = new StoredQuerySupport(parms, QueryReturnType.LEAFCLASS, er, null, false)
        GetAllSim sim = new GetAllSim(sqs)
        sim.setRegIndex(regIndex)
        Metadata m
        try {
            m = sim.runSpecific()
        } catch (MetadataValidationException mve) {
            er.showErrorInfo()
        }

        then:
        er.getNbErrors() == 0
        m.getExtrinsicObjects().size() == 2
        m.getSubmissionSets().size() == 2
        m.getAssociations().size() == 2
        m.getFolders().size() == 0
    }

    def 'Test 3 submissions - 1 pid does not match'() {
        when:
        regIndex = RegIndexFactory.buildRegIndex(
                [
                        new File(resourcesDir, 'registry-data/submission1/Registry'),
                        new File(resourcesDir, 'registry-data/submission2/Registry'),
                        new File(resourcesDir, 'registry-data/submission3/Registry')
                ]
        )
        println 'RegIndex:'
        println regIndex.mc.statsToString()


        parms.addStringParm('$patientId', '123^^^&1.2.343&ISO')
        parms.addListParm('$XDSDocumentEntryStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated'])
        parms.addListParm('$XDSSubmissionSetStatus', ['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])
        parms.addListParm('$XDSFolderStatus',['urn:oasis:names:tc:ebxml-regrep:StatusType:Approved'])

        StoredQuerySupport sqs = new StoredQuerySupport(parms, QueryReturnType.LEAFCLASS, er, null, false)
        GetAllSim sim = new GetAllSim(sqs)
        sim.setRegIndex(regIndex)
        Metadata m
        try {
            m = sim.runSpecific()
        } catch (MetadataValidationException mve) {
            er.showErrorInfo()
        }

        then:
        er.getNbErrors() == 0
        m.getExtrinsicObjects().size() == 2
        m.getSubmissionSets().size() == 2
        m.getAssociations().size() == 2
        m.getFolders().size() == 0
    }
}
