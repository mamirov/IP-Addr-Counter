package ru.mamirov.ipcounter;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IpV4ParserTest {

    public static String intToIPv4(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 8) & 0xFF) + "." +
               (ip & 0xFF);
    }

    @Test
    void testParsing() throws IOException, InterruptedException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("ips.txt").getFile());
        HashSet<String> stringIps = new HashSet<>(Files.readAllLines(file.toPath()));
        AtomicInteger count = new AtomicInteger(0);
        IpV4Parser.parseFile(file.getAbsolutePath(), value -> {
            assertTrue(stringIps.contains(intToIPv4(value)));
            count.incrementAndGet();
        });
        assertEquals(5, count.get());
    }

}