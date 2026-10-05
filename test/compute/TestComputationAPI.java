package compute;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

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