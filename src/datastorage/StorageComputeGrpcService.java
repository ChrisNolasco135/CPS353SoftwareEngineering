
package datastorage;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import compute.ComputationResult;
import io.grpc.stub.StreamObserver;
import network.storage.StorageReadRequest;
import network.storage.StorageReadResponse;
import network.storage.StorageServiceGrpc;
import network.storage.StorageUploadRequest;
import network.storage.StorageUploadResponse;
import network.storage.StorageWriteRequest;
import network.storage.StorageWriteResponse;
import user.DataDestination;
import user.DataSource;

public final class StorageComputeGrpcService
        extends StorageServiceGrpc.StorageServiceImplBase {

    private final StorageComputeAPI storageAPI;

    private final ConcurrentMap<String, Path> uploadedFiles =
            new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Path> outputFiles =
            new ConcurrentHashMap<>();

    public StorageComputeGrpcService(StorageComputeAPI storageAPI) {
        this.storageAPI = storageAPI;
    }

    @Override
    public void upload(
            StorageUploadRequest request,
            StreamObserver<StorageUploadResponse> responseObserver) {

        try {
            List<String> numbers = request.getNumbersList();

            if (numbers.isEmpty()) {
                throw new IllegalArgumentException(
                        "No numbers were provided");
            }

            for (String value : numbers) {
                new BigInteger(value);
            }

            Path inputPath = Files.createTempFile(
                    "storage-input-", ".txt");

            Files.writeString(
                    inputPath,
                    String.join(",", numbers));

            String identifier = UUID.randomUUID().toString();

            uploadedFiles.put(identifier, inputPath);

            StorageUploadResponse response =
                    StorageUploadResponse.newBuilder()
                            .setSuccessful(true)
                            .setSourceIdentifier(identifier)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception exception) {

            StorageUploadResponse response =
                    StorageUploadResponse.newBuilder()
                            .setSuccessful(false)
                            .setErrorMessage(exception.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void read(
            StorageReadRequest request,
            StreamObserver<StorageReadResponse> responseObserver) {

        try {
            Path inputPath = uploadedFiles.get(
                    request.getSourceIdentifier());

            if (inputPath == null) {
                throw new IllegalArgumentException(
                        "Uploaded input was not found");
            }

            StorageRequest storageRequest = new StorageRequest() {

                @Override
                public DataSource getSource() {
                    return () -> inputPath.toString();
                }

                @Override
                public DataDestination getDestination() {
                    return null;
                }
            };

            IntegerData data = storageAPI.read(storageRequest);

            if (data == null) {
                throw new IllegalStateException(
                        "Storage API returned null");
            }

            List<String> values = data.getAllValues()
                    .stream()
                    .map(value -> value.getValue().toString())
                    .toList();

            StorageReadResponse response =
                    StorageReadResponse.newBuilder()
                            .setSuccessful(true)
                            .addAllNumbers(values)
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception exception) {

            StorageReadResponse response =
                    StorageReadResponse.newBuilder()
                            .setSuccessful(false)
                            .setErrorMessage(exception.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void write(
            StorageWriteRequest request,
            StreamObserver<StorageWriteResponse> responseObserver) {

        try {
            String identifier =
                    request.getDestinationIdentifier();

            Path outputPath = outputFiles.computeIfAbsent(
                    identifier,
                    key -> {
                        try {
                            return Files.createTempFile(
                                    "storage-output-", ".txt");
                        } catch (Exception exception) {
                            throw new IllegalStateException(exception);
                        }
                    });

            StorageRequest storageRequest = new StorageRequest() {

                @Override
                public DataSource getSource() {
                    return null;
                }

                @Override
                public DataDestination getDestination() {
                    return () -> outputPath.toString();
                }
            };

            ComputationResult result = request::getPrime;

            boolean successful =
                    storageAPI.write(storageRequest, result);

            StorageWriteResponse response =
                    StorageWriteResponse.newBuilder()
                            .setSuccessful(successful)
                            .setErrorMessage(
                                    successful ? "" : "Storage write failed")
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception exception) {

            StorageWriteResponse response =
                    StorageWriteResponse.newBuilder()
                            .setSuccessful(false)
                            .setErrorMessage(exception.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
