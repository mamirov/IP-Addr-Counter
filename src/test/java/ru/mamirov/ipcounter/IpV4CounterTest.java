package ru.mamirov.ipcounter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IpV4CounterTest {

    @Test
    void testUniqueNumbers() {
        IpV4Counter counter = new IpV4Counter();
        int[] numbers = new int[]{1, 2, 3, 4, 1, 2, 3, 4};

        for (int number : numbers) {
            counter.countUniqueIp(number);
        }

        assertEquals(4, counter.count());
    }

    @Test
    void testPositiveLargeNumbers() {
        IpV4Counter counter = new IpV4Counter();

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            counter.countUniqueIp(i);
        }

        assertEquals(Integer.MAX_VALUE, counter.count());
    }

    @Test
    void testNegativeLargeNumbers() {
        IpV4Counter counter = new IpV4Counter();

        for (int i = Integer.MIN_VALUE; i < 0; i++) {
            counter.countUniqueIp(i);
        }

        assertEquals(Integer.MAX_VALUE + 1L, counter.count());
    }

    @Test
    void testConcurrentNumber() throws InterruptedException {
        IpV4Counter counter = new IpV4Counter();

        int threadCount = 4;
        int expectedValue = 1024;
        var threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = -expectedValue; j < expectedValue; j++) {
                    counter.countUniqueIp(j);
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(expectedValue * 2, counter.count());
    }

}