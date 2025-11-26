package time_based_kv_store;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

/**
 * Version 3: Thread-Safe using synchronized keyword
 *
 * - synchronized methods acquire the object's intrinsic lock
 * - Only ONE thread can execute ANY synchronized method at a time
 * - Even multiple readers will block each other (inefficient!)
 * - This is called "coarse-grained locking"
 *
 * Trade-offs:
 * + Simple to implement and reason about
 * - Readers block readers (unnecessary)
 * - Single lock = single bottleneck
 */
public class TimeBasedKeyValueStoreV3 {
    private final Map<String, TreeMap<Integer, String>> store;

    public TimeBasedKeyValueStoreV3() {
        this.store = new HashMap<>();
    }

    public synchronized void set(String key, String value, int timestamp) {
        if (!store.containsKey(key)) {
            // without synchronized, thread 2 can override thread 1
            store.put(key, new TreeMap<>());
        }
        store.get(key).put(timestamp, value);
    }

    public synchronized String get(String key, int timestamp) {
        if (!store.containsKey(key)) {
            return "";
        }
        var elementMap = store.get(key);
        Integer floorKey = elementMap.floorKey(timestamp);
        return floorKey == null ? "" : elementMap.get(floorKey);
    }

    public synchronized void delete(String key) {
        store.remove(key);
    }

    public synchronized void delete(String key, int timestamp) {
        if (!store.containsKey(key)) {
            return;
        }
        var elementMap = store.get(key);
        elementMap.remove(timestamp);
        if (elementMap.isEmpty()) {
            store.remove(key);
        }
    }

    public synchronized int size() {
        return store.size();
    }

    public synchronized int versionCount(String key) {
        if (!store.containsKey(key)) {
            return 0;
        }
        return store.get(key).size();
    }

    // Time Complexity (same as V2):
    // set: O(log n) - TreeMap insertion
    // get: O(log n) - TreeMap floorKey
    // delete(key): O(1) - HashMap removal
    // delete(key, timestamp): O(log n) - TreeMap removal
}
