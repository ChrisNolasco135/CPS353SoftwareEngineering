
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

        try {
            // Validate the storage request.
            if (request == null) {
                throw new NullPointerException(
                        "Storage request cannot be null");
            }

            DataSource source = request.getSource();

            if (source == null) {
                throw new NullPointerException(
                        "Data source cannot be null");
            }

            String identifier = source.getIdentifier();

            if (identifier == null) {
                throw new NullPointerException(
                        "Input file identifier cannot be null");
            }

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

            // Read the input file.
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
                            numbers.add(
                                    new BigInteger(trimmedValue));

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

        } catch (Exception exception) {

            // Process boundary: do not allow exceptions to escape.
            return null;
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
    public boolean write(
            StorageRequest request,
            ComputationResult result) {

        try {
            // Validate the storage request.
            if (request == null) {
                throw new NullPointerException(
                        "Storage request cannot be null");
            }

            DataDestination destination = request.getDestination();

            if (destination == null) {
                throw new NullPointerException(
                        "Data destination cannot be null");
            }

            String identifier = destination.getIdentifier();

            if (identifier == null) {
                throw new NullPointerException(
                        "Output file identifier cannot be null");
            }

            if (identifier.isBlank()) {
                throw new IllegalArgumentException(
                        "Output file identifier cannot be blank");
            }

            if (result == null) {
                throw new NullPointerException(
                        "Computation result cannot be null");
            }

            Path outputPath = Path.of(identifier);

            // Validate the destination directory.
            Path parent = outputPath.toAbsolutePath().getParent();

            if (parent != null && !Files.isDirectory(parent)) {
                throw new IllegalArgumentException(
                        "Output directory does not exist: " + parent);
            }

            // Prepare output without modifying stored results yet.
            List<String> newResults = new ArrayList<>(outputResults);

            newResults.add(
                    Boolean.toString(result.isPrime()));

            String output = String.join(",", newResults);

            // Write output to the destination.
            Files.writeString(outputPath, output);

            // Update stored results only after a successful write.
            outputResults.clear();
            outputResults.addAll(newResults);

            return true;

        } catch (Exception exception) {

            // Process boundary: report failure instead of throwing.
            return false;
        }
    }
}
