module common.util {
    requires static org.jspecify;
    requires static com.github.spotbugs.annotations;
    requires static io.soabase.recordbuilder.core;
    requires static java.compiler;

    exports io.github.piotr_janczukowicz.annotation;
    exports io.github.piotr_janczukowicz.util;
}
