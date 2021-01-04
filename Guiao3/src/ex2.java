import java.time.Duration;
import java.time.Instant;
import java.util.Random;

public class ex2 {

    public static void main(String[] args) throws InterruptedException {
        final int N=10;
        int[] ids = new int[N];

        Bank b = new Bank();


        for (int i=0; i<N; i++) {
            ids[i] = b.createAccount(1000);
        }

        System.out.println(b.totalBalance(ids));

        Thread t1 = new Thread(new Mover(b,N));
        Thread t2 = new Thread(new Mover(b,N));

        Instant start = Instant.now();

        t1.start(); t2.start(); t1.join(); t2.join();

        Instant stop = Instant.now();


        System.out.println(b.totalBalance(ids));

        System.out.println("Demorou " + Duration.between(start,stop).toMillis() + "ms");
    }
}

class Mover implements Runnable {
    Bank b;
    int s; // Number of accounts

    public Mover(Bank b, int s) { this.b=b; this.s=s; }

    public void run() {
        final int moves=100000;
        int from, to;
        Random rand = new Random();

        for (int m=0; m<moves; m++)
        {
            from=rand.nextInt(s); // Get one
            while ((to=rand.nextInt(s))==from); // Slow way to get distinct
            b.transfer(from,to,1);
        }
    }
}