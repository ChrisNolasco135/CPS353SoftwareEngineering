package datastorage;

import compute.ComputationResult;
import project.annotations.ProcessAPIPrototype;
import user.DataDestination;
import user.DataSource;

public class StorageComputePrototype {

    @ProcessAPIPrototype
    public void prototype(StorageComputeAPI storageComputeAPI) {

        DataSource dataSource = new DataSource() {
            @Override
            public String getIdentifier() {
                return "inputfilepath.txt";
            }
        };

        DataDestination dataDestination = new DataDestination() {
            @Override
            public String getIdentifier() {
                return "filepath.txt";
            }
        };

        StorageRequest storageRequest = new StorageRequest() {

            @Override
            public DataSource getSource() {
                return dataSource;
            }

            @Override
            public DataDestination getDestination() {
                return dataDestination;
            }
        };

        // Read input
        IntegerData data = storageComputeAPI.read(storageRequest);

        System.out.println("Data read: " + data);

        ComputationResult result = new ComputationResult() {

            @Override
            public boolean isPrime() {
                return true;
            }
        };

        // Write computation result
        storageComputeAPI.write(storageRequest, result);

        System.out.println("Data written: " + result);
    }
}