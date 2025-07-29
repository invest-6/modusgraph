package com.modusgraph.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies Dgraph predicate settings for a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DgraphField {
    /** Predicate name. Defaults to the field name. */
    String predicate() default "";
    /** Index directive, e.g. "hash", "term", "embedding". */
    String index() default "";
    /** If true adds @upsert directive. */
    boolean upsert() default false;
    /** If true indicates unique constraint. */
    boolean unique() default false;
}
