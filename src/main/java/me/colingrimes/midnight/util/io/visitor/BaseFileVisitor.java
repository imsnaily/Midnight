package me.colingrimes.midnight.util.io.visitor;

import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public abstract class BaseFileVisitor<T> extends SimpleFileVisitor<Path> {

    private final List<T> list = new ArrayList<>();
    private final Path startingPath;
    private final String packageName;

    public BaseFileVisitor(@Nonnull Path startingPath, @Nonnull String packageName) {
        // Normalize paths for comparison - convert both to URI format to handle Windows/Linux differences
        String normalizedPath = normalizePath(startingPath.toString());
        String normalizedPackage = packageName.replace(".", "/");
        
        if (!normalizedPath.endsWith(normalizedPackage)) {
            throw new IllegalArgumentException("Path " + startingPath + " does not end with " + packageName);
        }

        this.startingPath = startingPath;
        this.packageName = packageName;
    }

    /**
     * Gets the list of items found by the visitor.
     *
     * @return the list of items
     */
    @Nonnull
    public List<T> getList() {
        return list;
    }

    /**
     * Converts a file system path to a fully qualified Java class or package name.
     * <p>
     * This method processes the path, removes any file extension related to class files,
     * and formats it to match Java's naming convention for classes and packages.
     * </p>
     *
     * @param path the file system path representing a class or a package
     * @return the fully qualified Java class or package name
     */
    @Nonnull
    String toQualifiedName(@Nonnull Path path) {
        // Normalize both paths using URI format for consistent comparison
        String normalizedPath = normalizePath(path.toString());
        String normalizedStarting = normalizePath(startingPath.toString());
        
        if (!normalizedPath.startsWith(normalizedStarting)) {
            throw new IllegalArgumentException("Path " + path + " is not a child of " + startingPath);
        }

        String name = path.toString();        
        int startLength = startingPath.toString().length();
        if (startLength < name.length() && (name.charAt(startLength) == '/' || name.charAt(startLength) == '\\')) {
            startLength++;
        }
        
        name = name.substring(startLength);
        name = name.replace(".class", "");
        
        // Normalize all separators to dots, handling both Windows and Unix separators
        name = name.replace("\\", ".").replace("/", ".");
        
        return packageName + "." + name;
    }

    /**
     * Normalizes a path string by converting all separators to forward slashes.
     * This ensures consistent path comparison across different operating systems.
     *
     * @param path the path to normalize
     * @return the normalized path
     */
    private String normalizePath(String path) {
        return path.replace("\\", "/");
    }
}
