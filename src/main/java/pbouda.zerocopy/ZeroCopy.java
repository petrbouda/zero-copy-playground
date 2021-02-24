package pbouda.zerocopy;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.time.Duration;

import static java.nio.file.StandardOpenOption.*;
import static pbouda.zerocopy.Constants.*;

public class ZeroCopy {

    public static void main(String[] args) throws IOException {
        System.out.println("Press ENTER");
        System.in.read();

        Files.deleteIfExists(TARGET_ZERO);
        try (FileChannel input = FileChannel.open(SOURCE, READ);
             FileChannel output = FileChannel.open(TARGET_ZERO, CREATE_NEW, WRITE)) {

            long start = System.nanoTime();
            long requested = input.size();
            // It's able to transfer only Integer.MAX
            System.out.println(requested);
            long transferred = input.transferTo(0, requested, output);
            Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

            System.out.println("Transferred Bytes: " + transferred + ", requested: " + requested + ", elapsed: " + elapsed.toMillis());
        }
    }
}
