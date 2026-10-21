
package datastorage;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import compute.ComputationResult;
import user.DataDestination;
import user.DataSource;

public class TestDataStorageAPI {

    @TempDir
    Path tempDir;

    @Test
    public void testRead() throws Exception {
        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        Path inputFile = tempDir.resolve("input.txt");
        Files.writeString(inputFile, "59");

        DataSource source = mock(DataSource.class);
        when(source.getIdentifier())
                .thenReturn(inputFile.toString());

        StorageRequest request = mock(StorageRequest.class);
        when(request.getSource()).thenReturn(source);

        IntegerData result = storageAPI.read(request);

        assertNotNull(result);
        assertEquals(
                BigInteger.valueOf(59),
                result.getValues().getValue()
        );
    }

    @Test
    public void testWrite() throws Exception {
        StorageComputeAPI storageAPI =
                new StorageComputeAPIImplementation();

        Path outputFile = tempDir.resolve("output.txt");

        DataDestination destination =
                mock(DataDestination.class);
        when(destination.getIdentifier())
                .thenReturn(outputFile.toString());

        StorageRequest request = mock(StorageRequest.class);
        when(request.getDestination()).thenReturn(destination);

        ComputationResult result = mock(ComputationResult.class);
        when(result.isPrime()).thenReturn(true);

        storageAPI.write(request, result);

        assertEquals("true", Files.readString(outputFile));
    }
}
