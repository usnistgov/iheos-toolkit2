package gov.nist.toolkit.registrymetadata.deletion.support

import gov.nist.toolkit.registrymetadata.deletion.AssnType
import gov.nist.toolkit.registrymetadata.deletion.Registry
import gov.nist.toolkit.registrymetadata.deletion.Uuid
import gov.nist.toolkit.registrymetadata.deletion.objects.Association
import gov.nist.toolkit.registrymetadata.deletion.objects.DocumentEntry
import gov.nist.toolkit.registrymetadata.deletion.objects.Folder
import gov.nist.toolkit.registrymetadata.deletion.objects.RO
import gov.nist.toolkit.registrymetadata.deletion.objects.SubmissionSet

/**
 *
 */
class RegistryMock implements Registry {
    Map<Uuid, RO> ros = [:]

    def load(List<RO> data) {
        data.each { ros[it.id] = it }
    }

    @Override
    boolean exists(Uuid id) {
        return ros.containsKey(id)
    }

    @Override
    boolean isDE(Uuid id) {
        return exists(id) && ros[id] instanceof DocumentEntry
    }

    @Override
    boolean isSS(Uuid id) {
        return exists(id) && ros[id] instanceof SubmissionSet
    }

    @Override
    boolean isFol(Uuid id) {
        return exists(id) && ros[id] instanceof Folder
    }

    @Override
    boolean isASSN(Uuid id) {
        return exists(id) && ros[id] instanceof Association
    }

    @Override
    boolean isHasMember(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.HasMember
    }

    @Override
    boolean isRPLC(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.RPLC
    }

    @Override
    boolean isAPND(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.APND
    }

    @Override
    boolean isXFRM(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.XFRM
    }

    @Override
    boolean isIsSnapshotOf(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.IsSnapshotOf
    }

    @Override
    boolean isSigns(Uuid id) {
        if (!isASSN(id)) return false
        Association a = ros[id] as Association
        return a.type == AssnType.SIGNS
    }

    @Override
    Uuid source(Uuid id) {
        if (!isASSN(id)) return null
        Association a = ros[id] as Association
        return a.source
    }

    @Override
    Uuid target(Uuid id) {
        if (!isASSN(id)) return null
        Association a = ros[id] as Association
        return a.target
    }

    @Override
    AssnType assnType(Uuid id) {
        if (!isASSN(id)) return null
        Association a = ros[id] as Association
        return a.type
    }

    @Override
    boolean onlyAssn(Uuid obj, Uuid id, AssnType type) {
        def assnsOfObj = ros.values().findAll {
            if (it instanceof Association) {
                Association a = it as Association
                a.source == obj || a.target == obj
            } else {
                false
            }
        }
        assnsOfObj.size() == 1 && assnsOfObj[0] == id
    }

    @Override
    List<Uuid> assnLinkedToDE(Uuid de) {
        def objs = ros.values().findAll {
            if (it instanceof Association) {
                Association a = it as Association
                a.source == de || a.target == de
            } else {
                false
            }
        }
        objs.collect { it.id }
    }

    @Override
    List<Uuid> assnLinkedToSS(Uuid ss) {
        def objs = ros.values().findAll {
            if (it instanceof Association) {
                Association a = it as Association
                a.source == ss || a.target == ss
            } else {
                false
            }
        }
        objs.collect { it.id }
    }

    @Override
    List<Uuid> assnLinkedToFol(Uuid fol) {
        def objs = ros.values().findAll {
            if (it instanceof Association) {
                Association a = it as Association
                a.source == fol || a.target == fol
            } else {
                false
            }
        }
        objs.collect { it.id }
    }
}
