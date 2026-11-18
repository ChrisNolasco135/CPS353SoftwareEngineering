
import datastorage.StorageComputeAPI;
import datastorage.StorageComputeAPIImplementation;
import datastorage.StorageComputeGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public final class StorageServerMain {

    private static final int DEFAULT_PORT = 50052;

    private StorageServerMain() {
    }

    public static void main(String[] args) throws Exception {

        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : DEFAULT_PORT;

        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        Server server = ServerBuilder.forPort(port)
                .addService(
                        new StorageComputeGrpcService(storageAPI))
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        System.out.println(
                "Storage server listening on port " + port);

        server.awaitTermination();
    }
}
