package lab1.demo;

import lab1.demo.service.RequestCounterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestCounterServiceTest {

    private RequestCounterService counterService;

    @BeforeEach
    void setUp() {
        counterService = new RequestCounterService();
    }

    @Test
    void shouldReturnZeroInitially() {
        assertEquals(0, counterService.getCount());
    }

    @Test
    void shouldIncrementCounterByOne() {
        counterService.increment();
        assertEquals(1, counterService.getCount());
    }

    @Test
    void shouldIncrementCounterMultipleTimes() {
        for (int i = 0; i < 5; i++) {
            counterService.increment();
        }
        assertEquals(5, counterService.getCount());
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        int threads = 100;
        Thread[] threadPool = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            threadPool[i] = new Thread(counterService::increment);
            threadPool[i].start();
        }

        for (Thread thread : threadPool) {
            thread.join();
        }

        assertEquals(threads, counterService.getCount());
    }
    @Test
    void increment_WhenCalledFromMultipleThreads_ThenMaintainsCorrectCount() throws InterruptedException {

        int threads = 100;
        Thread[] threadPool = new Thread[threads];


        for (int i = 0; i < threads; i++) {
            threadPool[i] = new Thread(counterService::increment);
            threadPool[i].start();
        }

        for (Thread thread : threadPool) {
            thread.join();
        }


        assertEquals(threads, counterService.getCount());
    }
}
