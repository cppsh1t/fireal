package test.concurrentTest;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import fireal.core.BaseContainer;

public class MainTest {

    @Test
    public void test() throws InterruptedException {
        var container = new BaseContainer(TestConfig.class);
        var list = new CopyOnWriteArrayList<Foo>();
        Runnable action = () -> {
            Foo foo = container.getBean(Foo.class);
            list.add(foo);
        };

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(action);
        }

        for (var thread : threads) {
            thread.start();
            thread.join();
        }

        for(int i = 0; i < threads.length; i++) {
            if (i == threads.length - 1) return;

            Assertions.assertEquals(list.get(i), list.get(i + 1));
        }
    }

}
