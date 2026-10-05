package datastorage;
import user.DataDestination;
import user.DataSource;

public interface StorageRequest {

    DataDestination getDestination();

    DataSource getSource();
}