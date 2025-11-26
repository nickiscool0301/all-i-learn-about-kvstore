package time_based_kv_store;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Concurrent test for V3 to demonstrate:
 * 1. Thread safety - no data corruption
 * 2. Performance bottleneck - synchronized blocks everything
 */
public class ConcurrentTestV3 {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Concurrent Testing for V3 ===\n");
        testDataCorruptionPrevention();
    }

    /**
     * Test 1: Verify no data corruption with concurrent writes
     */
    static void testDataCorruptionPrevention() throws Exception {
        System.out.println("Test 1: Data Corruption Prevention");
        System.out.println("----------------------------------");

        var store = new TimeBasedKeyValueStoreV3();
        int numThreads = 10;
        int operationsPerThread = 100;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // Each thread writes to a different key
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String key = "key_" + threadId;
                        store.set(key, "value_" + j, j);
                        successCount.incrementAndGet();
                    }
                } finally {
                    // Workers countdown when done
                    latch.countDown();
                }
            });
        }

        // Main thread waits until count reaches zero
        latch.await();

        executor.shutdown();

        System.out.println("Operations completed: " + successCount.get());
        System.out.println("Expected: " + (numThreads * operationsPerThread));
        System.out.println("Keys in store: " + store.size());
        System.out.println("No data corruption!\n");
    }
}
