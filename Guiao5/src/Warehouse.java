import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Warehouse {
  private Map<String, Product> m =  new HashMap<>();
  private Lock l = new ReentrantLock();

  private class Product {
    int q = 0;
    Condition c = l.newCondition();
  }

  private Product get(String s) {
    Product p = m.get(s);
    if (p != null) return p;
    p = new Product();
    m.put(s, p);
    return p;
  }

  public void supply(String s, int q) {
    l.lock();
    try {
      Product p = get(s);
      p.q += q;
      p.c.signalAll();
    } finally {
      l.unlock();
    }
  }

  public void consume(String[] a) throws InterruptedException {
    l.lock();
    try {
      for (String s : a) {
        Product p = get(s);
        while (p.q == 0)
          p.c.await();
        p.q--;
      }
    } finally {
      l.unlock();
    }
  }

}
