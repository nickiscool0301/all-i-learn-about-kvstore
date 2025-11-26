package time_based_kv_store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeBasedKeyValueStoreV1 {
    // Your implementation here
    private class Element {
        String value;
        int timestamp;

        Element(String value, int timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    Map<String, List<Element>> store;

    public TimeBasedKeyValueStoreV1() {
        this.store = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        if (!store.containsKey(key)) {
            store.put(key, new ArrayList<>());
        }
        store.get(key).add(new Element(value, timestamp));
    }

    public String get(String key, int timestamp) {
        if (!store.containsKey(key))
            return "";
        var elementList = store.get(key);
        int left = 0, right = elementList.size() - 1;
        int res = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (elementList.get(mid).timestamp <= timestamp) {
                res = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return res == -1 ? "" : elementList.get(res).value;
    }

    public static void main(String[] agrs) {
        var kv = new TimeBasedKeyValueStoreV1();
        kv.set("foo", "bar", 1);
        System.out.println(kv.get("foo", 1)); // return "bar"
        System.out.println(kv.get("foo", 3)); // return "bar"
        kv.set("foo", "bar2", 4);
        System.out.println(kv.get("foo", 4)); // return "bar2"
        System.out.println(kv.get("foo", 5)); // return "bar2"
    }

    // Time complexity: O(log n) for get, O(1) for set
    // Space complexity: O(m), m is the number of total set operations
    // Assumptions: All timestamps of set are strictly increasing
}
