package pl.piotrjanczukowicz.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

        final Throwable thrown = catchThrowable(() -> EncodingUtils.decodeBase64(value, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("value must not be null");
    }

    @Test
    void decodeBase64_whenMessageNull_shouldThrow() {
        final String value = "aGVsbG8=";
        final String message = null;

        final Throwable thrown = catchThrowable(() -> EncodingUtils.decodeBase64(value, message));

        assertThat(thrown).isInstanceOf(NullPointerException.class).hasMessage("message must not be null");
    }

    @Test
    void decodeBase64_whenMessageBlank_shouldThrow() {
        final String value = "aGVsbG8=";
        final String message = " ";

        final Throwable thrown = catchThrowable(() -> EncodingUtils.decodeBase64(value, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("message must not be blank");
    }

    @ParameterizedTest
    @ValueSource(strings = {"_-8=", "aGVs\r\nbG8="})
    void decodeBase64_whenUrlSafeOrMimeInput_shouldThrow(final String value) {
        final String message = "not valid base64";

        final Throwable thrown = catchThrowable(() -> EncodingUtils.decodeBase64(value, message));

        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage(message);
    }
}
