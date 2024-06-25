package ru.mamirov.ipcounter;

import java.util.concurrent.atomic.AtomicLong;

public class IpV4Counter {

    private final ConcurrentBitSet positiveBitSet = new ConcurrentBitSet(Integer.MAX_VALUE);
    private final ConcurrentBitSet negativeBitSet = new ConcurrentBitSet(Integer.MAX_VALUE);
    private final AtomicLong uniqueCount = new AtomicLong(0);

    /**
     * @param ipAddrValue integer value of IpV4 address string literal
     */
    public void countUniqueIp(int ipAddrValue) {
        if (ipAddrValue < 0) {
            if (!negativeBitSet.get(Math.abs(ipAddrValue))) {
                negativeBitSet.set(Math.abs(ipAddrValue));
                uniqueCount.incrementAndGet();
            }
        } else {
            if (!positiveBitSet.get(ipAddrValue)) {
                positiveBitSet.set(ipAddrValue);
                uniqueCount.incrementAndGet();
            }
        }
    }

    public long count() {
        return uniqueCount.get();
    }
}
