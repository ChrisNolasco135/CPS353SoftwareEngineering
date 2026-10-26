
package compute;

import java.math.BigInteger;
import datastorage.IntegerData;
import datastorage.DataValue;

public class ComputationAPIImplementation implements ComputationAPI {

    public ComputationAPIImplementation() {
    }

    @Override
    public ComputationResult compute(ComputationRequest request) {
        IntegerData data = request.getData();
        DataValue dataValue = data.getValues();
        BigInteger number = dataValue.getValue();

        boolean prime = isPrime(number);

        return new ComputationResult() {
            @Override
            public boolean isPrime() {
                return prime;
            }
        };
    }

    private boolean isPrime(BigInteger number) {
        if (number == null || number.compareTo(BigInteger.TWO) < 0) {
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
