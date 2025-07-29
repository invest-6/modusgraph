package com.modusgraph.client;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.Transaction;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Simple wrapper around the official Dgraph client.
 */
public class DgraphClientWrapper implements AutoCloseable {
    private final ManagedChannel channel;
    private final DgraphClient client;

    public DgraphClientWrapper(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.client = new DgraphClient(DgraphGrpc.newBlockingStub(channel));
    }

    public DgraphClient getClient() {
        return client;
    }

    public Transaction newTxn() {
        return client.newTransaction();
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.shutdownNow();
        }
    }
}
