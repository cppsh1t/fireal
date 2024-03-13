package fireal.util;

import java.util.Map;

public class MapUtil {

    public static <T, R> boolean putInSafe(Map<T, R> map, T key, R instance, boolean canOverride) {
        if (canOverride) {
            map.put(key, instance);
            //put success
            return true;
        } else {
            //don't know success or failure
            //but if success, return null
            return map.putIfAbsent(key, instance) == null;
        }
    }


}
