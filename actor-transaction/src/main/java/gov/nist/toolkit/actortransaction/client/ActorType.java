package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.*;

// This file must be kept up to date with SimulatorActorTypes.java

/**
 * Actor types defined by test engine.  A subset of these are available as simulators.
 */
public enum ActorType implements IsSerializable, Serializable {
    XDR_DOC_SRC(
            "XDR Document Source",
			Arrays.asList("XDR_Source"),
            "xdrsrc",
            "gov.nist.toolkit.simulators.sim.src.XdrDocSrcActorSimulator",
			Arrays.asList(TransactionType.XDR_PROVIDE_AND_REGISTER),
            false,
            null
	),
    REGISTRY(
            "Document Registry",
            Arrays.asList("DOC_REGISTRY", "Initialize_for_Stored_Query"),
            "reg",
            "gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator",
            Arrays.asList(TransactionType.REGISTER, TransactionType.REGISTER_ODDE, TransactionType.STORED_QUERY, TransactionType.UPDATE, TransactionType.MPQ),
            true,
            null
    ),
	// Update option on Document Registry
	// this should be removed once implications are re-discovered
//		UPDATE (
//				"Update Option",
//				new ArrayList<String>(),
//				"update",
//				new ArrayList<TransactionType>(),
//				false,
//				null
//				),
    REPOSITORY(
            "Document Repository",
			Arrays.asList("DOC_REPOSITORY"),
            "rep",
            "gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator",
			Arrays.asList(TransactionType.PROVIDE_AND_REGISTER, TransactionType.RETRIEVE),
			true,
            "repository"
	),
    ONDEMAND_DOCUMENT_SOURCE(
            "On-Demand Document Source",
			Arrays.asList("ODDS", "ON_DEMAND_DOC_SOURCE"),
            "odds",
            "gov.nist.toolkit.simulators.sim.od.rep.RepositoryActorSimulator",
			Arrays.asList(TransactionType.ODDS_RETRIEVE),
			true,
            "odds"
	),
    ISR(
            "Integrated Source/Repository",
			Arrays.asList("EMBED_REPOS"),
            "isr",
            null,
			Arrays.asList(TransactionType.ISR_RETRIEVE),
			true,
            "isr"
	),
    REPOSITORY_REGISTRY(
            "Document Repository/Registry",
			new ArrayList<String>(),
            "rr",
            "gov.nist.toolkit.simulators.sim.RegRepActorSimulator",
			Arrays.asList(TransactionType.REGISTER, TransactionType.STORED_QUERY, TransactionType.UPDATE, TransactionType.MPQ, TransactionType.PROVIDE_AND_REGISTER, TransactionType.RETRIEVE),
            false,
            null
	),
    DOCUMENT_RECIPIENT(
            "Document Recipient",
			Arrays.asList("DOC_RECIPIENT"),
            "rec",
            "gov.nist.toolkit.simulators.sim.RegRepActorSimulator",
			Arrays.asList(TransactionType.XDR_PROVIDE_AND_REGISTER),
            true,
            null
	),
    RESPONDING_GATEWAY(
            "Responding Gateway",
			Arrays.asList("RESP_GATEWAY"),
            "rg",
            "gov.nist.toolkit.simulators.sim.rg.RGActorSimulator",
			Arrays.asList(TransactionType.XC_QUERY, TransactionType.XC_RETRIEVE, TransactionType.XC_PATIENT_DISCOVERY),
            true,
            null
	),
    INITIATING_GATEWAY(
            "Initiating Gateway",
			Arrays.asList("INIT_GATEWAY"),
            "ig",
            "gov.nist.toolkit.simulators.sim.ig.IgActorSimulator",
			Arrays.asList(TransactionType.IG_QUERY, TransactionType.IG_RETRIEVE),
            true,
            null
	)
    ;

	private static final long serialVersionUID = 1L;
    String name;
	List<String> altNames;
    String shortName;
    List<TransactionType> transactionTypes; // TransactionTypes this actor can receive
    boolean showInConfig;
    String actorsFileLabel;
    String simulatorClassName;

	ActorType() {
	} // for GWT

    ActorType(String name, List<String> altNames, String shortName, String simulatorClassName, List<TransactionType> tt, boolean showInConfig, String actorsFileLabel) {
        this.name = name;
        this.altNames = altNames;
        this.shortName = shortName;
        this.simulatorClassName = simulatorClassName;
        this.transactionTypes = tt;   // This actor receives
        this.showInConfig = showInConfig;
        this.actorsFileLabel = actorsFileLabel;
    }

	public boolean showInConfig() {
        return showInConfig;
    }

	public boolean isRepositoryActor() {
        return this.equals(REPOSITORY);
	}

    public boolean isRegistryActor() {
        return this.equals(REGISTRY);
    }

    public boolean isRGActor() {
        return this.equals(RESPONDING_GATEWAY);
    }

	public boolean isIGActor() {
        return this.equals(INITIATING_GATEWAY);
    }

	public boolean isGW() {
        return isRGActor() || isIGActor();
    }

    public String getSimulatorClassName() { return simulatorClassName; }

	public String getActorsFileLabel() {
        return actorsFileLabel;
    }

    static public List<String> getActorNames() {
        List<String> names = new ArrayList<String>();

		for (ActorType a : values())
            names.add(a.name);

		return names;
    }

	/**
	 * Within toolkit, each TransactionType maps to a unique ActorType
	 * (as receiver of the transaction). To make this work, transaction
	 * names are customized to make this mapping unique.  This goes
	 * beyond the definition in the TF.
	 *
	 * @param tt
	 * @return
	 */
    static public ActorType getActorType(TransactionType tt) {
        if (tt == null)
            return null;
        for (ActorType at : values()) {
            if (at.hasTransaction(tt))
                return at;
        }
        return null;
    }

    static public Set<ActorType> getActorTypes(TransactionType tt) {
        Set<ActorType> types = new HashSet<>();
        if (tt == null)
            return types;
        for (ActorType at : values()) {
            if (at.hasTransaction(tt))
                types.add(at);
        }
        return types;
    }

	static public ActorType findActor(String name) {
        if (name == null)
            return null;

		for (ActorType actor : values()) {
            if (actor.name.equals(name)) return actor;
            if (actor.shortName.equals(name)) return actor;
            if (actor.altNames.contains(name)) return actor;
        }
        return null;
    }


	public String toString() {
        StringBuffer buf = new StringBuffer();

		buf.append(name).append(" [");
        for (TransactionType tt : transactionTypes)
            buf.append(tt).append(",");
        buf.append("]");

		return buf.toString();
    }

	public String getName() {
        return name;
    }

	public String getShortName() {
        return shortName;
    }

	public List<TransactionType> getTransactions() {
        return transactionTypes;
    }

	public boolean hasTransaction(TransactionType transType) {
        for (TransactionType transType2 : transactionTypes) {
            if (transType2.equals(transType))
                return true;
        }
        return false;
    }


	public boolean equals(ActorType at) {
        try {
            return name.equals(at.name);
        } catch (Exception e) {
		}
        return false;
    }
}
