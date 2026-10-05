package compute;

import datastorage.DataValue;
import datastorage.IntegerData;
import project.annotations.ConceptualAPIPrototype;

public class ComputationPrototype {

    @ConceptualAPIPrototype
    public void prototype(ComputationAPI computationAPI) {
        ComputationRequest request = new ComputationRequest() {
            @Override
            public IntegerData getData() {
                return new IntegerData() {
                    @Override
                    public DataValue getValues() {
                        return null;
                    }
                };
            }
        };

        ComputationResult result = computationAPI.compute(request);
        System.out.println("Computation result: " + result);
    }
}