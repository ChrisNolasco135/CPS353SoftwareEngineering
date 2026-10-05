package user;

import project.annotations.NetworkAPIPrototype;

public class UserComputePrototype {

    @NetworkAPIPrototype
    public void prototype(UserComputeAPI userComputeAPI) {
        DataSource inputDataSource = new DataSource() {
            public String getIdentifier() {
                return "input-file.txt";
            }
        };

        DataDestination outputDataDestination = new DataDestination() {
            public String getIdentifier() {
                return "output-file.txt";
            }
        };

        UserComputeRequest request = new UserComputeRequest() {
            public DataSource getSource() {
                return inputDataSource;
            }

            public DataDestination getDestination() {
                return outputDataDestination;
            }

            public String getDelimiter() {
                return ",";
            }

            public boolean useDefaultDelimiter() {
                return true;
            }
        };
        UserComputeResponse submitted = userComputeAPI.compute(request);
        System.out.println("Job submitted: " + submitted);
    }
}