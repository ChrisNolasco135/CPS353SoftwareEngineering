
package threadharness;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import compute.ComputationAPI;
import compute.ComputationAPIImplementation;
import compute.MultiThreadedComputationAPIImplementation;
import datastorage.StorageComputeAPI;
import datastorage.StorageComputeAPIImplementation;
import user.UserComputeAPI;


public class TestMultiUser {

    // Network API interface.
    private UserComputeAPI coordinator;

    @BeforeEach
	public void initializeComputeEngine() {

    // Instantiate the real Storage API.
    StorageComputeAPI storageAPI =
            new StorageComputeAPIImplementation();

    // Instantiate the Conceptual API.
    ComputationAPI computationAPI =
            new ComputationAPIImplementation(storageAPI);

    // Instantiate the multi-threaded Network API.
    coordinator =
            new MultiThreadedComputationAPIImplementation(
                    storageAPI,
                    computationAPI);
	}	

    @Test
    public void compareMultiAndSingleThreaded() throws Exception {

        int threadCount = 4;

        List<TestUser> testUsers = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            testUsers.add(new TestUser(coordinator));
        }

        // Run single threaded.
        String singleThreadFilePrefix =
                "testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp";

        for (int i = 0; i < threadCount; i++) {

            File singleThreadedOut =
                    new File(singleThreadFilePrefix + i);

            singleThreadedOut.deleteOnExit();

            testUsers.get(i).run(
                    singleThreadedOut.getCanonicalPath());
        }

        // Run multi threaded.
        ExecutorService threadPool =
                Executors.newCachedThreadPool();

        List<Future<?>> results = new ArrayList<>();

        String multiThreadFilePrefix =
                "testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp";

        for (int i = 0; i < threadCount; i++) {

            File multiThreadedOut =
                    new File(multiThreadFilePrefix + i);

            multiThreadedOut.deleteOnExit();

            String multiThreadOutputPath =
                    multiThreadedOut.getCanonicalPath();

            TestUser testUser = testUsers.get(i);

            results.add(
                    threadPool.submit(
                            () -> testUser.run(multiThreadOutputPath)));
        }

        results.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        threadPool.shutdown();

        // Compare single-threaded and multi-threaded results.
        List<String> singleThreaded =
                loadAllOutput(singleThreadFilePrefix, threadCount);

        List<String> multiThreaded =
                loadAllOutput(multiThreadFilePrefix, threadCount);

        Assertions.assertEquals(singleThreaded, multiThreaded);
    }

    private List<String> loadAllOutput(
            String prefix, int threadCount) throws IOException {

        List<String> result = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {

            File multiThreadedOut =
                    new File(prefix + i);

            result.addAll(
                    Files.readAllLines(multiThreadedOut.toPath()));
        }

        return result;
    }
}
