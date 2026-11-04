
package user;

import java.util.Objects;

import compute.ComputationAPI;
import compute.ComputationRequest;
import compute.ComputationResult;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;

public class UserComputeAPIImplementation implements UserComputeAPI {

    private final StorageComputeAPI storageAPI;
    private final ComputationAPI computationAPI;

    public UserComputeAPIImplementation(
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
        Objects.requireNonNull(
                request, "User computation request cannot be null");

        DataSource source = Objects.requireNonNull(
                request.getSource(), "Data source cannot be null");

        DataDestination destination = Objects.requireNonNull(
                request.getDestination(),
                "Data destination cannot be null");

        StorageRequest storageRequest = new StorageRequest() {
            @Override
            public DataSource getSource() {
                return source;
            }

            @Override
            public DataDestination getDestination() {
                return destination;
            }
        };

        IntegerData inputData = Objects.requireNonNull(
                storageAPI.read(storageRequest),
                "Storage API returned null input data");

        ComputationRequest computationRequest = new ComputationRequest() {
            @Override
            public IntegerData getData() {
                return inputData;
            }
        };

        ComputationResult result = Objects.requireNonNull(
                computationAPI.compute(computationRequest),
                "Computation API returned a null result");

        storageAPI.write(storageRequest, result);

        return new UserComputeResponse(result.isPrime());

    } catch (Exception exception) {
        // Translate checked and unchecked exceptions into an API error.
        String message = exception.getMessage();

        if (message == null || message.isBlank()) {
            message = "An unexpected error occurred";
        }

        return new UserComputeResponse(
                exception.getClass().getSimpleName() + ": " + message);
    }
}

}
