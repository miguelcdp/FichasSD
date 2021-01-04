import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ex3 {
    static int N=10;
    static int I=1000;
    static int V=100;

    public static void main(String[] args) throws InterruptedException {
        Bank b = new Bank();

        Thread[] t = new Thread[N];


        for (int i=0; i<N; i++) {
            t[i] = new Ex3Thread(I,V,b);
            t[i].start();
        }

        for (int i=0; i<N; i++) {
            t[i].join();
        }

        System.out.println("Valor final: " + b.balance());

    }
}

class Ex3Thread extends Thread {
    int i;
    int v;
    Bank b;

    public Ex3Thread(int i, int v, Bank b) {
        this.i = i;
        this.v = v;
        this.b = b;
    }

    public void run() {
        for (int j=0; j<i; j++) {
            b.deposit(v);
        }
    }
}
