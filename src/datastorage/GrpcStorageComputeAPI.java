
package datastorage;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import compute.ComputationResult;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.storage.StorageReadRequest;
import network.storage.StorageReadResponse;
import network.storage.StorageServiceGrpc;
import network.storage.StorageUploadRequest;
import network.storage.StorageUploadResponse;
import network.storage.StorageWriteRequest;
import network.storage.StorageWriteResponse;

public final class GrpcStorageComputeAPI
        implements StorageComputeAPI, AutoCloseable {

    private final ManagedChannel channel;
    private final StorageServiceGrpc.StorageServiceBlockingStub stub;

    public GrpcStorageComputeAPI(String host, int port) {

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

        stub = StorageServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public IntegerData read(StorageRequest request) {

        try {
            if (request == null || request.getSource() == null) {
                System.err.println(
                        "Storage read failed: request or source is null");
                return null;
            }

            String identifier =
                    request.getSource().getIdentifier();

            if (identifier == null || identifier.isBlank()) {
                System.err.println(
                        "Storage read failed: source identifier is blank");
                return null;
            }

            // Read the input file on the compute server.
            Path inputPath = Path.of(identifier);

            if (!Files.isRegularFile(inputPath)) {
                System.err.println(
                        "Storage read failed: input file does not exist: "
                                + inputPath);
                return null;
            }

            if (!Files.isReadable(inputPath)) {
                System.err.println(
                        "Storage read failed: input file is not readable: "
                                + inputPath);
                return null;
            }

            String contents = Files.readString(inputPath);

            if (contents.isBlank()) {
                System.err.println(
                        "Storage read failed: input file is empty");
                return null;
            }

            String[] tokens = contents.trim().split("[,\\s]+");

            List<String> numbers = new ArrayList<>();

            // Validate and normalize the integers.
            for (String token : tokens) {
                BigInteger number = new BigInteger(token);
                numbers.add(number.toString());
            }

            // Upload the input data to the storage process.
            StorageUploadResponse uploadResponse =
                    stub.upload(
                            StorageUploadRequest.newBuilder()
                                    .addAllNumbers(numbers)
                                    .build());

            if (!uploadResponse.getSuccessful()) {
                System.err.println(
                        "Storage upload failed: "
                                + uploadResponse.getErrorMessage());
                return null;
            }

            if (uploadResponse.getSourceIdentifier().isBlank()) {
                System.err.println(
                        "Storage upload failed: source identifier is blank");
                return null;
            }

            // Ask the storage process to read the uploaded data.
            StorageReadResponse response =
                    stub.read(
                            StorageReadRequest.newBuilder()
                                    .setSourceIdentifier(
                                            uploadResponse.getSourceIdentifier())
                                    .build());

            if (!response.getSuccessful()) {
                System.err.println(
                        "Storage read failed: "
                                + response.getErrorMessage());
                return null;
            }

            List<DataValue> values = response.getNumbersList()
                    .stream()
                    .map(this::createDataValue)
                    .toList();

            if (values.isEmpty()) {
                System.err.println(
                        "Storage read failed: no numbers were returned");
                return null;
            }

            return new IntegerData() {

                @Override
                public DataValue getValues() {
                    return values.get(0);
                }

                @Override
                public List<DataValue> getAllValues() {
                    return values;
                }
            };

        } catch (Exception exception) {
            System.err.println(
                    "Remote storage read failed: "
                            + exception.getClass().getSimpleName()
                            + ": " + exception.getMessage());

            exception.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean write(
            StorageRequest request,
            ComputationResult result) {

        try {
            if (request == null
                    || request.getDestination() == null
                    || result == null) {
                System.err.println(
                        "Storage write failed: request, destination, or result is null");
                return false;
            }

            String identifier =
                    request.getDestination().getIdentifier();

            if (identifier == null || identifier.isBlank()) {
                System.err.println(
                        "Storage write failed: destination identifier is blank");
                return false;
            }

            StorageWriteResponse response =
                    stub.write(
                            StorageWriteRequest.newBuilder()
                                    .setDestinationIdentifier(identifier)
                                    .setPrime(result.isPrime())
                                    .build());

            if (!response.getSuccessful()) {
                System.err.println(
                        "Storage write failed: "
                                + response.getErrorMessage());
                return false;
            }

            return true;

        } catch (Exception exception) {
            System.err.println(
                    "Remote storage write failed: "
                            + exception.getClass().getSimpleName()
                            + ": " + exception.getMessage());

            exception.printStackTrace();
            return false;
        }
    }

    private DataValue createDataValue(String value) {

        BigInteger number = new BigInteger(value);

        return () -> number;
    }

    @Override
    public void close() {
        channel.shutdownNow();
    }
}
