package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */

public class ActorOptionManager {
    // ActorType => list of options
    // Load configuration from external data source
    private static Map<String, List<ActorAndOption>> actorOptions;

    /*
    private static final Map<String, List<ActorAndOption>> actorOptions;
    static {
        actorOptions = new HashMap<>();
        actorOptions.put("ig", BuildIGTestOrchestrationButton.ACTOR_OPTIONS);
        actorOptions.put("rg", BuildRGTestOrchestrationButton.ACTOR_OPTIONS);
        actorOptions.put("reg", BuildRegTestOrchestrationButton.ACTOR_OPTIONS);
        actorOptions.put("rep", BuildRepTestOrchestrationButton.ACTOR_OPTIONS);
        actorOptions.put("rec", BuildRecTestOrchestrationButton.ACTOR_OPTIONS);
    };
    */

    private ActorOptionManager() {}


    public static ActorAndOption actorDetails(ActorOption actorOption) {
        List<ActorAndOption> aaos = actorOptions.get(actorOption.actorTypeId);
        if (aaos == null) return null;
        for (ActorAndOption aao : aaos) {
            if (aao.getOptionId().equals(actorOption.optionId)) {
                return aao;
            }
        }
        return null;
    }

    public static List<String> optionTitles(String actorId) {
        List<String> names = new ArrayList<>();
        List<ActorAndOption> aaos = actorOptions.get(actorId);
        if (aaos == null) return names;
        for (ActorAndOption aao : aaos) {
            if (aao.getActorId().equals(actorId))
                names.add(aao.getOptionTitle());
        }
        return names;
    }

    public static List<String> optionIds(String actorId) {
        List<String> names = new ArrayList<>();
        List<ActorAndOption> aaos = actorOptions.get(actorId);
        if (aaos == null) return names;
        for (ActorAndOption aao : aaos) {
            if (aao.getActorId().equals(actorId))
                names.add(aao.getOptionId());
        }
        return names;
    }
}
