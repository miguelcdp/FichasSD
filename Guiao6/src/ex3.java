import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ServerWorker implements Runnable {
    private Socket socket;

    public ServerWorker(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            PrintWriter out = new PrintWriter(this.socket.getOutputStream());

            String line;
            int soma = 0;
            int n = 0;
            while ((line = in.readLine()) != null) {
                try {
                    soma += Integer.parseInt(line);
                    n++;
                } catch (NumberFormatException e) {
                    // ignore invalid integers
                }

                out.println(soma);
                out.flush();
            }

            if (n<1)
                n=1;

            out.println((float) soma/n);
            out.flush();

            this.socket.shutdownOutput();
            this.socket.shutdownInput();
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class ex3 {
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(12345);
        while (true) {
            Socket socket = ss.accept();

            Thread worker = new Thread(new ServerWorker(socket));
            worker.start();
        }
    }
}
