package compute;

import java.math.BigInteger;

import datastorage.DataValue;
import datastorage.IntegerData;

public class ComputationAPIImplementation implements ComputationAPI {

    public ComputationAPIImplementation() {
    }

    @Override
    public ComputationResult compute(ComputationRequest request) {
        if (request == null || request.getData() == null) {
            throw new IllegalArgumentException(
                    "Request and data cannot be null");
        }

        IntegerData data = request.getData();
        DataValue value = data.getValues();

        if (value == null || value.getValue() == null) {
            throw new IllegalArgumentException(
                    "Input number cannot be null");
        }

        BigInteger number = value.getValue();

        boolean prime = number.compareTo(BigInteger.TWO) >= 0
                && number.isProbablePrime(100);

        return new ComputationResult() {
            @Override
            public boolean isPrime() {
                return prime;
            }
        };
    }
}