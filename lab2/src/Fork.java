public class Fork {
    private int index;
    public MyLock lock;

    public Fork(int index){
        this.index = index;
        this.lock = new MyLock();
    }

    public int getIndex() {
        return index;
    }

    public MyLock getLock() {
        return lock;
    }

    public void pickUp(int id) throws InterruptedException {
        lock.lock();
        System.out.println("Philosopher " + id + " " + System.nanoTime() + ": Picked up fork " + index);
    }
    public void putDown(int id){
        lock.unLock();
        System.out.println("Philosopher " + id + " " + System.nanoTime() + ": Put down  fork " + index);
    }

}
