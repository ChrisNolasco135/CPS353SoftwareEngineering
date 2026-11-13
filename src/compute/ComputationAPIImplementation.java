
package compute;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

import datastorage.DataValue;
import datastorage.IntegerData;
import datastorage.StorageComputeAPI;
import datastorage.StorageRequest;
import user.DataDestination;
import user.DataSource;
import user.UserComputeAPI;
import user.UserComputeRequest;
import user.UserComputeResponse;

public class ComputationAPIImplementation
        implements ComputationAPI, UserComputeAPI {

    private final StorageComputeAPI storageAPI;

    public ComputationAPIImplementation(
            StorageComputeAPI storageAPI) {

        this.storageAPI = Objects.requireNonNull(
                storageAPI, "Storage API cannot be null");
    }

    // Conceptual API: Process one integer.
    @Override
    public ComputationResult compute(ComputationRequest request) {

        Objects.requireNonNull(
                request, "Computation request cannot be null");

        IntegerData data = Objects.requireNonNull(
                request.getData(), "Input data cannot be null");

        DataValue dataValue = Objects.requireNonNull(
                data.getValues(), "Data value cannot be null");

        BigInteger number = Objects.requireNonNull(
                dataValue.getValue(), "Integer value cannot be null");

        boolean prime = isPrime(number);

        return new ComputationResult() {
            @Override
            public boolean isPrime() {
                return prime;
            }
        };
    }

    // Network API: Read, compute, and write multiple values.
    @Override
    public UserComputeResponse compute(UserComputeRequest request) {

        try {
            Objects.requireNonNull(
                    request, "Request cannot be null");

            DataSource source = Objects.requireNonNull(
                    request.getSource(), "Source cannot be null");

            DataDestination destination = Objects.requireNonNull(
                    request.getDestination(), "Destination cannot be null");

            StorageRequest storageRequest = new StorageRequest() {

                @Override
                public DataDestination getDestination() {
                    return destination;
                }

                @Override
                public DataSource getSource() {
                    return source;
                }
            };

            // Read input data.
            IntegerData data = Objects.requireNonNull(
                    storageAPI.read(storageRequest),
                    "Storage API failed to read input data");

            List<DataValue> values = Objects.requireNonNull(
                    data.getAllValues(),
                    "Input values cannot be null");

            if (values.isEmpty()) {
                throw new IllegalArgumentException(
                        "Input data contains no values");
            }

            boolean lastResult = false;

            // Process every input value.
            for (DataValue value : values) {

                Objects.requireNonNull(
                        value, "Input value cannot be null");

                Objects.requireNonNull(
                        value.getValue(), "Integer cannot be null");

                IntegerData singleValueData = () -> value;

                ComputationRequest computationRequest =
                        () -> singleValueData;

                ComputationResult result = compute(computationRequest);

                // Check whether writing succeeded.
                boolean writeSuccessful =
                        storageAPI.write(storageRequest, result);

                if (!writeSuccessful) {
                    throw new IllegalStateException(
                            "Storage API failed to write output data");
                }

                lastResult = result.isPrime();
            }

            return new UserComputeResponse(lastResult);

        } catch (Exception exception) {

            String message = exception.getMessage();

            if (message == null || message.isBlank()) {
                message = "An unexpected error occurred";
            }

            return new UserComputeResponse(
                    exception.getClass().getSimpleName()
                            + ": " + message);
        }
    }

    // Prime-number calculation.
    private boolean isPrime(BigInteger number) {

        if (number.compareTo(BigInteger.TWO) < 0) {
            return false;
        }

        if (number.equals(BigInteger.TWO)) {
            return true;
        }

        if (!number.testBit(0)) {
            return false;
        }

        BigInteger divisor = BigInteger.valueOf(3);

        while (divisor.multiply(divisor).compareTo(number) <= 0) {

            if (number.remainder(divisor).equals(BigInteger.ZERO)) {
                return false;
            }

            divisor = divisor.add(BigInteger.TWO);
        }

        return true;
    }
}
