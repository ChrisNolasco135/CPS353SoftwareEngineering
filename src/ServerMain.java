
import compute.ComputationAPIImplementation;
import datastorage.GrpcStorageComputeAPI;
import network.GrpcServer;
import user.UserComputeAPI;

public final class ServerMain {

    private static final String STORAGE_HOST = "localhost";
    private static final int STORAGE_PORT = 50052;

    private ServerMain() {
    }

    public static void main(String[] args) throws Exception {

        int computePort = args.length > 0
                ? Integer.parseInt(args[0])
                : GrpcServer.DEFAULT_PORT;

        String storageHost = args.length > 1
                ? args[1]
                : STORAGE_HOST;

        int storagePort = args.length > 2
                ? Integer.parseInt(args[2])
                : STORAGE_PORT;

        // Connect to the separate storage process.
        try (GrpcStorageComputeAPI storageAPI =
                new GrpcStorageComputeAPI(
                        storageHost, storagePort)) {

            UserComputeAPI userComputeAPI =
                    new ComputationAPIImplementation(storageAPI);

            GrpcServer server =
                    new GrpcServer(computePort, userComputeAPI).start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.shutdown();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
            }));

            System.out.println(
                    "Compute server listening on port "
                            + server.getPort());

            System.out.println(
                    "Connected to storage server at "
                            + storageHost + ":" + storagePort);

            server.awaitTermination();
        }
    }
}
