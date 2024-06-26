package ru.mamirov.ipcounter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public class IpV4Parser {

    public static void parseFile(String filePath,
                                 IntConsumer ipV4ValueConsumer) throws IOException, InterruptedException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel fileChannel = file.getChannel()) {

            long fileSize = file.length();
            //calculate tasks per chunks
            int tasksCount = Runtime.getRuntime().availableProcessors();
            if (fileSize > (long) tasksCount * Integer.MAX_VALUE) {
                tasksCount = (int) (fileSize / (Integer.MAX_VALUE - 1)) + 1;
            }

            //split a file to chunks
            long[] chunks = new long[tasksCount];
            for (int i = 1; i < tasksCount; i++) {
                var start = i * fileSize / tasksCount;
                file.seek(start);
                while (file.read() != '\n' && file.getFilePointer() != fileSize) {
                }
                start = file.getFilePointer();
                chunks[i] = start;
            }

            //submit tasks to eService, use available processors
            try (ExecutorService executorService =
                         //adjust depends on your hardware configuration
                         Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors())) {

                for (int i = 0; i < tasksCount; i++) {
                    long chunk = chunks[i];
                    var end = i == tasksCount - 1 ? fileSize - chunk : chunks[i + 1] - chunk;
                    executorService.execute(() -> parseIpV4IntValue(fileChannel, chunk, end, ipV4ValueConsumer));
                }

                executorService.shutdown();
                //adjust depends on your hardware configuration
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            }
        }
    }

    /**
     * Parse IpV4 string literal to int value
     * @param fileChannel
     * @param chunk
     * @param end
     * @param ipValueProducer
     */
    private static void parseIpV4IntValue(FileChannel fileChannel, long chunk, long end, IntConsumer ipValueProducer) {
        try {
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, chunk, end);
            int octet = 0;
            int ipAddrCom = 0;
            buffer.position(0);
            for (int j = 0; j < end; j++) {
                char c = (char) buffer.get();
                if (c == '.') {
                    ipAddrCom = (ipAddrCom << 8) | octet;
                    octet = 0;
                } else if (c != '\n' && c != '\r') {
                    octet = octet * 10 + (c - '0');
                    if (j == end - 1) {
                        ipAddrCom = (ipAddrCom << 8) | octet;
                        ipValueProducer.accept(ipAddrCom);
                        ipAddrCom = 0;
                        octet = 0;
                    }
                } else {
                    if (c != '\n') {
                        ipAddrCom = (ipAddrCom << 8) | octet;
                        ipValueProducer.accept(ipAddrCom);
                        ipAddrCom = 0;
                        octet = 0;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
