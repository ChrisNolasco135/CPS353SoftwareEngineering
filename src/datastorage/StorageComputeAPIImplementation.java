package datastorage;

import compute.ComputationResult;

public class StorageComputeAPIImplementation implements StorageComputeAPI {

    @Override
    public IntegerData read(StorageRequest request) {
        return null;
    }

    @Override
    public void write(StorageRequest request, ComputationResult result) {
        // Do nothing for now
    }
}