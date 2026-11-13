
package datastorage;

import java.math.BigInteger;
import java.util.List;

import compute.ComputationResult;

public class InMemoryStorageComputeAPI implements StorageComputeAPI {

    @Override
    public IntegerData read(StorageRequest request) {

        try {
            // Validate the request.
            if (request == null) {
                return null;
            }

            if (!(request.getSource() instanceof InMemoryDataSource source)) {
                return null;
            }

            List<Integer> input = source.getInput();

            if (input == null || input.isEmpty()) {
                return null;
            }

            // Return the input data.
            return new IntegerData() {

                @Override
                public DataValue getValues() {
                    return createDataValue(input.get(0));
                }

                @Override
                public List<DataValue> getAllValues() {
                    return input.stream()
                            .map(InMemoryStorageComputeAPI.this::createDataValue)
                            .toList();
                }
            };

        } catch (Exception exception) {

            // Process boundary: return failure sentinel.
            return null;
        }
    }

    public List<Integer> readAll(StorageRequest request) {

        try {
            if (request == null) {
                return null;
            }

            if (!(request.getSource() instanceof InMemoryDataSource source)) {
                return null;
            }

            return source.getInput();

        } catch (Exception exception) {

            return null;
        }
    }

    private DataValue createDataValue(int value) {

        BigInteger number = BigInteger.valueOf(value);

        return new DataValue() {
            @Override
            public BigInteger getValue() {
                return number;
            }
        };
    }

    @Override
    public boolean write(
            StorageRequest request,
            ComputationResult result) {

        try {
            // Validate the request.
            if (request == null || result == null) {
                return false;
            }

            if (!(request.getDestination()
                    instanceof InMemoryDataDestination destination)) {
                return false;
            }

            if (destination.getOutput() == null) {
                return false;
            }

            // Store the computation result.
            destination.getOutput().add(
                    Boolean.toString(result.isPrime()));

            return true;

        } catch (Exception exception) {

            // Process boundary: return failure status.
            return false;
        }
    }
}
