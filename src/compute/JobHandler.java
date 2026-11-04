
package compute;

import java.util.List;
import java.util.Objects;

import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;
import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class JobHandler implements UserComputeAPI {

    private final StorageComputeAPI storageAPI;
    private final ComputationAPI computationAPI;

    public JobHandler(
            StorageComputeAPI storageAPI,
            ComputationAPI computationAPI) {

        this.storageAPI = Objects.requireNonNull(
                storageAPI, "Storage API cannot be null");

        this.computationAPI = Objects.requireNonNull(
                computationAPI, "Computation API cannot be null");
    }

    @Override
    public UserComputeResponse compute(UserComputeRequest request) {
        try {
            // Validate the request.
            Objects.requireNonNull(
                    request, "Request cannot be null");

            DataSource source = Objects.requireNonNull(
                    request.getSource(), "Source cannot be null");

            DataDestination destination = Objects.requireNonNull(
                    request.getDestination(),
                    "Destination cannot be null");

            // Create the storage request.
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
                    "Storage API returned null data");

            List<DataValue> values = Objects.requireNonNull(
                    data.getAllValues(),
                    "Input values cannot be null");

            if (values.isEmpty()) {
                throw new IllegalArgumentException(
                        "Input data contains no values");
            }

            boolean lastResult = false;

            // Process every input value.
            for (DataValue value : values) {
                Objects.requireNonNull(
                        value, "Input value cannot be null");

                Objects.requireNonNull(
                        value.getValue(), "Integer cannot be null");

                IntegerData singleValueData = () -> value;

                ComputationRequest computationRequest =
                        () -> singleValueData;

                ComputationResult result = Objects.requireNonNull(
                        computationAPI.compute(computationRequest),
                        "Computation API returned null result");

                storageAPI.write(storageRequest, result);

                lastResult = result.isPrime();
            }

            // Return the successful result.
            return new UserComputeResponse(lastResult);

        } catch (Exception exception) {
            // Convert component failures into an identifiable API response.
            String message = exception.getMessage();

            if (message == null || message.isBlank()) {
                message = "An unexpected error occurred";
            }

            return new UserComputeResponse(
                    exception.getClass().getSimpleName() + ": " + message);
        }
    }
}
