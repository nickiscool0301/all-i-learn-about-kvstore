package time_based_kv_store;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimeBasedKeyValueStoreV4 {
    private final Map<String, TreeMap<Integer, String>> store;
    private final ReadWriteLock lock;

    public TimeBasedKeyValueStoreV4() {
        this.store = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }

    public void set(String key, String value, int timestamp) {
        lock.writeLock().lock();
        try {
            if (!store.containsKey(key)) {
                store.put(key, new TreeMap<>());
            }
            store.get(key).put(timestamp, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String get(String key, int timestamp) {
        lock.readLock().lock();
        try {

            if (!store.containsKey(key)) {
                return "";
            }
            var elementMap = store.get(key);
            Integer floorKey = elementMap.floorKey(timestamp);
            return floorKey == null ? "" : elementMap.get(floorKey);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void delete(String key) {
        lock.writeLock().lock();
        try {
            store.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(String key, int timestamp) {
        lock.writeLock().lock();
        try {
            if (!store.containsKey(key)) {
                return;
            }
            var elementMap = store.get(key);
            elementMap.remove(timestamp);
            if (elementMap.isEmpty()) {
                store.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int size() {
        lock.readLock().lock();
        try {
            return store.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public int versionCount(String key) {
        lock.readLock().lock();
        try {
            if (!store.containsKey(key)) {
                return 0;
            }
            return store.get(key).size();
        } finally {
            lock.readLock().unlock();
        }
    }

    // Time Complexity (same as V2):
    // set: O(log n) - TreeMap insertion
    // get: O(log n) - TreeMap floorKey
    // delete(key): O(1) - HashMap removal
    // delete(key, timestamp): O(log n) - TreeMap removal
}
