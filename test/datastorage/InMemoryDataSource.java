package datastorage;

import java.util.List;

import user.DataSource;

public class InMemoryDataSource implements DataSource {

    private final List<Integer> input;

    public InMemoryDataSource(List<Integer> input) {
        this.input = input;
    }

    public List<Integer> getInput() {
        return input;
    }

    @Override
    public String getIdentifier() {
        return "in-memory-input";
    }
}