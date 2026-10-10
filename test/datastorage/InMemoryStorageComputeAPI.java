package datastorage;

import java.math.BigInteger;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import compute.ComputationResult;
import user.DataSource;

public class InMemoryStorageComputeAPI implements StorageComputeAPI {

    private final Map<DataSource, Integer> readPositions =
            new IdentityHashMap<>();

    @Override
    public IntegerData read(StorageRequest request) {
        InMemoryDataSource source =
                (InMemoryDataSource) request.getSource();

        List<Integer> input = source.getInput();

        int index = readPositions.getOrDefault(source, 0);

        // No more input numbers
        if (index >= input.size()) {
            return null;
        }

        readPositions.put(source, index + 1);

        BigInteger number = BigInteger.valueOf(input.get(index));

        return new IntegerData() {
            @Override
            public DataValue getValues() {
                return new DataValue() {
                    @Override
                    public BigInteger getValue() {
                        return number;
                    }
                };
            }
        };
    }

    @Override
    public void write(StorageRequest request, ComputationResult result) {
        InMemoryDataDestination destination =
                (InMemoryDataDestination) request.getDestination();

        destination.getOutput().add(
                Boolean.toString(result.isPrime()));
    }
}