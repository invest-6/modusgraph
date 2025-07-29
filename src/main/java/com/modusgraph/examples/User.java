package com.modusgraph.examples;

import com.modusgraph.annotations.DgraphType;
import com.modusgraph.annotations.DgraphField;
import java.util.List;

@DgraphType("User")
public class User {
    @DgraphField(predicate="name", index="term")
    public String name;

    @DgraphField(predicate="age")
    public int age;

    @DgraphField(predicate="embedding", index="embedding")
    public float[] embedding;

    @DgraphField(predicate="posts")
    public List<Post> posts;

    public String uid;
}
