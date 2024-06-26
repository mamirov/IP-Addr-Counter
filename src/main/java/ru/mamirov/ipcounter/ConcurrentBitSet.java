package ru.mamirov.ipcounter;

import java.util.concurrent.atomic.AtomicLongArray;

/**
 * Concurrent bit set based on AtomicLongArray
 * Additionally add +2 to capacity to solve -2^31 number
 */
public class ConcurrentBitSet {
    private final AtomicLongArray bits;

    public ConcurrentBitSet(int size) {
        var arr = new long[wordIndex(size - 1) + 2];
        bits = new AtomicLongArray(arr);
    }

    private static int wordIndex(int bitIndex) {
        return bitIndex >> 6;
    }

    public void set(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);
        if (wordIndex < 0) {
            wordIndex = Math.abs(wordIndex);
        }
        long bitMask = 1L << bitIndex;
        while (true) {
            long current = bits.get(wordIndex);
            long updated = current | bitMask;
            if (bits.compareAndSet(wordIndex, current, updated)) {
                break;
            }
        }
    }

    public boolean get(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);
        //handle -2^31
        if (wordIndex < 0) {
            wordIndex = Math.abs(wordIndex);
        }
        long bitMask = 1L << bitIndex;
        return (bits.get(wordIndex) & bitMask) != 0;
    }

    public long valuesSize() {
        long count = 0;
        for (int i = 0; i < bits.length(); i++) {
            count += Long.bitCount(bits.get(i));
        }
        return count;
    }
}
