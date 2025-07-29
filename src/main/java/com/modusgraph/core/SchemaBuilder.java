package com.modusgraph.core;

import com.modusgraph.annotations.DgraphField;
import com.modusgraph.annotations.DgraphType;
import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * Utility for generating Dgraph schema from annotated classes.
 */
public final class SchemaBuilder {
    private SchemaBuilder() {}

    public static String buildSchema(Class<?> clazz) {
        StringBuilder schema = new StringBuilder();
        DgraphType t = clazz.getAnnotation(DgraphType.class);
        String typeName = t != null && !t.value().isEmpty() ? t.value() : clazz.getSimpleName();

        for (Field f : clazz.getDeclaredFields()) {
            DgraphField df = f.getAnnotation(DgraphField.class);
            if (df == null) {
                continue;
            }
            String predicate = df.predicate().isEmpty() ? f.getName() : df.predicate();
            String dType = mapType(f);
            schema.append(predicate).append(": ").append(dType);
            if (!df.index().isEmpty()) {
                schema.append(" @index(").append(df.index()).append(")");
            }
            if (df.upsert()) {
                schema.append(" @upsert");
            }
            if (df.unique()) {
                if (df.index().isEmpty()) {
                    schema.append(" @index(hash)");
                }
                schema.append(" @upsert");
            }
            schema.append(" .\n");
        }

        StringJoiner joiner = new StringJoiner("\n", "type " + typeName + " {\n", "\n}\n");
        for (Field f : clazz.getDeclaredFields()) {
            DgraphField df = f.getAnnotation(DgraphField.class);
            if (df != null) {
                String predicate = df.predicate().isEmpty() ? f.getName() : df.predicate();
                joiner.add("\t" + predicate);
            }
        }
        schema.append(joiner.toString());
        return schema.toString();
    }

    private static String mapType(Field f) {
        Class<?> type = f.getType();
        if (type == String.class) {
            return "string";
        }
        if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
            return "int";
        }
        if (type == float.class || type == Float.class || type == double.class || type == Double.class) {
            return "float";
        }
        if (type == boolean.class || type == Boolean.class) {
            return "bool";
        }
        if (type == float[].class) {
            return "float32vector";
        }
        return "uid";
    }
}
