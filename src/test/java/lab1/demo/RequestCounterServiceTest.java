package lab1.demo;
import lab1.demo.service.RequestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestCounterServiceTest {

    private RequestCounterService counterService;

    @BeforeEach
    void initialize() {
        counterService = new RequestCounterService();
    }

    @Test
    void getCount_whenInitialState_thenReturnsZero() {
        assertEquals(0, counterService.getCount());
    }

    @Test
    void increment_whenCalledOnce_thenIncreasesByOne() {
        counterService.increment();
        assertEquals(1, counterService.getCount());
    }

    @Test
    void increment_whenCalledMultipleTimes_thenIncreasesCorrectly() {
        for (int i = 0; i < 5; i++) {
            counterService.increment();
        }
        assertEquals(5, counterService.getCount());
    }

    @Test
    void increment_whenCalledConcurrently_thenMaintainsCorrectCount() throws InterruptedException {
        int threadCount = 100;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(counterService::increment);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(threadCount, counterService.getCount());
    }
}