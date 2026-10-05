package compute;

import datastorage.IntegerData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestComputationAPI {

    @Test
public void testCompute() {

    ComputationAPI computationAPI =
            new ComputationAPIImplementation();

    ComputationRequest request =
            mock(ComputationRequest.class);

    ComputationResult result =
            computationAPI.compute(request);

    assertNull(result);
}
}