package network;

import java.util.List;
import java.util.Objects;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.grpc.ComputeRequest;
import network.grpc.ComputeResponse;
import network.grpc.UserComputeServiceGrpc;

public final class GrpcClient implements AutoCloseable {

    private final ManagedChannel channel;
    private final UserComputeServiceGrpc.UserComputeServiceBlockingStub stub;

    public GrpcClient(String host, int port) {

        Objects.requireNonNull(host, "Host cannot be null");

        if (host.isBlank()) {
            throw new IllegalArgumentException(
                    "Host cannot be blank");
        }

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Port must be between 1 and 65535");
        }

        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        stub = UserComputeServiceGrpc.newBlockingStub(channel);
    }

    public ComputeResponse compute(List<String> numbers) {

        Objects.requireNonNull(numbers, "Numbers cannot be null");

        ComputeRequest request = ComputeRequest.newBuilder()
                .addAllNumbers(numbers)
                .build();

        return stub.compute(request);
    }

    @Override
    public void close() {
        channel.shutdownNow();
    }
}