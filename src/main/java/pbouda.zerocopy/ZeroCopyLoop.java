package pbouda.zerocopy;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.time.Duration;

import static java.nio.file.StandardOpenOption.*;
import static pbouda.zerocopy.Constants.SOURCE;
import static pbouda.zerocopy.Constants.TARGET_ZERO;

public class ZeroCopyLoop {

    public static void main(String[] args) throws IOException {
        Files.deleteIfExists(TARGET_ZERO);
        try (FileChannel input = FileChannel.open(SOURCE, READ);
             FileChannel output = FileChannel.open(TARGET_ZERO, CREATE_NEW, WRITE)) {

            long start = System.nanoTime();
            long requested = input.size();
            // It's able to transfer only Integer.MAX
            long position = 0;
            while (position < requested) {
                System.out.println(position);
                position += input.transferTo(position, requested, output);
            }
            Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

            System.out.println("Transferred Bytes: " + position + ", requested: " + requested + ", elapsed: " + elapsed.toMillis());
        }
    }
}
