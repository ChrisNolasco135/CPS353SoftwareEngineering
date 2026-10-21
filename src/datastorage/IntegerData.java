package datastorage;

import java.util.Collections;
import java.util.List;

public interface IntegerData {

    DataValue getValues();

    default List<DataValue> getAllValues() {
        return Collections.singletonList(getValues());
    }
}