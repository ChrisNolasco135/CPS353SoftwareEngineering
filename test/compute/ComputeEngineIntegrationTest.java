package compute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import datastorage.InMemoryDataDestination;
import datastorage.InMemoryDataSource;
import datastorage.InMemoryStorageComputeAPI;
import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class ComputeEngineIntegrationTest {

    @Test
    public void testComputeEngine() {

        // Input: [1, 10, 25]
        List<Integer> input = Arrays.asList(1, 10, 25);

        // Output list that the in-memory data store will write to
        List<String> output = new ArrayList<>();

        // Test-only Process API / Data Store
        InMemoryStorageComputeAPI storageAPI =
                new InMemoryStorageComputeAPI();

        // Real Conceptual API implementation
        ComputationAPI computationAPI =
                new ComputationAPIImplementation();

        // Real Network API implementation
        UserComputeAPI userAPI =
                new JobHandler(storageAPI, computationAPI);

        // In-memory input configuration
        InMemoryDataSource dataSource =
                new InMemoryDataSource(input);

        // In-memory output configuration
        InMemoryDataDestination dataDestination =
                new InMemoryDataDestination(output);

        // User request
        UserComputeRequest request =
                new UserComputeRequest() {

                    @Override
                    public DataSource getSource() {
                        return dataSource;
                    }

                    @Override
                    public DataDestination getDestination() {
                        return dataDestination;
                    }

                    @Override
                    public String getDelimiter() {
                        return null;
                    }

                    @Override
                    public boolean useDefaultDelimiter() {
                        return true;
                    }
                };

        // Run the compute engine
        UserComputeResponse response =
                userAPI.compute(request);

        // Eventually the compute engine should determine:
        // 1  -> not prime
        // 10 -> not prime
        // 25 -> not prime
        List<String> expectedOutput =
                Arrays.asList("false", "false", "false");

        assertEquals(expectedOutput, output);
    }
}