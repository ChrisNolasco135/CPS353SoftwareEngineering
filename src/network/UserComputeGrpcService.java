package network;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import network.grpc.ComputeRequest;
import network.grpc.ComputeResponse;
import network.grpc.UserComputeServiceGrpc;
import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public final class UserComputeGrpcService
        extends UserComputeServiceGrpc.UserComputeServiceImplBase {

    private final UserComputeAPI userComputeAPI;

    public UserComputeGrpcService(UserComputeAPI userComputeAPI) {
        this.userComputeAPI = Objects.requireNonNull(
                userComputeAPI,
                "User compute API cannot be null");
    }

    @Override
    public void compute(
            ComputeRequest request,
            StreamObserver<ComputeResponse> responseObserver) {

        if (request.getNumbersCount() == 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("At least one integer is required")
                    .asRuntimeException());
            return;
        }

        Path inputPath = null;
        Path outputPath = null;

        try {
            List<String> numbers = request.getNumbersList();

            // Validate every integer before creating the input file.
            for (String number : numbers) {
                if (number == null || number.isBlank()) {
                    throw new IllegalArgumentException(
                            "Input contains an empty integer");
                }

                new BigInteger(number);
            }

            // Create temporary files on the server.
            inputPath = Files.createTempFile(
                    "compute-input-", ".txt");

            outputPath = Files.createTempFile(
                    "compute-output-", ".txt");

            // Existing storage implementation expects comma-separated input.
            Files.writeString(
                    inputPath,
                    String.join(",", numbers));

            final String inputIdentifier =
                    inputPath.toAbsolutePath().toString();

            final String outputIdentifier =
                    outputPath.toAbsolutePath().toString();

            // Adapt the gRPC request to the existing UserComputeAPI.
            UserComputeRequest apiRequest = new UserComputeRequest() {

                @Override
                public DataSource getSource() {
                    return () -> inputIdentifier;
                }

                @Override
                public DataDestination getDestination() {
                    return () -> outputIdentifier;
                }

                @Override
                public String getDelimiter() {
                    return ",";
                }

                @Override
                public boolean useDefaultDelimiter() {
                    return true;
                }
            };

            UserComputeResponse apiResponse =
                    Objects.requireNonNull(
                            userComputeAPI.compute(apiRequest),
                            "User compute API returned null");

            ComputeResponse.Builder response =
                    ComputeResponse.newBuilder()
                            .setSuccessful(apiResponse.isSuccessful());

            if (apiResponse.isSuccessful()) {
                response.addAllResults(apiResponse.getResults());
            } else if (apiResponse.getErrorMessage() != null) {
                response.setErrorMessage(
                        apiResponse.getErrorMessage());
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException exception) {

            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(exception.getMessage())
                    .withCause(exception)
                    .asRuntimeException());

        } catch (Exception exception) {

            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to process computation request")
                    .withCause(exception)
                    .asRuntimeException());

        } finally {
            deleteTemporaryFile(inputPath);
            deleteTemporaryFile(outputPath);
        }
    }

    private void deleteTemporaryFile(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                System.err.println(
                        "Unable to delete temporary file: " + path);
            }
        }
    }
}