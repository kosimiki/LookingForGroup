package hu.blog.megosztanam.model.shared.summoner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Miklós on 2017. 04. 30..
 */
public class Servers {
    private static final Map<Server, String> serversV2;
    private static final Map<Server, String> serversV3;
    static {
        Map<Server, String> aMap = new HashMap<>();
        aMap.put(Server.EUNE, "eune");
        aMap.put(Server.EUW, "euw");
        serversV2 = Collections.unmodifiableMap(aMap);

        Map<Server, String> aMap2 = new HashMap<>();
        aMap2.put(Server.EUNE, "eun1");
        aMap2.put(Server.EUW,"euw1");
        serversV3 = Collections.unmodifiableMap(aMap2);
    }

    public static String getServerV2(Server server){
        return serversV2.get(server);
    }

    public static String getServerV3(Server server){
        return serversV3.get(server);
    }
}
