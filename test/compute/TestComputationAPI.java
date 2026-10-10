package compute;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import datastorage.DataValue;
import datastorage.IntegerData;

public class TestComputationAPI {

    @Test
    public void testCompute() {
        ComputationAPI computationAPI =
                new ComputationAPIImplementation();

        DataValue value = mock(DataValue.class);
        when(value.getValue()).thenReturn(BigInteger.valueOf(7));

        IntegerData data = mock(IntegerData.class);
        when(data.getValues()).thenReturn(value);

        ComputationRequest request = mock(ComputationRequest.class);
        when(request.getData()).thenReturn(data);

        ComputationResult result = computationAPI.compute(request);

        assertNotNull(result);
        org.junit.jupiter.api.Assertions.assertTrue(result.isPrime());
    }
}