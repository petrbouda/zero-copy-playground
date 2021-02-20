package pbouda.zerocopy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import static java.nio.file.StandardOpenOption.*;
import static pbouda.zerocopy.Constants.SOURCE;
import static pbouda.zerocopy.Constants.TARGET_BUFFER;

public class ByteBuffering {

    public static void main(String[] args) throws IOException {
        Files.deleteIfExists(TARGET_BUFFER);
        try (BufferedInputStream input = new BufferedInputStream(Files.newInputStream(SOURCE, READ));
             BufferedOutputStream output = new BufferedOutputStream(Files.newOutputStream(TARGET_BUFFER, CREATE_NEW, WRITE))) {

            long start = System.nanoTime();
            long transferred = input.transferTo(output);
            Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

            System.out.println("Transferred Bytes: " + transferred + ", elapsed: " + elapsed.toMillis());
        }
    }
}
