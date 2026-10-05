package user;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import compute.ComputationAPI;
import compute.JobHandler;
import datastorage.StorageComputeAPI;

public class TestUserComputeAPI {

    @Test
    public void testCompute() {

        // Mock the APIs that JobHandler depends on
        StorageComputeAPI storageAPI = mock(StorageComputeAPI.class);
        ComputationAPI computationAPI = mock(ComputationAPI.class);

        // Explicitly instantiate the implementation
        UserComputeAPI userComputeAPI =
                new JobHandler(storageAPI, computationAPI);

        // Mock the request
        UserComputeRequest request = mock(UserComputeRequest.class);

        // Call the API
        UserComputeResponse response =
                userComputeAPI.compute(request);

        // Smoke test
        assertNull(response);
    }
}