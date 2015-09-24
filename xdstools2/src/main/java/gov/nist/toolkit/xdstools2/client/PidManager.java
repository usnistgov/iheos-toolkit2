package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.actorfactory.client.Pid;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a singleton owned by Xdstools2
 */
public class PidManager {
    Set<Pid> favoritePids = new HashSet<>();  // the database of values

}
