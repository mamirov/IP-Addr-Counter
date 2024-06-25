package ru.mamirov.ipcounter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class IPv4Generator {

    public static void main(String[] args) {
        long numberOfAddresses = 10_000_000_000_000L;
        String filePath = "ipv4_addresses.txt";
        generateIPv4Addresses(numberOfAddresses, filePath);
    }

    public static void generateIPv4Addresses(long numberOfAddresses, String filePath) {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (long i = 0; i < numberOfAddresses; i++) {
                // Generate random octets
                int[] octets = new int[4];
                for (int j = 0; j < 4; j++) {
                    octets[j] = random.nextInt(255); // Random octet value between 0 and 255
                }

                // Format the IPv4 address and write it to the file
                String ipAddress = String.format("%d.%d.%d.%d", octets[0], octets[1], octets[2], octets[3]);
                writer.write(ipAddress);
                writer.newLine(); // Add a newline after each address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("IPv4 addresses written to file: " + filePath);
    }
}
