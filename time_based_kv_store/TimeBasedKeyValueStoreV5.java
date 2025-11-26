package time_based_kv_store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TimeBasedKeyValueStoreV5 {

    // ConcurrentHashMap: like HashMap but thread-safe without locking everything
    // ConcurrentSkipListMap: like TreeMap but designed for concurrent access
    private final ConcurrentHashMap<String, ConcurrentSkipListMap<Integer, String>> store;

    public TimeBasedKeyValueStoreV5() {
        this.store = new ConcurrentHashMap<>();
    }

    /**
     * Set operation - no explicit locking needed!
     *
     * Here's what happens under the hood:
     * 1. computeIfAbsent checks if key exists
     * 2. If not, it creates a new ConcurrentSkipListMap ATOMICALLY
     * 3. Then we put the timestamp->value into that map
     *
     * computeIfAbsent uses CAS (Compare-And-Swap)
     * - Thread 1: "Is key absent? Yes. Let me add it."
     * - Thread 2: "Is key absent? Oh wait, Thread 1 just added it. Use that one."
     * - No lock needed! It's atomic at the CPU level.
     */
    public void set(String key, String value, int timestamp) {
        store.computeIfAbsent(key, k -> new ConcurrentSkipListMap<>())
                .put(timestamp, value);

        // Compare to V4 where we had to:
        // 1. Acquire write lock
        // 2. Check if key exists
        // 3. Create TreeMap if needed
        // 4. Put value
        // 5. Release lock
    }

    public String get(String key, int timestamp) {
        var timestampMap = store.get(key);
        if (timestampMap == null) {
            return "";
        }
        Integer floorKey = timestampMap.floorKey(timestamp);
        return floorKey == null ? "" : timestampMap.get(floorKey);
    }

    public void delete(String key) {
        store.remove(key);
    }

    public void delete(String key, int timestamp) {
        var timestampMap = store.get(key);
        if (timestampMap == null) {
            return;
        }
        timestampMap.remove(timestamp);
        if (timestampMap.isEmpty()) {
            store.remove(key);
        }
    }

    public int size() {
        return store.size();
    }

    public int versionCount(String key) {
        var timestampMap = store.get(key);
        return timestampMap == null ? 0 : timestampMap.size();
    }
}
