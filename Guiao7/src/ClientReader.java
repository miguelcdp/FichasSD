import java.io.*;
import java.net.Socket;

public class ClientReader {
    public static void main (String[] args) throws IOException {
        Socket socket = new Socket("localhost", 34567);

        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
        socket.shutdownOutput();

        int elements = in.readInt();
        for (int i=0; i<elements; i++) {
            Contact contacto = Contact.deserialize(in);
            out.write(i + ": ");
            out.write(contacto.toString());
            out.flush();
        }

        socket.shutdownInput();
        socket.close();
    }
}
