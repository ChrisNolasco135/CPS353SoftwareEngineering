package user;

public class UserComputeResponse {

    private final boolean prime;

    public UserComputeResponse(boolean prime) {
        this.prime = prime;
    }

    public boolean isPrime() {
        return prime;
    }
}
