package user;

import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI {

    void compute(UserComputeRequest request);
}