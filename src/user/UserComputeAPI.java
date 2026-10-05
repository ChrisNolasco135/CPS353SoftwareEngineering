package user;

import project.annotations.NetworkAPI;

@NetworkAPI
public interface UserComputeAPI {

    UserComputeResponse compute(UserComputeRequest request);
}