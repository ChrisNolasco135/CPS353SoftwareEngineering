package datastorage;

import java.util.List;

import compute.ComputationResult;

public class InMemoryStorageComputeAPI implements StorageComputeAPI {

    @Override
    public IntegerData read(StorageRequest request) {

        InMemoryDataSource source =
                (InMemoryDataSource) request.getSource();

        List<Integer> input = source.getInput();

        return new IntegerData() {

            @Override
            public DataValue getValues() {
                return new DataValue() {

                    @Override
                    public java.math.BigInteger getValue() {
                        return java.math.BigInteger.valueOf(input.get(0));
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
                Boolean.toString(result.isPrime())
        );
    }
}