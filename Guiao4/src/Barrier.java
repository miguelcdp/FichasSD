import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {
    private int n;
    private int c = 0;
    Lock l = new ReentrantLock();
    Condition cond = l.newCondition();

    Barrier (int n) {
        this.n = n;
    }

    void await() throws InterruptedException {
        l.lock();
        try {
            c++;
            if (c<n)
                while (c < n)
                    cond.await();
            else
                cond.signalAll();
        } finally {
            l.unlock();
        }
    }
}
