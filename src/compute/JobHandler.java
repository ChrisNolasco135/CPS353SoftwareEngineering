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

        // Reader
        IntegerData data = storageAPI.read(storageRequest);

        if (data == null) {
            throw new IllegalArgumentException("No data was read");
        }

        // Create ComputationRequest
        ComputationRequest computationRequest =
                new ComputationRequest() {

                    @Override
                    public IntegerData getData() {
                        return data;
                    }
                };

        // Computation
        ComputationResult result =
                computationAPI.compute(computationRequest);

        // Writer
        storageAPI.write(storageRequest, result);

        // Return result to user
        return new UserComputeResponse(result.isPrime());
    }
}