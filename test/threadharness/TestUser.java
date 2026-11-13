
package threadharness;

import java.io.File;
import java.util.Objects;

import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class TestUser {

    // Network API interface.
    private final UserComputeAPI coordinator;

    public TestUser(UserComputeAPI coordinator) {
        this.coordinator = Objects.requireNonNull(coordinator);
    }

    public void run(String outputPath) {

        char delimiter = ';';

        String inputPath =
        "test" + File.separatorChar + "testInputFile.txt";
        // Configure the input source.
        DataSource source = () -> inputPath;

        // Configure the output destination.
        DataDestination destination = () -> outputPath;

        // Create the user request.
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
                return String.valueOf(delimiter);
            }

            @Override
            public boolean useDefaultDelimiter() {
                return false;
            }
        };

        // Execute the computation.
        UserComputeResponse response = coordinator.compute(request);

        // Check for computation failure.
        if (!response.isSuccessful()) {
            throw new IllegalStateException(
                    "Computation failed: "
                            + response.getErrorMessage());
        }
    }
}
