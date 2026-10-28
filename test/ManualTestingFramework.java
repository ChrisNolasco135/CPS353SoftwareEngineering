
import compute.ComputationAPI;
import compute.ComputationAPIImplementation;
import compute.JobHandler;
import datastorage.StorageComputeAPI;
import datastorage.StorageComputeAPIImplementation;
import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class ManualTestingFramework {

    public static final String INPUT = "manualTestInput.txt";
    public static final String OUTPUT = "manualTestOutput.txt";

    public static void main(String[] args) {

        // TODO 1: Instantiate real implementations of all three APIs.
        ComputationAPI computationAPI =
                new ComputationAPIImplementation();

        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        UserComputeAPI userAPI =
                new JobHandler(storageAPI, computationAPI);

        // TODO 2: Configure the input file, output file, and delimiter.
        UserComputeRequest request = new UserComputeRequest() {

            @Override
            public DataSource getSource() {
                return () -> INPUT;
            }

            @Override
            public DataDestination getDestination() {
                return () -> OUTPUT;
            }

            @Override
            public String getDelimiter() {
                return ",";
            }

            @Override
            public boolean useDefaultDelimiter() {
                return false;
            }
        };

        
        // Execute the computation.
        UserComputeResponse response = userAPI.compute(request);

        if (response.isSuccessful()) {
            System.out.println(
                    "Prime result: " + response.isPrime());
            System.out.println(
                    "Output written to: " + OUTPUT);
        } else {
            System.out.println(
                    "Computation failed: " + response.getErrorMessage());
        }
    }
}
