package network;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import network.grpc.ComputeRequest;
import network.grpc.ComputeResponse;
import user.UserComputeAPI;
import user.UserComputeResponse;

public class UserComputeGrpcServiceTest {

        @Test
        public void computeForwardsRequestAndReturnsApiResponse() {

        AtomicReference<String> receivedInput =
                new AtomicReference<>();

        AtomicReference<String> receivedDestination =
                new AtomicReference<>();

        UserComputeAPI api = request -> {

                try {
                receivedInput.set(
                        Files.readString(
                                Path.of(request.getSource().getIdentifier())));

                } catch (IOException exception) {
                throw new UncheckedIOException(exception);
                }

                receivedDestination.set(
                        request.getDestination().getIdentifier());

                return new UserComputeResponse(
                        List.of(false, true, false));
        };

        UserComputeGrpcService service =
                new UserComputeGrpcService(api);

        RecordingObserver observer = new RecordingObserver();

        service.compute(
                ComputeRequest.newBuilder()
                        .addAllNumbers(List.of("12", "17", "25"))
                        .build(),
                observer);

        assertTrue(observer.completed);
        assertEquals(null, observer.error);
        assertTrue(observer.response.getSuccessful());

        // Verify that the server received the actual input data.
        assertEquals("12,17,25", receivedInput.get());

        // Verify that a temporary output file was provided.
        assertTrue(receivedDestination.get().endsWith(".txt"));

        // Verify all computation results.
        assertEquals(
                List.of(false, true, false),
                observer.response.getResultsList());
        }

    @Test
    public void computeRejectsEmptyInput() {

        UserComputeAPI api =
                request -> new UserComputeResponse(true);

        UserComputeGrpcService service =
                new UserComputeGrpcService(api);

        RecordingObserver observer = new RecordingObserver();

        service.compute(
                ComputeRequest.newBuilder().build(),
                observer);

        assertFalse(observer.completed);

        assertTrue(
                observer.error instanceof StatusRuntimeException);

        assertEquals(
                Status.Code.INVALID_ARGUMENT,
                Status.fromThrowable(observer.error).getCode());
    }

    @Test
    public void computeRejectsInvalidInteger() {

        UserComputeAPI api =
                request -> new UserComputeResponse(true);

        UserComputeGrpcService service =
                new UserComputeGrpcService(api);

        RecordingObserver observer = new RecordingObserver();

        service.compute(
                ComputeRequest.newBuilder()
                        .addNumbers("not-a-number")
                        .build(),
                observer);

        assertFalse(observer.completed);

        assertEquals(
                Status.Code.INVALID_ARGUMENT,
                Status.fromThrowable(observer.error).getCode());
    }

    private static final class RecordingObserver
            implements StreamObserver<ComputeResponse> {

        private ComputeResponse response;
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(ComputeResponse value) {
            response = value;
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
        }

        @Override
        public void onCompleted() {
            completed = true;
        }
    }
}