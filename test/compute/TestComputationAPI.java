
package compute;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        ComputationRequest request =
                mock(ComputationRequest.class);

        IntegerData integerData = mock(IntegerData.class);
        DataValue dataValue = mock(DataValue.class);

        when(request.getData()).thenReturn(integerData);
        when(integerData.getValues()).thenReturn(dataValue);
        when(dataValue.getValue()).thenReturn(BigInteger.valueOf(59));

        ComputationResult result =
                computationAPI.compute(request);

        assertNotNull(result);
        assertTrue(result.isPrime());
    }
    
        @Test
        public void testOneIsNotPrime() {

        ComputationAPI computationAPI =
                new ComputationAPIImplementation();

        ComputationRequest request =
                mock(ComputationRequest.class);

        IntegerData integerData = mock(IntegerData.class);
        DataValue dataValue = mock(DataValue.class);

        when(request.getData()).thenReturn(integerData);
        when(integerData.getValues()).thenReturn(dataValue);
        when(dataValue.getValue()).thenReturn(BigInteger.ONE);

        ComputationResult result =
                computationAPI.compute(request);

        assertNotNull(result);
        assertFalse(result.isPrime());
        }

}
