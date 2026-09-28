package user;

public interface UserComputeRequest {

    DataSource getSource();

    DataDestination getDestination();

    String getDelimiter();

    boolean useDefaultDelimiter();
}
