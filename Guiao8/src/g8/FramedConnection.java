package g8;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FramedConnection implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Lock wlock = new ReentrantLock();
    private final Lock rlock = new ReentrantLock();

    public FramedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
    }

    public void send(byte[] data) throws IOException {
        try {
            wlock.lock();
            out.writeInt(data.length);
            out.write(data);
            out.flush();
        } finally {
            wlock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        byte[] data;
        try {
            rlock.lock();
            data = new byte[in.readInt()];
            in.readFully(data);
        } finally {
            rlock.lock();
        }
        return data;
    }

    public void close() throws IOException {
        this.socket.close();
    }

}
