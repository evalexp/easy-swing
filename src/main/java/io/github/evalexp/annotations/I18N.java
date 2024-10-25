package io.github.evalexp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark the field to auto wire and set i18n text
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18N {
    /**
     * i18n text key
     * @return i18n text key
     */
    String key();
}
