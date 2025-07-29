package com.modusgraph;

import com.modusgraph.core.SchemaBuilder;
import com.modusgraph.examples.User;
import org.junit.Test;

public class SchemaBuilderTest {
    @Test
    public void printSchema() {
        String schema = SchemaBuilder.buildSchema(User.class);
        System.out.println(schema);
    }
}
