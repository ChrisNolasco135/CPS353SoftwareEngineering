
package compute;

import java.util.Objects;

import datastorage.StorageComputeAPI;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class MultiThreadedComputationAPIImplementation
        implements UserComputeAPI {

    private final StorageComputeAPI storageAPI;
    private final ComputationAPI computationAPI;

    public MultiThreadedComputationAPIImplementation(
            StorageComputeAPI storageAPI,
            ComputationAPI computationAPI) {

        this.storageAPI = Objects.requireNonNull(
                storageAPI, "Storage API cannot be null");

        this.computationAPI = Objects.requireNonNull(
                computationAPI, "Computation API cannot be null");
    }

    @Override
    public UserComputeResponse compute(UserComputeRequest request) {

        return NetworkComputeSupport.compute(
                request,
                storageAPI,
                computationAPI,
                true);
    }
}
