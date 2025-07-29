package com.modusgraph.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Dgraph node type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DgraphType {
    /** Name of the type in Dgraph. Defaults to class simple name. */
    String value() default "";
}
