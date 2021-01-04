public class ex1 extends Thread {
    public static void main(String[] args) throws InterruptedException {
        int n = 10;

        Thread[] threads = new Thread[n];

        for (int i=0; i<n; i++) {
            //threads[i] = new Incrementer();
            threads[i] = new Thread(new Incrementer());
            threads[i].start();
        }

        for (int i=0; i<n; i++) {
            threads[i].join();
        }

        System.out.println("fim");
    }
}
