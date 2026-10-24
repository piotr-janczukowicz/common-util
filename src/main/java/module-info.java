module common.util {
    requires static org.jspecify;
    requires static com.github.spotbugs.annotations;
    requires static io.soabase.recordbuilder.core;
    requires static java.compiler;

    exports pl.piotrjanczukowicz.annotation;
    exports pl.piotrjanczukowicz.util;
}
