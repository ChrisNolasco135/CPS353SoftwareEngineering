
package compute;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import datastorage.InMemoryStorageComputeAPI;

public class ComputationValidationTest {

    @Test
    public void testNullRequestIsRejected() {

        ComputationAPIImplementation computation =
                new ComputationAPIImplementation(
                        new InMemoryStorageComputeAPI());

        assertThrows(
                NullPointerException.class,
                () -> computation.compute((ComputationRequest) null));
    }
}
