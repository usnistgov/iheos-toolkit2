package gov.nist.toolkit.simulators.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

class ActorTransactionFactory implements Serializable,IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	static public enum TransactionType { PNR, XCQR, REGISTER, SQ, RETRIEVE, MU, XCQ, XCR, IGQ, IGR, UNKNOWN };


	/********************************************************
	 * 
	 * Translation of transaction types from string  <==> enum
	 * 
	 * 
	 ********************************************************/
	static public TransactionType getTransaction(String name) {
		if (name == null)
			return TransactionType.UNKNOWN;
		if (name.equals("prb"))
			return TransactionType.PNR;
		if (name.equals("ret"))
			return TransactionType.RETRIEVE;
		if (name.equals("rb"))
			return TransactionType.REGISTER;
		if (name.equals("update"))
			return TransactionType.MU;
		if (name.equals("sq"))
			return TransactionType.SQ;
		if (name.equals("xcq"))
			return TransactionType.XCQ;
		if (name.equals("xcr"))
			return TransactionType.XCR;
		if (name.equals("igq"))
			return TransactionType.IGQ;
		if (name.equals("igr"))
			return TransactionType.IGR;
		if (name.equals("xcqr"))
			return TransactionType.XCQR;
		return TransactionType.UNKNOWN;
	}

	// these are the display names used in the Simulator Message View
	// this list must be kept in sync with the above getTransaction(String) method
	static public String getTransactionName(TransactionType type) {
		switch (type) {
		case SQ:
			return "sq";
		case MU:
			return "update";
		case RETRIEVE:
			return "ret";
		case REGISTER:
			return "rb";
		case PNR:
			return "prb";
		case XCQR:
			return "xcqr";
		case XCQ:
			return "xcq";
		case XCR:
			return "xcr";
		case IGQ:
			return "igq";
		case IGR:
			return "igr";
		default:
			return "unknown";
		}
	}
	
	// get transaction name used in actors.xml file (site definition)
	static public String getSiteTransactionName(TransactionType transaction) {
		switch (transaction) {
		case PNR:
			return "pr.b";
		case XCR:
			return "xcr";
		case XCQ:
			return "xcq";
		case IGQ:
			return "igq";
		case IGR:
			return "igr";
		case REGISTER:
			return "r.b";
		case RETRIEVE:
			return "ret";
		case SQ:
			return "sq.b";
		case MU:
			return "update";
		default:
			return	null;
		}
	}
	
	static public List<String> asStrings(List<TransactionType> transTypes) {
		List<String> strs = new ArrayList<String>();

		for (TransactionType tt : transTypes) {
			strs.add(getTransactionName(tt));
		}

		return strs;
	}


	/********************************************************
	 * 
	 * Actor stuff
	 * 
	 * 
	 ********************************************************/

	static public enum ActorType { 
//		EPSOS, 
		REGISTRY, 
		IG,
		RG,
		REPOSITORY,
//		REGREP, 
//		REGREP_UPDATE, 
		UNKNOWN 
		};
		

	static public String getActorName(ActorType type) {
		return type.toString().toLowerCase();
	}

	static public ActorType getActorType(String actorName) {
		if (actorName == null)
			return ActorType.UNKNOWN;
//		if (actorName.equals("epsos"))
//			return ActorType.EPSOS;
		if (actorName.equals("registry"))
			return ActorType.REGISTRY;
		if (actorName.equals("repository"))
			return ActorType.REPOSITORY;
		if (actorName.equals("IG"))
			return ActorType.IG;
		if (actorName.equals("RG"))
			return ActorType.RG;
//		if (actorName.equals("regrep"))
//			return ActorType.REGREP;
//		if (actorName.equals("regrep_update"))
//			return ActorType.REGREP_UPDATE;
		return ActorType.UNKNOWN;
	}


	static public List<TransactionType> getIncomingTransactions(ActorType actor) {
		List<TransactionType> trans = new ArrayList<TransactionType>();

		switch (actor) {
//		case EPSOS:
//			trans.add(TransactionType.PNR);
//			trans.add(TransactionType.XCQR);
//			break;
		case REGISTRY:
			trans.add(TransactionType.REGISTER);
			trans.add(TransactionType.SQ);
			trans.add(TransactionType.MU);
			break;
//		case REGREP:
//			trans.add(TransactionType.PNR);
//			trans.add(TransactionType.SQ);
//			trans.add(TransactionType.RETRIEVE);
//			break;
//		case REGREP_UPDATE:
//			trans.add(TransactionType.PNR);
//			trans.add(TransactionType.SQ);
//			trans.add(TransactionType.RETRIEVE);
//			trans.add(TransactionType.MU);
		default:
		}

		return trans;
	}


}
