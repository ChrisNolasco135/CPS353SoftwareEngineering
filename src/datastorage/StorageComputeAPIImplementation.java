
package datastorage;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import compute.ComputationResult;
import user.DataDestination;
import user.DataSource;

public class StorageComputeAPIImplementation
        implements StorageComputeAPI {

    private final List<String> outputResults = new ArrayList<>();

    @Override
    public IntegerData read(StorageRequest request) {
        Objects.requireNonNull(
                request, "Storage request cannot be null");

        DataSource source = Objects.requireNonNull(
                request.getSource(), "Data source cannot be null");

        String identifier = Objects.requireNonNull(
                source.getIdentifier(), "Input file identifier cannot be null");

        if (identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Input file identifier cannot be blank");
        }

        Path inputPath = Path.of(identifier);

        if (!Files.isRegularFile(inputPath)) {
            throw new IllegalArgumentException(
                    "Input path is not a regular file: " + inputPath);
        }

        if (!Files.isReadable(inputPath)) {
            throw new IllegalArgumentException(
                    "Input file is not readable: " + inputPath);
        }

        try {
            List<String> lines = Files.readAllLines(inputPath);
            List<BigInteger> numbers = new ArrayList<>();

            for (String line : lines) {
                String trimmedLine = line.trim();

                if (!trimmedLine.isEmpty()) {
                    String[] values = trimmedLine.split(",");

                    for (String value : values) {
                        String trimmedValue = value.trim();

                        if (trimmedValue.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "Input file contains an empty value");
                        }

                        try {
                            numbers.add(new BigInteger(trimmedValue));
                        } catch (NumberFormatException exception) {
                            throw new IllegalArgumentException(
                                    "Invalid integer in input file: "
                                            + trimmedValue,
                                    exception);
                        }
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
        Objects.requireNonNull(
                request, "Storage request cannot be null");

        DataDestination destination = Objects.requireNonNull(
                request.getDestination(),
                "Data destination cannot be null");

        String identifier = Objects.requireNonNull(
                destination.getIdentifier(),
                "Output file identifier cannot be null");

        if (identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "Output file identifier cannot be blank");
        }

        Objects.requireNonNull(
                result, "Computation result cannot be null");

        Path outputPath = Path.of(identifier);

        // Validate the destination's parent directory if one is specified.
        Path parent = outputPath.toAbsolutePath().getParent();

        if (parent != null && !Files.isDirectory(parent)) {
            throw new IllegalArgumentException(
                    "Output directory does not exist: " + parent);
        }

        outputResults.add(Boolean.toString(result.isPrime()));

        String output = String.join(",", outputResults);

        try {
            Files.writeString(outputPath, output);
        } catch (IOException exception) {
            throw new RuntimeException(
                    "Failed to write output file: " + outputPath,
                    exception);
        }
    }
}
