import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Bank {

  private static class Account {

    Lock l = new ReentrantLock();

    private int balance;

    Account(int balance) {
      this.balance = balance;
    }

    int balance() {
      /*l.lock();
      try {*/
        return balance;
      /*} finally {
        l.unlock();
      }*/
    }

    boolean deposit(int value) {
      /*l.lock();
      try {*/
        balance += value;
        return true;
      /*} finally {
        l.unlock();
      }*/
    }

    boolean withdraw(int value) {
      /*l.lock();
      try {*/
        if (value > balance)
          return false;
        balance -= value;
        return true;
      /*} finally {
        l.unlock();
      }*/
    }
  }

  // Bank slots and vector of accounts
  private int slots;
  private Account[] av;

  //Lock l = new ReentrantLock();

  public Bank(int n)
  {
    slots=n;
    av=new Account[slots];
    for (int i=0; i<slots; i++) av[i]=new Account(0);
  }

  // Account balance
  /*public int balance(int id) {
    l.lock();
    try {
      if (id < 0 || id >= slots)
        return 0;
      return av[id].balance();
    } finally {
      l.unlock();
    }
  }*/

  // Account balance
  public int balance(int id) {
    if (id < 0 || id >= slots)
      return 0;
    av[id].l.lock();
    try {
      return av[id].balance();
    } finally {
      av[id].l.unlock();
    }
  }

  // Deposit
  /*boolean deposit(int id, int value) {
    l.lock();
    try {
      if (id < 0 || id >= slots)
        return false;
      return av[id].deposit(value);
    } finally {
      l.unlock();
    }
  }*/

  // Deposit
  boolean deposit(int id, int value) {
    if (id < 0 || id >= slots)
      return false;
    av[id].l.lock();
    try {
      return av[id].deposit(value);
    } finally {
      av[id].l.unlock();
    }
  }

  // Withdraw; fails if no such account or insufficient balance
  /*public boolean withdraw(int id, int value) {
    l.lock();
    try {
      if (id < 0 || id >= slots)
        return false;
      return av[id].withdraw(value);
    } finally {
      l.unlock();
    }
  }*/

  // Withdraw; fails if no such account or insufficient balance
  public boolean withdraw(int id, int value) {
    if (id < 0 || id >= slots)
      return false;
    av[id].l.lock();
    try {
      return av[id].withdraw(value);
    } finally {
      av[id].l.unlock();
    }
  }

  /*public boolean transfer(int from, int to, int value) {
    l.lock();
    try {
      if(withdraw(from,value)) {
        deposit(to,value);
        return true;
      }
      return false;
    } finally {
      l.unlock();
    }
  }*/

  public boolean transfer(int from, int to, int value) {
    if(from<to) {
      av[from].l.lock();
      av[to].l.lock();
    } else {
      av[to].l.lock();
      av[from].l.lock();
    }
    try {
      if (withdraw(from, value)) {
        deposit(to, value);
        return true;
      }
    } finally {
      av[from].l.unlock();
      av[to].l.unlock();
    }
    return false;
  }

  /*int totalBalance() {
    l.lock();
    try {
      int total = 0;
      for (int i=0; i<slots; i++)
        total += balance(i);
      return total;
    } finally {
      l.unlock();
    }
  }*/

  int totalBalance() {
    int total = 0;
    for (int i=0; i<slots; i++)
      total += balance(i);
    return total;
  }
}
