package com.modusgraph.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.modusgraph.client.DgraphClientWrapper;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.DgraphProto.Operation;
import io.dgraph.Transaction;
import com.google.protobuf.ByteString;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Basic DAO providing CRUD operations mapped to Java classes.
 */
public class GraphDAO {
    private final DgraphClientWrapper client;
    private final Set<Class<?>> schemas = new HashSet<>();
    private final Gson gson = new GsonBuilder().create();

    public GraphDAO(DgraphClientWrapper client) {
        this.client = client;
    }

    public <T> void insert(T obj) throws Exception {
        ensureSchema(obj.getClass());
        ensureBlankUid(obj);
        String json = gson.toJson(obj);
        Transaction txn = client.newTxn();
        try {
            Mutation mu = Mutation.newBuilder()
                    .setSetJson(ByteString.copyFromUtf8(json))
                    .build();
            var assigned = txn.mutate(mu);
            txn.commit();
            updateUID(obj, assigned.getUidsMap());
        } finally {
            txn.discard();
        }
    }

    public <T> void update(T obj) throws Exception {
        ensureSchema(obj.getClass());
        String json = gson.toJson(obj);
        Transaction txn = client.newTxn();
        try {
            Mutation mu = Mutation.newBuilder()
                    .setSetJson(ByteString.copyFromUtf8(json))
                    .build();
            txn.mutate(mu);
            txn.commit();
        } finally {
            txn.discard();
        }
    }

    public <T> T get(Class<T> clazz, String uid) throws Exception {
        ensureSchema(clazz);
        String q = String.format("{ node(func: uid(%s)) { uid expand(_all_) }}", uid);
        Transaction txn = client.newTxn();
        Response resp = txn.query(q);
        JsonObject root = JsonParser.parseString(resp.getJson().toStringUtf8()).getAsJsonObject();
        if (!root.has("node")) return null;
        return gson.fromJson(root.getAsJsonArray("node").get(0), clazz);
    }

    public void delete(String uid) throws Exception {
        Transaction txn = client.newTxn();
        try {
            Mutation mu = Mutation.newBuilder()
                    .setDeleteJson(String.format("{\"uid\": \"%s\"}", uid))
                    .build();
            txn.mutate(mu);
            txn.commit();
        } finally {
            txn.discard();
        }
    }

    private <T> void ensureSchema(Class<T> clazz) throws Exception {
        if (schemas.contains(clazz)) {
            return;
        }
        String schema = SchemaBuilder.buildSchema(clazz);
        Operation op = Operation.newBuilder().setSchema(schema).build();
        client.getClient().alter(op);
        schemas.add(clazz);
    }

    private <T> void updateUID(T obj, Map<String, String> uids) throws IllegalAccessException {
        if (uids.isEmpty()) return;
        Field uidField = findUidField(obj.getClass());
        if (uidField != null) {
            uidField.setAccessible(true);
            String uid = uids.values().iterator().next();
            uidField.set(obj, uid);
        }
    }

    private Field findUidField(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getName().equalsIgnoreCase("uid")) {
                return f;
            }
        }
        return null;
    }

    private <T> void ensureBlankUid(T obj) throws IllegalAccessException {
        Field f = findUidField(obj.getClass());
        if (f == null) {
            return;
        }
        f.setAccessible(true);
        Object v = f.get(obj);
        if (v == null || v.toString().isEmpty()) {
            f.set(obj, "_:new");
        }
    }
}
