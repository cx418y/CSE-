import java.util.concurrent.CountDownLatch;

public class Dining{
    public static void main(String[] args) throws Exception {
        Philosopher[] philosophers = new Philosopher[5];
        Fork[] forks = new Fork[philosophers.length];
        for (int i = 0; i < forks.length; i++) {
            // initialize fork object
            Fork fork = new Fork(i+1);
            forks[i] = fork;
            }
        for (int i = 0; i < philosophers.length; i++) {
            // initialize Philosopher object
            if (i < philosophers.length - 1) {
                Philosopher philosopher = new Philosopher(i + 1, forks[i], forks[i + 1]);
                philosophers[i] = philosopher;
            } else {
                Philosopher philosopher = new Philosopher(i + 1, forks[0], forks[i]);
                philosophers[i] = philosopher;
            }
        }


        Thread[] threads = new Thread[philosophers.length];
        for (int i = 0; i < philosophers.length; i++) {
            threads[i] = new Thread(philosophers[i]);
        }
        long start = System.nanoTime();
        long end = System.nanoTime();
        for (int i = 0; i < philosophers.length; i++) {
            threads[i].start();
        }

        while (end - start < 1000000){
            end = System.nanoTime();
        }
        CountDownLatch cdl = new CountDownLatch(philosophers.length);

       // for()

    }
}
