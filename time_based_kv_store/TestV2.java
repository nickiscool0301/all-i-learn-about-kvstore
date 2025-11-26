package time_based_kv_store;

public class TestV2 {
    public static void main(String[] args) {
        var store = new TimeBasedKeyValueStoreV2();

        System.out.println("=== Test 1: Basic operations ===");
        store.set("foo", "bar1", 1);
        store.set("foo", "bar2", 5);
        store.set("foo", "bar3", 10);
        System.out.println("get(foo, 1): " + store.get("foo", 1));   // bar1
        System.out.println("get(foo, 5): " + store.get("foo", 5));   // bar2
        System.out.println("get(foo, 7): " + store.get("foo", 7));   // bar2
        System.out.println("get(foo, 10): " + store.get("foo", 10)); // bar3

        System.out.println("\n=== Test 2: Delete specific timestamp ===");
        store.delete("foo", 5);
        System.out.println("After delete(foo, 5):");
        System.out.println("get(foo, 7): " + store.get("foo", 7));   // should be bar1 now
        System.out.println("get(foo, 10): " + store.get("foo", 10)); // still bar3

        System.out.println("\n=== Test 3: Delete entire key ===");
        store.set("bar", "value1", 1);
        store.set("bar", "value2", 5);
        store.delete("bar");
        System.out.println("get(bar, 5): " + store.get("bar", 5));   // should be ""

        System.out.println("\n=== Test 4: Unordered timestamps ===");
        store.set("baz", "v1", 10);
        store.set("baz", "v2", 5);  // older timestamp inserted later
        store.set("baz", "v3", 15);
        System.out.println("get(baz, 7): " + store.get("baz", 7));   // should be v2
        System.out.println("get(baz, 12): " + store.get("baz", 12)); // should be v1

        System.out.println("\n=== Test 5: Edge case - timestamp before all entries ===");
        try {
            System.out.println("get(baz, 1): " + store.get("baz", 1)); // NPE possible!
        } catch (NullPointerException e) {
            System.out.println("NPE caught! floorKey returns null when timestamp < all keys");
        }

        System.out.println("\n=== All tests completed ===");
    }
}
