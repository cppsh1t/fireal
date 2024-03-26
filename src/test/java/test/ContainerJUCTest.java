// package test;

// import fireal.core.BaseContainer;
// import org.junit.jupiter.api.Test;
// import test.component.Foo;
// import test.config.TestConfig;

// public class ContainerJUCTest {

//     @Test
//     public void getTest() throws InterruptedException {
//         //in lazyInit mode
//         var container = new BaseContainer(TestConfig.class);
//         Runnable action = () -> {
//             System.out.println(container.getBean(Foo.class));
//             System.out.println(Thread.currentThread().getName());
//         };

//         Thread[] threads = new Thread[10];
//         for(int i = 0; i < threads.length; i++) {
//             threads[i] = new Thread(action);
//         }

//         for(var thread : threads) {
//             thread.start();
//         }

//         Thread.sleep(1500);
//     }


// }
