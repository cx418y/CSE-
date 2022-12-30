public class Philosopher implements Runnable {
    private final Fork minFork;
    private final Fork maxFork;
    private int id;

    Philosopher(int id,Fork minFork, Fork maxFork) {
        this.id = id;
        this.minFork = minFork;
        this.maxFork = maxFork;
    }

    private void doAction(String action) throws InterruptedException {
        //System.out.println(Thread.currentThread().getName() + " " + action);
        System.out.println("Philosopher " + this.id + " " + action);
        Thread.sleep(((int) (Math.random() * 100)));
    }

    @Override
    public void run() {
        try {
            while (true) {
                doAction(System.nanoTime() + ": Thinking");
                pickUpMinFork();
                pickUpMaxFork();
                doAction(System.nanoTime() + ": Eating");
                putDownMinFork();
                putDownMaxFork();

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getIndex() {
        return id;
    }

    public void pickUpMinFork() throws InterruptedException {
        this.minFork.pickUp(id);
    }

    public void pickUpMaxFork() throws InterruptedException {
        this.maxFork.pickUp(id);
    }


    public void putDownMinFork() throws InterruptedException {
        this.minFork.putDown(id);
    }
    public void putDownMaxFork() throws InterruptedException {
        this.maxFork.putDown(id);
    }




}
