
package user;

import java.math.BigInteger;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import compute.ComputationAPI;
import compute.ComputationRequest;
import compute.ComputationResult;
import compute.JobHandler;
import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;

public class TestUserComputeAPI {

    @Test
    public void testCompute() {

        // Mock the APIs that JobHandler depends on.
        StorageComputeAPI storageAPI =
                mock(StorageComputeAPI.class);

        ComputationAPI computationAPI =
                mock(ComputationAPI.class);

        // Provide a valid source and destination.
        DataSource source = mock(DataSource.class);
        DataDestination destination =
                mock(DataDestination.class);

        UserComputeRequest request =
                mock(UserComputeRequest.class);

        when(request.getSource()).thenReturn(source);
        when(request.getDestination()).thenReturn(destination);

        // Configure the storage API to return input data.
        IntegerData integerData = mock(IntegerData.class);
        DataValue dataValue = mock(DataValue.class);

        when(storageAPI.read(any(StorageRequest.class)))
                .thenReturn(integerData);

        when(integerData.getValues()).thenReturn(dataValue);

        when(integerData.getAllValues())
                .thenReturn(Collections.singletonList(dataValue));

        when(dataValue.getValue())
                .thenReturn(BigInteger.valueOf(59));

        // Configure the computation API to return true.
        ComputationResult computationResult =
                mock(ComputationResult.class);

        when(computationResult.isPrime()).thenReturn(true);

        when(computationAPI.compute(any(ComputationRequest.class)))
                .thenReturn(computationResult);

        // Instantiate the coordination component.
        UserComputeAPI userComputeAPI =
                new JobHandler(storageAPI, computationAPI);

        // Execute the computation.
        UserComputeResponse response =
                userComputeAPI.compute(request);

        // Verify the result and component interactions.
        assertNotNull(response);
        assertTrue(response.isPrime());

        verify(storageAPI).read(any(StorageRequest.class));

        verify(computationAPI)
                .compute(any(ComputationRequest.class));

        verify(storageAPI).write(
                any(StorageRequest.class),
                eq(computationResult));
    }
}
