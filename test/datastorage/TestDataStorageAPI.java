package datastorage;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import compute.ComputationResult;

public class TestDataStorageAPI {

    @Test
    public void testRead() {

        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        StorageRequest request = mock(StorageRequest.class);

        IntegerData result = storageAPI.read(request);

        assertNull(result);
    }

    @Test
    public void testWrite() {

        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        StorageRequest request = mock(StorageRequest.class);
        ComputationResult result = mock(ComputationResult.class);

        storageAPI.write(request, result);

        assertTrue(true);
    }
}