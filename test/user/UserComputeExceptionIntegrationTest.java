
package user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import compute.ComputationAPI;
import compute.ComputationAPIImplementation;
import compute.ComputationResult;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;

public class UserComputeExceptionIntegrationTest {

    @Test
    public void testStorageExceptionBecomesErrorResponse() {
        // Simulate a storage component failure.
        StorageComputeAPI failingStorage = new StorageComputeAPI() {
            @Override
            public datastorage.IntegerData read(StorageRequest request) {
                throw new IllegalStateException("Simulated storage failure");
            }

            @Override
            public void write(
                    StorageRequest request,
                    ComputationResult result) {
                // No write occurs in this test.
            }
        };

        ComputationAPI computation =
                new ComputationAPIImplementation();

        UserComputeAPI coordinator =
                new UserComputeAPIImplementation(
                        failingStorage, computation);

        DataSource source = new DataSource() {
            @Override
            public String getIdentifier() {
                return "input.txt";
            }
        };

        DataDestination destination = new DataDestination() {
            @Override
            public String getIdentifier() {
                return "output.txt";
            }
        };

        UserComputeRequest request = new UserComputeRequest() {
            @Override
            public DataSource getSource() {
                return source;
            }

            @Override
            public DataDestination getDestination() {
                return destination;
            }

            @Override
            public String getDelimiter() {
                return ",";
            }

            @Override
            public boolean useDefaultDelimiter() {
                return true;
            }
        };

        // The exception must not escape from the coordinator.
        UserComputeResponse response = coordinator.compute(request);

        assertNotNull(response);
        assertFalse(response.isSuccessful());
        assertFalse(response.isPrime());
        assertNotNull(response.getErrorMessage());
        assertTrue(
                response.getErrorMessage().contains(
                        "Simulated storage failure"));
    }
}
