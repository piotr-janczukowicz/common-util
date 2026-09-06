package pl.piotrjanczukowicz.util;

import static pl.piotrjanczukowicz.util.ValidationUtils.requireNonNull;
import static pl.piotrjanczukowicz.util.ValidationUtils.requireNotBlank;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Classpath resource helpers.
 *
 * <p>Loads resources that ship on the classpath (the same lookup as {@link Class#getResourceAsStream(String)}). A
 * relative {@code fileName} is resolved against {@code type}'s package. A {@code fileName} that starts with {@code '/'}
 * is resolved from the classpath root. These methods read the resource fully and close the stream. They do not open
 * filesystem paths.
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Reads the named classpath resource as UTF-8 text.
     *
     * <p>Decodes {@link #readBytes(Class, String)} with {@link StandardCharsets#UTF_8}. Resource lookup follows
     * {@link Class#getResourceAsStream(String)}: relative names are in {@code type}'s package; names that start with
     * {@code '/'} are at the classpath root.
     *
     * @param type class used to locate the resource
     * @param fileName resource name, not a filesystem path
     * @return resource contents
     * @throws NullPointerException if {@code type} is {@code null}
     * @throws IllegalArgumentException if {@code fileName} is blank
     * @throws IllegalStateException if the resource is missing
     * @throws UncheckedIOException if reading or closing the resource fails
     */
    public static String readString(final Class<?> type, final String fileName) {
        return new String(readBytes(type, fileName), StandardCharsets.UTF_8);
    }

    /**
     * Reads the named classpath resource as bytes.
     *
     * <p>Resource lookup follows {@link Class#getResourceAsStream(String)}: relative names are in {@code type}'s
     * package; names that start with {@code '/'} are at the classpath root. The stream is closed after the resource is
     * read.
     *
     * @param type class used to locate the resource
     * @param fileName resource name, not a filesystem path
     * @return resource bytes
     * @throws NullPointerException if {@code type} is {@code null}
     * @throws IllegalArgumentException if {@code fileName} is blank
     * @throws IllegalStateException if the resource is missing
     * @throws UncheckedIOException if reading or closing the resource fails
     */
    public static byte[] readBytes(final Class<?> type, final String fileName) {
        requireNonNull(type, "type");
        requireNotBlank(fileName, "fileName");

        try (final InputStream stream = type.getResourceAsStream(fileName)) {
            if (stream == null) {
                throw new IllegalStateException(
                        "Missing resource %s in package %s".formatted(fileName, type.getPackageName()));
            }
            return stream.readAllBytes();
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
}
