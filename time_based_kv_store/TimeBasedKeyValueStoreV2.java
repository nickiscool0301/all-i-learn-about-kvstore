package time_based_kv_store;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

// Timestamp is not strictly increasing
// Use TreeMap to simplify the implementation

public class TimeBasedKeyValueStoreV2 {
    public class Element {
        String value;
        int timestamp;

        Element(String value, int timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }

    Map<String, TreeMap<Integer, String>> store;

    public TimeBasedKeyValueStoreV2() {
        this.store = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        if (!store.containsKey(key)) {
            store.put(key, new TreeMap<>());
        }
        store.get(key).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!store.containsKey(key)) {
            return "";
        }
        var elementMap = store.get(key);
        Integer floorKey = elementMap.floorKey(timestamp);
        return floorKey == null ? "" : elementMap.get(floorKey);
    }

    public void delete(String key) {
        if (!store.containsKey(key)) {
            return;
        }
        store.remove(key);
    }

    public void delete(String key, int timestamp) {
        if (!store.containsKey(key)) {
            return;
        }
        var elementMap = store.get(key);
        if (elementMap.containsKey(timestamp)) {
            elementMap.remove(timestamp);
        }
    }

    public static void main(String[] args) {
        var store = new TimeBasedKeyValueStoreV2();
        store.set("foo", "bar", 5);
        System.out.println(store.get("foo", 7)); // should return "bar"
        store.set("foo", "bar2", 6);
        System.out.println(store.get("foo", 7)); // should return "bar2"
    }

    // Time Complexity:
    // set: O(log n) due to TreeMap insertion
    // get: O(log n) due to TreeMap floorKey operation

}
