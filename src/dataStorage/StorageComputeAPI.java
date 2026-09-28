package datastorage;

import project.annotations.ProcessAPI;

@ProcessAPI
public interface StorageComputeAPI {

    IntegerData read(StorageRequest request);

    void write(StorageRequest request, IntegerData data);
}