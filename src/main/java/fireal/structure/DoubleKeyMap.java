package fireal.structure;

import fireal.util.MapUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DoubleKeyMap<T, R, U> {

    protected Map<T, U> firstKeyMap = new HashMap<>();
    protected Map<R, U> secondKeyMap = new HashMap<>();

    public boolean putInSafe(T firstKey, R secondKey, U instance, boolean canOverride) {
        if (firstKey == null && secondKey == null) throw new IllegalArgumentException("Arg Can't be all null");
        boolean firstSuccess = true;
        boolean secondSuccess = false;

        if (firstKey != null) {
            firstSuccess = MapUtil.putInSafe(firstKeyMap, firstKey, instance, canOverride);
        }

        if (secondKey != null) {
            secondSuccess = MapUtil.putInSafe(secondKeyMap, secondKey, instance, canOverride);
        }

        return firstSuccess && secondSuccess;
    }

    public boolean putInSafe(T firstKey, R secondKey, U instance) {
       return putInSafe(firstKey, secondKey, instance, true);
    }

    public U getWithFirstKey(T firstKey) {
        return firstKeyMap.get(firstKey);
    }

    public U getWithSecondKey(R secondKey) {
        return secondKeyMap.get(secondKey);
    }

    public U get(T firstKey, R secondKey, boolean queryFirstKeyAhead) {
        if (queryFirstKeyAhead) {
            return firstKeyMap.getOrDefault(firstKey, secondKeyMap.get(secondKey));
        } else {
            return secondKeyMap.getOrDefault(secondKey, firstKeyMap.get(firstKey));
        }
    }

    public U get(T firstKey, R secondKey) {
        return get(firstKey, secondKey, true);
    }
    
    public boolean containsKeyInFirstMap(T key) {
        return firstKeyMap.containsKey(key);
    }

    public boolean containKeyInSecondMap(R key) {
        return secondKeyMap.containsKey(key);
    }

    public boolean containValue(U value) {
        return firstKeyMap.containsValue(value) || secondKeyMap.containsValue(value);
    }

    public Collection<U> values() {
        var set = new HashSet<U>();
        set.addAll(firstKeyMap.values());
        set.addAll(secondKeyMap.values());
        return set;
    }

    public void remove(T firstKey, R secondKey) {
        firstKeyMap.remove(firstKey);
        secondKeyMap.remove(secondKey);
    }

    public void clear() {
        firstKeyMap.clear();
        secondKeyMap.clear();
    }
}
