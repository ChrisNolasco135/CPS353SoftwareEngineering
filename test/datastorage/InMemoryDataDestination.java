package datastorage;

import java.util.List;

import user.DataDestination;

public class InMemoryDataDestination implements DataDestination {

    private final List<String> output;

    public InMemoryDataDestination(List<String> output) {
        this.output = output;
    }

    public List<String> getOutput() {
        return output;
    }

    @Override
    public String getIdentifier() {
        return "in-memory-output";
    }
}