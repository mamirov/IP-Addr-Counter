package ru.mamirov.ipcounter;

public class IpV4Counter {

    private final ConcurrentBitSet positiveBitSet = new ConcurrentBitSet(Integer.MAX_VALUE);
    private final ConcurrentBitSet negativeBitSet = new ConcurrentBitSet(Integer.MAX_VALUE);

    /**
     * @param ipAddrValue integer value of IpV4 address string literal
     */
    public void addIpV4Address(int ipAddrValue) {
        if (ipAddrValue < 0) {
            if (!negativeBitSet.get(Math.abs(ipAddrValue))) {
                negativeBitSet.set(Math.abs(ipAddrValue));
            }
        } else {
            if (!positiveBitSet.get(ipAddrValue)) {
                positiveBitSet.set(ipAddrValue);
            }
        }
    }

    public long count() {
        return positiveBitSet.valuesSize() + negativeBitSet.valuesSize();
    }
}
