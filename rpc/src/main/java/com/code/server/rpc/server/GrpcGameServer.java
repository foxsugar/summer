package com.code.server.rpc.server;

import com.code.server.grpc.idl.GameServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Created by sunxianping on 2017/4/27.
 */
public class GrpcGameServer {

    private Server server;
    public Server startServer(int port, GameServiceGrpc.GameServiceImplBase impl) throws IOException {
        server = ServerBuilder
                .forPort(port)
                .addService(impl)
                .build()
                .start();

        return server;
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
}
