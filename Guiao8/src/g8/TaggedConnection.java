package g8;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final Lock wlock = new ReentrantLock();
    private final Lock rlock = new ReentrantLock();


    public static class Frame {
        public final int tag;
        public final byte[] data;

        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }
    }


    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        this.send(frame.tag,frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        try {
            wlock.lock();
            this.out.writeInt(4 + data.length);
            this.out.writeInt(tag);
            this.out.write(data);
            this.out.flush();
        } finally {
            wlock.unlock();
        }
    }

    public Frame receive() throws IOException {
        byte[] data;
        int tag;
        try {
            rlock.lock();
            data = new byte[this.in.readInt() - 4];
            tag = this.in.readInt();
            this.in.readFully(data);
        } finally {
            rlock.unlock();
        }
        return new Frame(tag,data);
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
