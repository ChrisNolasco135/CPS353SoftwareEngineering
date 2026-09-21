package compute;

import project.annotations.ConceptualAPI;

@ConceptualAPI
public interface ComputationAPI {

    ComputationResult compute(ComputationRequest request);
}