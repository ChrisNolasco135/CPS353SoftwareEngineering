package user;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import compute.ComputationAPI;
import compute.ComputationResult;
import compute.ComputationRequest;
import compute.JobHandler;
import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;
import datastorage.InMemoryDataSource;
import datastorage.InMemoryDataDestination;

public class TestUserComputeAPI {

    @Test
    public void testCompute() {
        StorageComputeAPI storageAPI = mock(StorageComputeAPI.class);
        ComputationAPI computationAPI = mock(ComputationAPI.class);

        IntegerData data = mock(IntegerData.class);
        DataValue value = mock(DataValue.class);

        when(storageAPI.read(
                org.mockito.ArgumentMatchers.any(StorageRequest.class)))
                .thenReturn(data);
        when(data.getValues()).thenReturn(value);
        when(value.getValue()).thenReturn(BigInteger.valueOf(7));

        ComputationResult result = mock(ComputationResult.class);
        when(result.isPrime()).thenReturn(true);
        when(computationAPI.compute(
                org.mockito.ArgumentMatchers.any(ComputationRequest.class)))
                .thenReturn(result);

        UserComputeAPI userComputeAPI =
                new JobHandler(storageAPI, computationAPI);

        InMemoryDataSource source =
                new InMemoryDataSource(Arrays.asList(7));
        InMemoryDataDestination destination =
                new InMemoryDataDestination(new ArrayList<>());

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
                return null;
            }

            @Override
            public boolean useDefaultDelimiter() {
                return true;
            }
        };

        UserComputeResponse response = userComputeAPI.compute(request);

        assertNotNull(response);
    }
}