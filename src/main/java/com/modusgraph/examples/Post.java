package com.modusgraph.examples;

import com.modusgraph.annotations.DgraphType;
import com.modusgraph.annotations.DgraphField;

@DgraphType("Post")
public class Post {
    @DgraphField(predicate="title", index="term")
    public String title;

    @DgraphField(predicate="content")
    public String content;

    public String uid;
}
