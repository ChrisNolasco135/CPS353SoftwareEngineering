package compute;

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

        this.storageAPI = storageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public UserComputeResponse compute(UserComputeRequest request) {

        // Validator
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getSource() == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }

        if (request.getDestination() == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }

        // Create StorageRequest
        StorageRequest storageRequest = new StorageRequest() {

            @Override
            public DataDestination getDestination() {
                return request.getDestination();
            }

            @Override
            public DataSource getSource() {
                return request.getSource();
            }
        };

        // Reader and computation loop
        ComputationResult lastResult = null;

        while (true) {
            IntegerData data = storageAPI.read(storageRequest);

            // End of input
            if (data == null) {
                break;
            }

            ComputationRequest computationRequest =
                    new ComputationRequest() {
                        @Override
                        public IntegerData getData() {
                            return data;
                        }
                    };

            ComputationResult result =
                    computationAPI.compute(computationRequest);

            if (result == null) {
                throw new IllegalStateException(
                        "Computation returned a null result");
            }

            storageAPI.write(storageRequest, result);
            lastResult = result;
        }

        if (lastResult == null) {
            throw new IllegalArgumentException("No input data was provided");
        }

        return new UserComputeResponse(lastResult.isPrime());

    }
}