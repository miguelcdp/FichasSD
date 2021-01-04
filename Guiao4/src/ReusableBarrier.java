import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReusableBarrier {
    private int n;
    private int c = 0;
    private int epoca = 0;
    Lock l = new ReentrantLock();
    Condition cond = l.newCondition();

    ReusableBarrier (int n) {
        this.n = n;
    }

    void await() throws InterruptedException {
        l.lock();
        try {
            int e = epoca;
            c++;
            if (c<n)
                while (epoca == e)
                    cond.await();
            else {
                cond.signalAll();
                c=0;
                epoca++;
            }
        } finally {
            l.unlock();
        }
    }
}
