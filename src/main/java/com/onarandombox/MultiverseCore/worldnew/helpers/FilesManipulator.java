package com.onarandombox.MultiverseCore.worldnew.helpers;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;

@Service
public class FilesManipulator {

    public Try<Void> deleteFolder(File file) {
        return deleteFolder(file.toPath());
    }

    public Try<Void> deleteFolder(Path path) {
        try (Stream<Path> files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            return Try.success(null);
        } catch (IOException e) {
            Logging.severe("Failed to delete folder: " + path.toAbsolutePath());
            e.printStackTrace();
            return Try.failure(e);
        }
    }

    public Try<Void> copyFolder(File sourceDir, File targetDir) {
        return copyFolder(sourceDir.toPath(), targetDir.toPath(), Collections.emptyList());
    }

    public Try<Void> copyFolder(File sourceDir, File targetDir, List<String> excludeFiles) {
        return copyFolder(sourceDir.toPath(), targetDir.toPath(), excludeFiles);
    }

    public Try<Void> copyFolder(Path sourceDir, Path targetDir) {
        return copyFolder(sourceDir, targetDir, Collections.emptyList());
    }

    public Try<Void> copyFolder(Path sourceDir, Path targetDir, List<String> excludeFiles) {
        try {
            Files.walkFileTree(sourceDir, new CopyDirFileVisitor(sourceDir, targetDir, excludeFiles));
            return Try.success(null);
        } catch (IOException e) {
            Logging.severe("Failed to copy folder: " + sourceDir.toAbsolutePath());
            e.printStackTrace();
            return Try.failure(e);
        }
    }

    private static final class CopyDirFileVisitor extends SimpleFileVisitor<Path> {

        private final Path sourceDir;
        private final Path targetDir;
        private final List<String> excludeFiles;

        private CopyDirFileVisitor(@NotNull Path sourceDir, @NotNull Path targetDir, @NotNull List<String> excludeFiles) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
            this.excludeFiles = excludeFiles;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path newDir = targetDir.resolve(sourceDir.relativize(dir));
            if (!Files.isDirectory(newDir)) {
                Files.createDirectory(newDir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Pass files that are set to ignore
            if (excludeFiles.contains(file.getFileName().toString())) {
                Logging.finest("Ignoring file: " + file.getFileName());
                return FileVisitResult.CONTINUE;
            }
            // Copy the files
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            Files.copy(file, targetFile, COPY_ATTRIBUTES);
            return FileVisitResult.CONTINUE;
        }
    }
}
