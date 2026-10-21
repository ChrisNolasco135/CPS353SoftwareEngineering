
package datastorage;

import java.math.BigInteger;
import java.util.List;

import compute.ComputationResult;

public class InMemoryStorageComputeAPI implements StorageComputeAPI {

    @Override
    public IntegerData read(StorageRequest request) {
        InMemoryDataSource source =
                (InMemoryDataSource) request.getSource();

        List<Integer> input = source.getInput();

        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException(
                    "Input list cannot be null or empty");
        }

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
    }

    public List<Integer> readAll(StorageRequest request) {
        InMemoryDataSource source =
                (InMemoryDataSource) request.getSource();

        return source.getInput();
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
    public void write(
            StorageRequest request,
            ComputationResult result) {

        InMemoryDataDestination destination =
                (InMemoryDataDestination) request.getDestination();

        destination.getOutput().add(
                Boolean.toString(result.isPrime()));
    }
}
