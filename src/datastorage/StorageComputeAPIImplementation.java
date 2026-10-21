
package datastorage;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import compute.ComputationResult;
import user.DataDestination;
import user.DataSource;

public class StorageComputeAPIImplementation
        implements StorageComputeAPI {

    private final List<String> outputResults = new ArrayList<>();

    @Override
    public IntegerData read(StorageRequest request) {
        DataSource source = request.getSource();
        Path inputPath = Path.of(source.getIdentifier());

        try {
            List<String> lines = Files.readAllLines(inputPath);
            List<BigInteger> numbers = new ArrayList<>();

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (!trimmedLine.isEmpty()) {
                    String[] values = trimmedLine.split(",");

                    for (String value : values) {
                        numbers.add(new BigInteger(value.trim()));
                    }
                }
            }

            if (numbers.isEmpty()) {
                throw new IllegalArgumentException(
                        "Input file contains no integers");
            }

            outputResults.clear();

            return new IntegerData() {
                @Override
                public DataValue getValues() {
                    return createDataValue(numbers.get(0));
                }

                @Override
                public List<DataValue> getAllValues() {
                    List<DataValue> values = new ArrayList<>();

                    for (BigInteger number : numbers) {
                        values.add(createDataValue(number));
                    }

                    return values;
                }
            };

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to read input file: " + inputPath,
                    exception);
        }
    }

    private DataValue createDataValue(BigInteger number) {
        return new DataValue() {
            @Override
            public BigInteger getValue() {
                return number;
            }
        };
    }

    @Override
    public void write(StorageRequest request, ComputationResult result) {
        DataDestination destination = request.getDestination();

        outputResults.add(Boolean.toString(result.isPrime()));

        // Write all results as a single comma-separated line.
        String output = String.join(",", outputResults);

        try {
            Files.writeString(
                    Path.of(destination.getIdentifier()),
                    output);
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to write output file",
                    exception);
        }
    }
}
