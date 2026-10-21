
package user;

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
        this.storageAPI = storageAPI;
        this.computationAPI = computationAPI;
    }

    @Override
    public UserComputeResponse compute(UserComputeRequest request) {
        // Create a storage request using the user's source and destination.
        StorageRequest storageRequest = new StorageRequest() {
            @Override
            public DataSource getSource() {
                return request.getSource();
            }

            @Override
            public DataDestination getDestination() {
                return request.getDestination();
            }
        };

        // Read the input integer from the data storage component.
        IntegerData inputData = storageAPI.read(storageRequest);

        // Create a computation request containing the input data.
        ComputationRequest computationRequest = new ComputationRequest() {
            @Override
            public IntegerData getData() {
                return inputData;
            }
        };

        // Calculate whether the integer is prime.
        ComputationResult result =
                computationAPI.compute(computationRequest);

        // Write the computation result to the user's destination.
        storageAPI.write(storageRequest, result);

        // Return the result to the user.
        return new UserComputeResponse(result.isPrime());
    }
}
