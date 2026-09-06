package pl.piotrjanczukowicz.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RecordBuilderTest {

    @Test
    void build_shouldEqualCanonicalConstructor() {
        final SampleRecord expected = new SampleRecord("octocat", 42);

        final SampleRecord built =
                SampleRecordBuilder.builder().name("octocat").count(42).build();

        assertThat(built).isEqualTo(expected);
    }
}
