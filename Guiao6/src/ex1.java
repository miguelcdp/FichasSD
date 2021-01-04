import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ex1 {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            while (true) {
                Socket socket = ss.accept();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream());

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

                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
