# ZERO COPY

- https://developer.ibm.com/languages/java/articles/j-zerocopy/
- only `transferTo` supports DMA transfer
- it's able to transfer only Integer.MAX bytes
- `transferTo` copies first 2GB very fast then it gets slower

```
java -XX:TieredStopAtLevel=1 -agentpath:/home/pbouda/experiments/async-profiler/build/libasyncProfiler.so=start,file=/tmp/cpu-zero.html,interval=1ms,event=cpu -classpath ./target/classes pbouda.zerocopy.ZeroCopy

java -XX:TieredStopAtLevel=1 -agentpath:/home/pbouda/experiments/async-profiler/build/libasyncProfiler.so=start,file=/tmp/cpu-zero-loop.html,interval=1ms,event=cpu -classpath ./target/classes pbouda.zerocopy.ZeroCopyLoop

java -XX:TieredStopAtLevel=1 -agentpath:/home/pbouda/experiments/async-profiler/build/libasyncProfiler.so=start,file=/tmp/cpu-byte-buffer.html,interval=1ms,event=cpu -classpath ./target/classes pbouda.zerocopy.HeapBuffering

java -XX:TieredStopAtLevel=1 -agentpath:/home/pbouda/experiments/async-profiler/build/libasyncProfiler.so=start,file=/tmp/cpu-transfer-from-buffer.html,interval=1ms,event=cpu -classpath ./target/classes pbouda.zerocopy.TransferFromBuffering
```

- Tested on 9GB binary file

##### Buffered InputStream

- `Transferred Bytes: 9740308808, elapsed: 43560ms`

##### FileChannel#transferFrom

- `Transferred Bytes: 9740308808, requested: 9740308808, elapsed: 58862ms`

##### FileChannel#transferTo smaller chunks

- `Transferred Bytes: 9740308808, requested: 9740308808, elapsed: 39775`

##### FileChannel#transferTo Integer#MAX chunks / reproduceable only without attaching AsyncProfiler 

- `Transferred Bytes: 9740308808, requested: 9740308808, elapsed: 53152`

#### File to 2GB (Integer#MAX)

- Zero copy (DMA) transfers only 2GB files MAX and needs to be executed in a loop, does not guarantee to transfer all Integer#MAX files 

```
java -XX:TieredStopAtLevel=1 -Xmx300m -classpath ./target/classes pbouda.zerocopy.ZeroCopy     
Press ENTER

9740308808
Transferred Bytes: 203886592, requested: 9740308808, elapsed: 432
```

##### Recommendation

`Always use a loop with transferTo` - otherwise you can end up with
transferred fewer bytes than was expected.

```
Transfers bytes from this channel's file to the given writable byte channel.
An attempt is made to read up to count bytes starting at the given position 
in this channel's file and write them to the target channel. An invocation of 
this method may or may not transfer all of the requested bytes; 
whether or not it does so depends upon the natures and states of the channels. 
Fewer than the requested number of bytes are transferred if this channel's file 
contains fewer than count bytes starting at the given position, or if the target 
channel is non-blocking and it has fewer than count bytes free in its output buffer.
```

## Buffering in HEAP 

- Uses only User-level buffer for transferring bytes, it does not have any other impact to JAVA HEAP or Native Memory or RSS 

```
java -XX:TieredStopAtLevel=1 -Xmx300m -classpath ./target/classes pbouda.zerocopy.HeapBuffering
```

```java
public long transferTo(OutputStream out) throws IOException {
    Objects.requireNonNull(out, "out");
    long transferred = 0;
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int read;
    while ((read = this.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
        out.write(buffer, 0, read);
        transferred += read;
    }
    return transferred;
}
```