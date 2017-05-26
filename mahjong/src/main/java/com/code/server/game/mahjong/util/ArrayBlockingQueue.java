package com.code.server.game.mahjong.util;

import com.byz.mj.task.SynRoomTask;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ArrayBlockingQueue<E extends SynRoomTask> extends AbstractQueue<E>
  implements BlockingQueue<E>, Serializable
{
  private static final long serialVersionUID = -817911632652898426L;
  private final E[] items;
  private int takeIndex;
  private int putIndex;
  private int count;
  private final ReentrantLock lock;
  private final Condition notEmpty;
  private final Condition notFull;

  final int inc(int i)
  {
    i++; return i == this.items.length ? 0 : i;
  }

  private void insert(E x)
  {
    this.items[this.putIndex] = x;
    this.putIndex = inc(this.putIndex);
    this.count += 1;
    this.notEmpty.signal();
  }

  @SuppressWarnings("unchecked")
  private E extract()
  {
    SynRoomTask[] items = this.items;
    SynRoomTask x = null;

    int index = this.takeIndex;

    if (this.count == items.length) {
      SynRoomTask i = items[index];
      if (!i.isSynRoomTaskProcessing()) {
        i.setSynRoomTaskProcessing(true);
        x = i;
      } else {
        inc(index);
      }
    }
    if (x == null) {
      for (; index != this.putIndex; index = inc(index)) {
        SynRoomTask i = items[index];
        if (!i.isSynRoomTaskProcessing())
        {
          i.setSynRoomTaskProcessing(true);
          x = i;
          break;
        }
      }

    }

    if (x == null) {
      return null;
    }
    switchItemPos(index, this.takeIndex, items);
    items[this.takeIndex] = null;
    this.takeIndex = inc(this.takeIndex);
    this.count -= 1;
    this.notFull.signal();
    return (E) x;
  }

  private void switchItemPos(int index, int takeIndex, SynRoomTask[] items2)
  {
    if (index == takeIndex) {
      return;
    }
    SynRoomTask x = items2[index];
    items2[index] = items2[takeIndex];
    items2[takeIndex] = x;
  }

  void removeAt(int i)
  {
    SynRoomTask[] items = this.items;

    if (i == this.takeIndex) {
      items[this.takeIndex] = null;
      this.takeIndex = inc(this.takeIndex);
    }
    else {
      while (true) {
        int nexti = inc(i);
        if (nexti != this.putIndex) {
          items[i] = items[nexti];
          i = nexti;
        } else {
          items[i] = null;
          this.putIndex = i;
          break;
        }
      }
    }
    this.count -= 1;
    this.notFull.signal();
  }

  public ArrayBlockingQueue(int capacity)
  {
    this(capacity, false);
  }

  @SuppressWarnings("unchecked")
  public ArrayBlockingQueue(int capacity, boolean fair)
  {
    if (capacity <= 0)
      throw new IllegalArgumentException();
    this.items = (E[]) ((SynRoomTask[])new SynRoomTask[capacity]);
    this.lock = new ReentrantLock(fair);
    this.notEmpty = this.lock.newCondition();
    this.notFull = this.lock.newCondition();
  }

  @SuppressWarnings("rawtypes")
  public ArrayBlockingQueue(int capacity, boolean fair, Collection<? extends E> c)
  {
    this(capacity, fair);
    if (capacity < c.size()) {
      throw new IllegalArgumentException();
    }
    for (Iterator it = c.iterator(); it.hasNext(); )
      add((SynRoomTask)it.next());
  }

  @SuppressWarnings("unchecked")
  public boolean add(SynRoomTask synRoomTask)
  {
    return super.add((E) synRoomTask);
  }

  @SuppressWarnings("unused")
  public boolean offer(E e)
  {
    if (e == null)
      throw new NullPointerException();
    ReentrantLock lock = this.lock;
    lock.lock();
    try
    {
      boolean bool;
      if (this.count == this.items.length) {
        return false;
      }
      insert(e);
      return true;
    }
    finally {
      lock.unlock();
    }
  }

  public void put(E e)
    throws InterruptedException
  {
    if (e == null)
      throw new NullPointerException();
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
      try {
        while (this.count == items.length)
          this.notFull.await();
      } catch (InterruptedException ie) {
        this.notFull.signal();
        throw ie;
      }
      insert(e);
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unused")
  public boolean offer(E e, long timeout, TimeUnit unit)
    throws InterruptedException
  {
    if (e == null)
      throw new NullPointerException();
    long nanos = unit.toNanos(timeout);
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try
    {
      while (true)
      {
        boolean bool;
        if (this.count != this.items.length) {
          insert(e);
          return true;
        }
        if (nanos <= 0L)
          return false;
        try {
          nanos = this.notFull.awaitNanos(nanos);
        } catch (InterruptedException ie) {
          this.notFull.signal();
          throw ie;
        }
      }
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public E poll() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (this.count == 0)
        return null;
      SynRoomTask x = extract();
      return (E) x;
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public E take()
    throws InterruptedException
  {
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    SynRoomTask x = null;
    try {
      try {
        while ((this.count == 0) || ((x = extract()) == null))
          this.notEmpty.await();
      } catch (InterruptedException ie) {
        this.notEmpty.signal();
        throw ie;
      }
      return (E) x;
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    long nanos = unit.toNanos(timeout);
    ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try
    {
      while (true)
      {
        SynRoomTask x;
        if (this.count != 0) {
          x = extract();
          if (x != null) {
            return (E) x;
          }
        }
        if (nanos <= 0L)
          return null;
        try {
          nanos = this.notEmpty.awaitNanos(nanos);
        } catch (InterruptedException ie) {
          this.notEmpty.signal();
          throw ie;
        }
      }
    }
    finally {
      lock.unlock();
    }
  }

  public E peek() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return this.count == 0 ? null : this.items[this.takeIndex];
    } finally {
      lock.unlock();
    }
  }

  public int size()
  {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return this.count;
    } finally {
      lock.unlock();
    }
  }

  public int remainingCapacity()
  {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return this.items.length - this.count;
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unused")
  public boolean remove(Object o)
  {
    if (o == null)
      return false;
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int i = this.takeIndex;
      int k = 0;
      while (true)
      {
        boolean bool;
        if (k++ >= this.count)
          return false;
        if (o.equals(items[i])) {
          removeAt(i);
          return true;
        }
        i = inc(i);
      }
    }
    finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unused")
public boolean contains(Object o)
  {
    if (o == null)
      return false;
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int i = this.takeIndex;
      int k = 0;
      boolean bool;
      while (k++ < this.count) {
        if (o.equals(items[i]))
          return true;
        i = inc(i);
      }
      return false;
    } finally {
      lock.unlock();
    }
  }

  public Object[] toArray()
  {
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      Object[] a = new Object[this.count];
      int k = 0;
      int i = this.takeIndex;
      while (k < this.count) {
        a[(k++)] = items[i];
        i = inc(i);
      }
      return a;
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a)
  {
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      if (a.length < this.count) {
        a = (T[])Array.newInstance(a.getClass().getComponentType(), this.count);
      }
      int k = 0;
      int i = this.takeIndex;
      while (k < this.count) {
        a[(k++)] = (T) items[i];
        i = inc(i);
      }
      if (a.length > this.count)
        a[this.count] = null;
      return a;
    } finally {
      lock.unlock();
    }
  }

  public String toString() {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return super.toString();
    } finally {
      lock.unlock();
    }
  }

  public void clear()
  {
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int i = this.takeIndex;
      int k = this.count;
      while (k-- > 0) {
        items[i] = null;
        i = inc(i);
      }
      this.count = 0;
      this.putIndex = 0;
      this.takeIndex = 0;
      this.notFull.signalAll();
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings("unchecked")
  public int drainTo(Collection<? super E> c)
  {
    if (c == null)
      throw new NullPointerException();
    if (c == this)
      throw new IllegalArgumentException();
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int i = this.takeIndex;
      int n = 0;
      int max = this.count;
      while (n < max) {
        c.add((E) items[i]);
        items[i] = null;
        i = inc(i);
        n++;
      }
      if (n > 0) {
        this.count = 0;
        this.putIndex = 0;
        this.takeIndex = 0;
        this.notFull.signalAll();
      }
      return n;
    } finally {
      lock.unlock();
    }
  }

  @SuppressWarnings({ "unused", "unchecked" })
  public int drainTo(Collection<? super E> c, int maxElements)
  {
    if (c == null)
      throw new NullPointerException();
    if (c == this)
      throw new IllegalArgumentException();
    if (maxElements <= 0)
      return 0;
    SynRoomTask[] items = this.items;
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      int i = this.takeIndex;
      int n = 0;
      int sz = this.count;
      int max = maxElements < this.count ? maxElements : this.count;
      while (n < max) {
        c.add((E) items[i]);
        items[i] = null;
        i = inc(i);
        n++;
      }
      if (n > 0) {
        this.count -= n;
        this.takeIndex = i;
        this.notFull.signalAll();
      }
      return n;
    } finally {
      lock.unlock();
    }
  }

  public Iterator<E> iterator()
  {
    ReentrantLock lock = this.lock;
    lock.lock();
    try {
      return new Itr();
    } finally {
      lock.unlock();
    }
  }

  private class Itr
    implements Iterator<E>
  {
    private int nextIndex;
    private E nextItem;
    private int lastRet;

    Itr()
    {
      this.lastRet = -1;
      if (ArrayBlockingQueue.this.count == 0) {
        this.nextIndex = -1;
      } else {
        this.nextIndex = ArrayBlockingQueue.this.takeIndex;
        this.nextItem = ArrayBlockingQueue.this.items[ArrayBlockingQueue.this.takeIndex];
      }
    }

    public boolean hasNext()
    {
      return this.nextIndex >= 0;
    }

    private void checkNext()
    {
      if (this.nextIndex == ArrayBlockingQueue.this.putIndex) {
        this.nextIndex = -1;
        this.nextItem = null;
      } else {
        this.nextItem = ArrayBlockingQueue.this.items[this.nextIndex];
        if (this.nextItem == null)
          this.nextIndex = -1;
      }
    }

    @SuppressWarnings("unchecked")
	public E next() {
      ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        if (this.nextIndex < 0)
          throw new NoSuchElementException();
        this.lastRet = this.nextIndex;
        SynRoomTask x = this.nextItem;
        this.nextIndex = ArrayBlockingQueue.this.inc(this.nextIndex);
        checkNext();
        return (E) x;
      } finally {
        lock.unlock();
      }
    }

    public void remove() {
      ReentrantLock lock = ArrayBlockingQueue.this.lock;
      lock.lock();
      try {
        int i = this.lastRet;
        if (i == -1)
          throw new IllegalStateException();
        this.lastRet = -1;

        int ti = ArrayBlockingQueue.this.takeIndex;
        ArrayBlockingQueue.this.removeAt(i);

        this.nextIndex = (i == ti ? ArrayBlockingQueue.this.takeIndex : i);
        checkNext();
      } finally {
        lock.unlock();
      }
    }
  }
}