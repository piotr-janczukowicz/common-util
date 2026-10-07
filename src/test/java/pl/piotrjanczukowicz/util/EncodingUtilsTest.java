package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class EncodingUtilsTest {

    @Test
    void constructor_whenInstantiatedViaReflection_shouldThrow() throws Exception {
        final Constructor<EncodingUtils> constructor = EncodingUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void decodeBase64_whenValid_shouldReturnDecodedBytes() {
        final String value = "aGVsbG8=";
        final String message = "not valid base64";

        final byte[] result = EncodingUtils.decodeBase64(value, message);

        assertThat(result).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void decodeBase64_whenInvalid_shouldThrowWithoutCause() {
        final String value = "not*base64";
        final String message = "not valid base64";

        assertThatThrownBy(() -> EncodingUtils.decodeBase64(value, message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message)
                .hasNoCause();
    }

    @Test
    void decodeBase64_whenNull_shouldThrow() {
        final String value = null;
        final String message = "not valid base64";

        assertThatThrownBy(() -> EncodingUtils.decodeBase64(value, message))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(message);
    }
}
