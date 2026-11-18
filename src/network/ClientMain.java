package network;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import io.grpc.StatusRuntimeException;
import network.grpc.ComputeResponse;

public final class ClientMain {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 50051;

    private ClientMain() {
    }

    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("================================");
            System.out.println("       PRIME COMPUTE ENGINE");
            System.out.println("================================");

            List<String> numbers = getInputNumbers(scanner);

            if (numbers == null) {
                System.out.println("Input cancelled.");
                return;
            }

            Path outputPath = getOutputPath(scanner);

            if (outputPath == null) {
                System.out.println("Output cancelled.");
                return;
            }

            String delimiter = getDelimiter(scanner);

            System.out.println();
            System.out.println("Submitting computation request...");
            System.out.println("Number of integers: " + numbers.size());

            try (GrpcClient client =
                    new GrpcClient(SERVER_HOST, SERVER_PORT)) {

                ComputeResponse response = client.compute(numbers);

                if (!response.getSuccessful()) {
                    System.out.println();
                    System.out.println("COMPUTATION FAILED");

                    System.out.println(
                            response.getErrorMessage());
                    return;
                }

                List<Boolean> results = response.getResultsList();

                if (results.size() != numbers.size()) {
                    System.out.println(
                            "COMPUTATION FAILED: Result count does not "
                                    + "match input count.");
                    return;
                }

                writeResults(outputPath, results, delimiter);

                System.out.println();
                System.out.println("================================");
                System.out.println("     COMPUTATION COMPLETE");
                System.out.println("================================");
                System.out.println("Status: SUCCESS");
                System.out.println("Results saved to: " + outputPath);
                System.out.println("Numbers processed: " + results.size());

                displayResults(numbers, results);
            }

        } catch (StatusRuntimeException exception) {

            System.out.println();
            System.out.println("COMPUTATION FAILED");
            System.out.println(
                    "Unable to communicate with the compute server.");
            System.out.println(exception.getStatus().getDescription());

        } catch (IOException | IllegalArgumentException exception) {

            System.out.println();
            System.out.println("ERROR: " + exception.getMessage());
        }
    }

    private static List<String> getInputNumbers(Scanner scanner)
            throws IOException {

        System.out.println();
        System.out.println("Select input method:");
        System.out.println("1. Upload an input file");
        System.out.println("2. Enter integers manually");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("1")) {

            System.out.print("Enter input file path: ");

            String fileName = scanner.nextLine().trim();

            if (fileName.isBlank()) {
                throw new IllegalArgumentException(
                        "Input file path cannot be blank");
            }

            Path inputPath = Path.of(fileName);

            if (!Files.isRegularFile(inputPath)
                    || !Files.isReadable(inputPath)) {
                throw new IllegalArgumentException(
                        "Input file does not exist or cannot be read");
            }

            String contents = Files.readString(inputPath);

            return parseNumbers(contents);

        } else if (choice.equals("2")) {

            System.out.println(
                    "Enter integers separated by commas or spaces:");

            String input = scanner.nextLine();

            return parseNumbers(input);

        } else {

            throw new IllegalArgumentException(
                    "Invalid input selection");
        }
    }

    private static List<String> parseNumbers(String input) {

        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(
                    "No integers were provided");
        }

        String[] values = input.trim().split("[,\\s]+");

        List<String> numbers = new ArrayList<>();

        for (String value : values) {

            try {
                BigInteger number = new BigInteger(value);
                numbers.add(number.toString());

            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid integer: " + value);
            }
        }

        if (numbers.isEmpty()) {
            throw new IllegalArgumentException(
                    "No integers were provided");
        }

        return numbers;
    }

    private static Path getOutputPath(Scanner scanner) {

        System.out.println();
        System.out.print("Enter output file path: ");

        String fileName = scanner.nextLine().trim();

        if (fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Output file path cannot be blank");
        }

        return Path.of(fileName).toAbsolutePath().normalize();
    }

    private static String getDelimiter(Scanner scanner) {

        System.out.println();
        System.out.println("Enter output delimiter.");
        System.out.println("Press Enter to use the default comma.");
        System.out.print("Delimiter: ");

        String delimiter = scanner.nextLine();

        if (delimiter.isEmpty()) {
            return ",";
        }

        return delimiter;
    }

    private static void writeResults(
            Path outputPath,
            List<Boolean> results,
            String delimiter) throws IOException {

        Path parent = outputPath.getParent();

        if (parent != null && !Files.isDirectory(parent)) {
            throw new IllegalArgumentException(
                    "Output directory does not exist: " + parent);
        }

        String output = results.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));

        Files.writeString(
                outputPath,
                output + System.lineSeparator());
    }

    private static void displayResults(
            List<String> numbers,
            List<Boolean> results) {

        System.out.println();
        System.out.println("Results:");

        for (int i = 0; i < numbers.size(); i++) {

            System.out.println(
                    numbers.get(i) + " -> "
                            + (results.get(i) ? "PRIME" : "NOT PRIME"));
        }
    }
}
