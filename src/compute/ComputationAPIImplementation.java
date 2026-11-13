
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

    return NetworkComputeSupport.compute(
            request,
            storageAPI,
            this,
            false);
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
