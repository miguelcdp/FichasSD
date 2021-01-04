import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

class ServerWorker implements Runnable {
    private Socket socket;
    private ContactList contactList;

    public ServerWorker (Socket socket, ContactList contactList) {
        this.socket = socket;
        this.contactList = contactList;
    }

    @Override
    public void run() {
        try {
            socket.shutdownOutput();
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
            boolean aberto = true;

            while (aberto) {
                try {
                    contactList.addContact(in);
                } catch (IOException e) {
                    aberto = false;
                }

                Collection<Contact> contactos = contactList.printContacts();
                for (Contact c: contactos)
                    System.out.println(c.toString());
            }

            socket.shutdownInput();
            socket.close();

            System.out.println("Connection closed...");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class Server {
    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactList contactList = new ContactList();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, contactList));
            worker.start();
        }
    }
}
