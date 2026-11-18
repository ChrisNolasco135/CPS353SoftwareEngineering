
package network;

import java.io.IOException;
import java.util.Objects;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import user.UserComputeAPI;

public final class GrpcServer {

    public static final int DEFAULT_PORT = 50051;

    private final Server server;

    public GrpcServer(int port, UserComputeAPI userComputeAPI) {

        Objects.requireNonNull(
                userComputeAPI,
                "User compute API cannot be null");

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Port must be between 1 and 65535");
        }

        server = ServerBuilder.forPort(port)
                .addService(new UserComputeGrpcService(userComputeAPI))
                .build();
    }

    public GrpcServer start() throws IOException {
        server.start();
        return this;
    }

    public int getPort() {
        return server.getPort();
    }

    public void shutdown() throws InterruptedException {
        server.shutdown().awaitTermination();
    }

    public void awaitTermination() throws InterruptedException {
        server.awaitTermination();
    }

    public boolean isShutdown() {
        return server.isShutdown();
    }
}
