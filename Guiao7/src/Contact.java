import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Contact {
    private String name;
    private int age;
    private long phoneNumber;
    private String company;     // Pode ser null
    private List<String> emails;

    public Contact (String name, int age, long phone_number, String company, List<String> emails) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phone_number;
        this.company = company;
        this.emails = new ArrayList<>(emails);
    }

    public String toString () {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name).append(";");
        builder.append(this.age).append(";");
        builder.append(this.phoneNumber).append(";");
        builder.append(this.company).append(";");
        builder.append("{");
        for (String s : this.emails) {
            builder.append(s).append(";");
        }
        builder.append("}");
        return builder.toString();
    }

    public void serialize (DataOutputStream out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.age);
        out.writeLong(this.phoneNumber);

        if (this.company == null)
            out.writeBoolean(false);
        else {
            out.writeBoolean(true);
            out.writeUTF(this.company);
        }

        out.writeInt(this.emails.size());
        for (String e : emails)
            out.writeUTF(e);

        out.flush();
    }

    public static Contact deserialize (DataInputStream in) throws IOException {
        String name = in.readUTF();
        int age = in.readInt();
        long phoneNumber = in.readLong();

        String company = null;
        if (in.readBoolean())
            company = in.readUTF();

        List<String> emails = new ArrayList<>();
        int number_emails = in.readInt();
        for (int i=0; i<number_emails; i++)
            emails.add(in.readUTF());

        return new Contact(name,age,phoneNumber,company,emails);
    }
}


class ContactList {
    private List<Contact> contacts;
    private ReentrantLock l;

    public ContactList() {
        contacts = new ArrayList<>();
        l = new ReentrantLock();

        contacts.add(new Contact("John", 20, 253123321, null, new ArrayList<>(Arrays.asList("john@mail.com"))));
        contacts.add(new Contact("Alice", 30, 253987654, "CompanyInc.", new ArrayList<>(Arrays.asList("alice.personal@mail.com", "alice.business@mail.com"))));
        contacts.add(new Contact("Bob", 40, 253123456, "Comp.Ld", new ArrayList<>(Arrays.asList("bob@mail.com", "bob.work@mail.com"))));
    }

    public void addContact (DataInputStream in) throws IOException {
        Contact contact = Contact.deserialize(in);
        l.lock();
        try {
            contacts.add(contact);
        } finally {
            l.unlock();
        }
    }

    public Collection<Contact> printContacts () {
        l.lock();
        try {
            return new HashSet<>(this.contacts);
        } finally {
            l.unlock();
        }
    }

    public void getContacts (DataOutputStream out) throws IOException {
        l.lock();
        try{
            out.writeInt(contacts.size());
            out.flush();
            for (Contact c : contacts)
                c.serialize(out);
        } finally {
            l.unlock();
        }
    }
}
