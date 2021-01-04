import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

class Register {
    private final ReentrantLock l = new ReentrantLock();
    private int soma = 0;
    private int n = 0;

    public void adiciona(int value) {
        l.lock();
        try {
            this.soma += value;
            n++;
        } finally {
            l.unlock();
        }
    }

    public double getMedia() {
        l.lock();
        try {
            if (n<1) {
                return 0;
            }

            return (double) soma/n;
        } finally {
            l.unlock();
        }
    }
}

class ServerWorkerConcorrente implements Runnable {
    private Socket socket;
    private Register register;

    public ServerWorkerConcorrente(Socket socket, Register register) {
        this.socket = socket;
        this.register = register;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter out = new PrintWriter(this.socket.getOutputStream());

            String line;
            int soma = 0;
            while ((line = in.readLine()) != null) {
                try {
                    soma += Integer.parseInt(line);
                    register.adiciona(Integer.parseInt(line));
                } catch (NumberFormatException e) {
                    // ignore invalid integers
                }

                out.println(soma);
                out.flush();
            }

            out.println(register.getMedia());
            out.flush();

            this.socket.shutdownOutput();
            this.socket.shutdownInput();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ex4 {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        Register register = new Register();

        while (true) {
            Socket socket = ss.accept();

            Thread worker = new Thread(new ServerWorkerConcorrente(socket, register));
            worker.start();
        }
    }
}
