import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

class ReaderWorker implements Runnable {
    private Socket socket;
    private ContactList contactList;

    public ReaderWorker(Socket socket, ContactList contactList) {
        this.socket = socket;
        this.contactList = contactList;
    }


    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            contactList.getContacts(out);

            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerSocketReaderWorker implements Runnable {
    private ServerSocket serverSocket;
    private ContactList contactList;

    public ServerSocketReaderWorker(int port, ContactList contactList) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.contactList = contactList;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
                Thread worker = new Thread(new ReaderWorker(socket,contactList));
                worker.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class WriterWorker implements Runnable {
    private Socket socket;
    private ContactList contactList;

    public WriterWorker (Socket socket, ContactList contactList) {
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

class ServerSocketWriterWorker implements Runnable {
    private ServerSocket serverSocket;
    private ContactList contactList;

    public ServerSocketWriterWorker (int port, ContactList contactList) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.contactList = contactList;
    }

    @Override
    public void run() {
        while (true) {
            Socket socket;

            try {
                socket = serverSocket.accept();
                Thread worker = new Thread(new WriterWorker(socket, contactList));
                worker.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ServerEx3 {
    public static void main (String[] args) throws IOException {
        ContactList contactList = new ContactList();

        Thread writer_worker = new Thread(new ServerSocketWriterWorker(12345, contactList));
        Thread reader_worker = new Thread(new ServerSocketReaderWorker(34567, contactList));

        writer_worker.start();
        reader_worker.start();
    }
}
