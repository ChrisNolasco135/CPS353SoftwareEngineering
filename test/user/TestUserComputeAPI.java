
package user;

import java.math.BigInteger;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import compute.ComputationAPIImplementation;
import compute.ComputationResult;
import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;

public class TestUserComputeAPI {

    @Test
    public void testCompute() {

        // Mock the storage API.
        StorageComputeAPI storageAPI =
                mock(StorageComputeAPI.class);

        // Provide a valid source and destination.
        DataSource source = mock(DataSource.class);
        DataDestination destination =
                mock(DataDestination.class);

        UserComputeRequest request =
                mock(UserComputeRequest.class);

        when(request.getSource()).thenReturn(source);
        when(request.getDestination()).thenReturn(destination);

        // Configure storage to return input data.
        IntegerData integerData = mock(IntegerData.class);
        DataValue dataValue = mock(DataValue.class);

        when(storageAPI.read(any(StorageRequest.class)))
                .thenReturn(integerData);

        when(integerData.getValues()).thenReturn(dataValue);

        when(integerData.getAllValues())
                .thenReturn(Collections.singletonList(dataValue));

        when(dataValue.getValue())
                .thenReturn(BigInteger.valueOf(59));

        // Configure successful storage writes.
        when(storageAPI.write(
                any(StorageRequest.class),
                any(ComputationResult.class)))
                .thenReturn(true);

        // Instantiate the combined implementation.
        UserComputeAPI userComputeAPI =
                new ComputationAPIImplementation(storageAPI);

        // Execute the computation.
        UserComputeResponse response =
                userComputeAPI.compute(request);

        // Verify the result.
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertTrue(response.isPrime());

        // Verify storage interactions.
        verify(storageAPI).read(any(StorageRequest.class));

        verify(storageAPI).write(
                any(StorageRequest.class),
                any(ComputationResult.class));
    }
}
