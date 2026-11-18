package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserComputeResponse {

    private final List<Boolean> results;
    private final boolean successful;
    private final String errorMessage;

    // Successful computation with multiple results.
    public UserComputeResponse(List<Boolean> results) {
        this.results = Collections.unmodifiableList(
                new ArrayList<>(results));
        this.successful = true;
        this.errorMessage = null;
    }

    // Successful computation with one result.
    public UserComputeResponse(boolean prime) {
        this.results = Collections.singletonList(prime);
        this.successful = true;
        this.errorMessage = null;
    }

    // Failed computation.
    public UserComputeResponse(String errorMessage) {
        this.results = Collections.emptyList();
        this.successful = false;
        this.errorMessage = errorMessage;
    }

    public boolean isPrime() {
        return !results.isEmpty()
                && results.get(results.size() - 1);
    }

    public List<Boolean> getResults() {
        return results;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}