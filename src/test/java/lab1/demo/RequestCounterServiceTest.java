package lab1.demo;

import lab1.demo.service.RequestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestCounterServiceTest {

    private RequestCounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new RequestCounterService();
    }

    @Test
    void testIncrementAndGet_SingleThread() {
        counterService.increment();
        assertEquals(1, counterService.getCount());

        counterService.increment();
        assertEquals(2, counterService.getCount());

        assertEquals(2, counterService.getCount());
    }

    @Test
    void testIncrementAndGet_MultiThread() throws InterruptedException {
        int threadCount = 100;
        int incrementsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counterService.increment();
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * incrementsPerThread, counterService.getCount());
    }

    @Test
    void getCount_whenInitialState_thenReturnsZero() {
        assertEquals(0, counterService.getCount());
    }
}