
package user;

public class UserComputeResponse {

    private final boolean prime;
    private final boolean successful;
    private final String errorMessage;

    // Successful computation.
    public UserComputeResponse(boolean prime) {
        // Every boolean value is valid: true means prime, false means not prime.
        this.prime = prime;
        this.successful = true;
        this.errorMessage = null;
    }

    // Failed computation.
    public UserComputeResponse(String errorMessage) {
        this.prime = false;
        this.successful = false;
        this.errorMessage = errorMessage;
    }

    public boolean isPrime() {
        return prime;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
