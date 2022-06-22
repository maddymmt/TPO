package zad1;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.lang.System.out;

public class Futil extends SimpleFileVisitor<Path>{

    public static void processDir(String startingDirectory, String resultFileName) {
        File resultFile = new File(resultFileName);
        Path startingDirectoryPath = Paths.get(startingDirectory);
        if (resultFile.exists() && resultFile.isFile()) {
            out.println(resultFile.delete());
        }
        try {
            FileChannel fileChannelOut = FileChannel.open(Paths.get(resultFileName), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            Files.walkFileTree(startingDirectoryPath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            FileChannel fileChannelIn = FileChannel.open(file);
                            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannelIn.size());
                            fileChannelIn.read(byteBuffer);
                            byteBuffer.flip();
                            CharBuffer charBuffer = Charset.forName("windows-1250").decode(byteBuffer);
                            fileChannelOut.write(StandardCharsets.UTF_8.encode(charBuffer));
                            return FileVisitResult.CONTINUE;
                        }
                    }
            );
            fileChannelOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
