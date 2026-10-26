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

boolean lastResult = false;

for (datastorage.DataValue value : data.getAllValues()) {
    IntegerData singleValueData = () -> value;

    ComputationRequest computationRequest =
            () -> singleValueData;

    ComputationResult result =
            computationAPI.compute(computationRequest);

    storageAPI.write(storageRequest, result);
    lastResult = result.isPrime();
}

        return new UserComputeResponse(lastResult);
    }  
}