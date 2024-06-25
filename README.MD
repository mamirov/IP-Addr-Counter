IPv4 unique addresses counter

That approach doesn't trigger GC and use additional heap memory besides ConcurrentBitSet, which allocates 1Gb
Ram.

Tested on 8 core, 32Gb RAM hardware

How to run:

Launch main() method on Application.class, change a file path to your IpV4 addresses source