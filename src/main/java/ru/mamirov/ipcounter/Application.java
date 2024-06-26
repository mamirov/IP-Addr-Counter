package ru.mamirov.ipcounter;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        IpV4Counter counter = new IpV4Counter();
        IpV4Parser.parseFile("ipv4_addresses.txt", counter::addIpV4Address);
        System.out.println("Unique IPv4 addresses: " + counter.count());
    }
}
