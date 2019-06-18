package com.prashanthi.lru;

import java.util.*;

public class LruHash {

private int capacity;
private HashMap<Integer, DoubleLLImpl<CacheEntry>.Node> map;
private DoubleLLImpl<CacheEntry> list;

public LruHash(int capacity) {
    this.capacity = capacity;
    this.map = new HashMap<Integer, DoubleLLImpl<CacheEntry>.Node>(capacity*2);
    this.list = new DoubleLLImpl<CacheEntry>();
}

private int getCapacity() {
    return capacity;
}

public int[] put(int key, int value) {
    if (map.containsKey(key)) {
        System.out.println("Key :" + key + "already present... update?");
        //key already resent, Update
        DoubleLLImpl<CacheEntry>.Node node = map.get(key);
        System.out.println(node.data.key + " " + node.data.value);

        node = list.delete(node);
        map.remove(node.data.key);

        // update the value and add it back to the Q
        node.data.value = value;
        node = list.insertAtEnd(node.data);
        map.put(key, node);
        return null;
    } else {
        //Check capacity to check if we can insert or need to evict to insert
        if (list.getSize() >= getCapacity()) {
            int[] evictedEntry = new int[2];
            // max capacity reached
            System.out.println("Max capacity reached");
            DoubleLLImpl<CacheEntry>.Node node = list.getHead();
            list.delete(node);
            if (node != null) {
                System.out.println("evicted entry : " + node.data.key + " " + node.data.value);
                evictedEntry[0] = node.data.key;
                evictedEntry[1] = node.data.value;
            } else {
                System.out.println("Is null");
            }
            map.remove(node.data.key);

            CacheEntry cEntry = new CacheEntry(key, value);
            System.out.println("inserting entry after eviction: " + cEntry.key + " " + cEntry.value);
            node = list.insertAtEnd(cEntry);
            map.put(key, node);
            return evictedEntry;
        } else {
            System.out.println("Normal insert for " + key + " " + value);
            CacheEntry cEntry = new CacheEntry(key, value);
            DoubleLLImpl<CacheEntry>.Node node = list.insertAtEnd(cEntry);

            map.put(key, node);
            return null;
        }
    }
}

public int[] get(int key) {
    int[] arr = new int[2];
    if (!map.containsKey(key)) {
        System.out.println("Key : " + key + " not present");
        return null;
    } else {
        DoubleLLImpl<CacheEntry>.Node node = map.get(key);
        System.out.println("insert/get: " + node.data.key + " " + node.data.value);
        arr[0] = node.data.key;
        arr[1] = node.data.value;

        //remove and add to end of Queue
        CacheEntry cEntry = new CacheEntry(node.data.key, node.data.value);
        list.delete(node);
        map.remove(node.data.key);
        node = list.insertAtEnd(cEntry);

        map.put(key, node);
    }
    return arr;
}

public static void main(String[] args) {

    int capacity = 2;

    LruHash lruClass = new LruHash(capacity);

    lruClass.put(1, 400);
    lruClass.put(2, 800);

    System.out.println("1 and 2 in Q");

    lruClass.get(1);

    lruClass.put(3, 1200);
    System.out.println("insert 3 after evicting 2 from Q");

    lruClass.get(2);
    System.out.println("2 not found");

    lruClass.put(4, 1600);
    System.out.println("3 followed by 4 in Q");

    lruClass.get(1);

    lruClass.get(3);
    lruClass.get(4);
    System.out.println("get 3 and get 4 from Q");

    lruClass.put(3, 1800);
    lruClass.get(3);
    System.out.println("update 3, so 4 followed by 3 in  Q");

    lruClass.put(5, 1600);
    System.out.println("evict 4 and insert 5 in Q, 3 followed by 5 present");

    lruClass.put(6, 600);
    System.out.println("evict 3 from Q");

    lruClass.put(7, 700);
    System.out.println(" evict 5 from Q");
}

}
