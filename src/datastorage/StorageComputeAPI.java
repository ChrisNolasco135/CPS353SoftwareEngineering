package datastorage;

import compute.ComputationResult;
import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {

    IntegerData read(StorageRequest request);

    void write(StorageRequest request, ComputationResult result);
}