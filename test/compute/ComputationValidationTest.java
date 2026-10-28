
package compute;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class ComputationValidationTest {

    @Test
    public void testNullRequestIsRejected() {
        ComputationAPIImplementation computation =
                new ComputationAPIImplementation();

        assertThrows(
                NullPointerException.class,
                () -> computation.compute(null));
    }
}
