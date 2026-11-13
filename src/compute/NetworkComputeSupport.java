
package compute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;
import user.DataDestination;
import user.DataSource;
import user.UserComputeRequest;
import user.UserComputeResponse;

final class NetworkComputeSupport {

    // Maximum number of concurrent computation threads.
    static final int MAX_THREADS = 4;

    private NetworkComputeSupport() {
    }

    static UserComputeResponse compute(
            UserComputeRequest request,
            StorageComputeAPI storageAPI,
            ComputationAPI computationAPI,
            boolean multiThreaded) {

        ExecutorService executor = null;

        try {
            Objects.requireNonNull(request, "Request cannot be null");

            DataSource source = Objects.requireNonNull(
                    request.getSource(), "Source cannot be null");

            DataDestination destination = Objects.requireNonNull(
                    request.getDestination(), "Destination cannot be null");

            Objects.requireNonNull(storageAPI, "Storage API cannot be null");
            Objects.requireNonNull(
                    computationAPI, "Computation API cannot be null");

            StorageRequest storageRequest = new StorageRequest() {

                @Override
                public DataDestination getDestination() {
                    return destination;
                }

                @Override
                public DataSource getSource() {
                    return source;
                }
            };

            // Read the input data.
            IntegerData data = Objects.requireNonNull(
                    storageAPI.read(storageRequest),
                    "Storage API failed to read input data");

            List<DataValue> values = Objects.requireNonNull(
                    data.getAllValues(),
                    "Input values cannot be null");

            if (values.isEmpty()) {
                throw new IllegalArgumentException(
                        "Input data contains no values");
            }

            // Validate the input values before processing.
            for (DataValue value : values) {
                Objects.requireNonNull(
                        value, "Input value cannot be null");

                Objects.requireNonNull(
                        value.getValue(), "Integer cannot be null");
            }

            List<ComputationResult> results = new ArrayList<>();

            if (multiThreaded) {

                // Create a fixed-size thread pool.
                executor = Executors.newFixedThreadPool(MAX_THREADS);

                List<Future<ComputationResult>> futures =
                        new ArrayList<>();

                // Submit computations in input order.
                for (DataValue value : values) {

                    ComputationRequest computationRequest =
                            createComputationRequest(value);

                    Future<ComputationResult> future =
                            executor.submit(
                                    () -> computationAPI.compute(
                                            computationRequest));

                    futures.add(future);
                }

                // Retrieve results in input order.
                for (Future<ComputationResult> future : futures) {

                    results.add(
                            Objects.requireNonNull(
                                    future.get(),
                                    "Computation returned null"));
                }

            } else {

                // Single-threaded execution.
                for (DataValue value : values) {

                    ComputationRequest computationRequest =
                            createComputationRequest(value);

                    ComputationResult result =
                            computationAPI.compute(computationRequest);

                    results.add(
                            Objects.requireNonNull(
                                    result, "Computation returned null"));
                }
            }

            // Write results in input order.
            boolean lastResult = false;

            for (ComputationResult result : results) {

                boolean writeSuccessful =
                        storageAPI.write(storageRequest, result);

                if (!writeSuccessful) {
                    throw new IllegalStateException(
                            "Storage API failed to write output data");
                }

                lastResult = result.isPrime();
            }

            return new UserComputeResponse(lastResult);

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            return new UserComputeResponse(
                    "Computation was interrupted");

        } catch (Exception exception) {

            String message = exception.getMessage();

            if (message == null || message.isBlank()) {
                message = "An unexpected error occurred";
            }

            if (exception instanceof ExecutionException
                    && exception.getCause() != null) {
                message = exception.getCause().getMessage();
            }

            return new UserComputeResponse(
                    exception.getClass().getSimpleName()
                            + ": " + message);

        } finally {

            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    private static ComputationRequest createComputationRequest(
            DataValue value) {

        IntegerData singleValueData = () -> value;

        return () -> singleValueData;
    }
}
