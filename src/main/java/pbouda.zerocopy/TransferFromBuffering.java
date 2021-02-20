package pbouda.zerocopy;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.time.Duration;

import static java.nio.file.StandardOpenOption.*;
import static pbouda.zerocopy.Constants.*;

public class TransferFromBuffering {

    public static void main(String[] args) throws IOException {
        Files.deleteIfExists(TARGET_TRANSFER_FROM);
        try (FileChannel input = FileChannel.open(SOURCE, READ);
             FileChannel output = FileChannel.open(TARGET_TRANSFER_FROM, CREATE_NEW, WRITE)) {

            long start = System.nanoTime();
            long requested = input.size();
            long transferred = output.transferFrom(input, 0, requested);
            Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

            System.out.println("Transferred Bytes: " + transferred + ", requested: " + requested + ", elapsed: " + elapsed.toMillis());
        }
    }
}
