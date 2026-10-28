
package compute;

import java.math.BigInteger;
import java.util.Objects;

import datastorage.IntegerData;
import datastorage.DataValue;

public class ComputationAPIImplementation implements ComputationAPI {

    public ComputationAPIImplementation() {
    }

    @Override
    public ComputationResult compute(ComputationRequest request) {
        // Validate the request.
        Objects.requireNonNull(
                request, "Computation request cannot be null");

        // Validate the input data.
        IntegerData data = Objects.requireNonNull(
                request.getData(), "Input data cannot be null");

        // Validate the data value.
        DataValue dataValue = Objects.requireNonNull(
                data.getValues(), "Data value cannot be null");

        // Validate the integer.
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

    private boolean isPrime(BigInteger number) {
        // Every non-null BigInteger is a valid candidate.
        // Negative numbers, zero, and one are valid inputs but are not prime.
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
