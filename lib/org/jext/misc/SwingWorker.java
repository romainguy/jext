package org.jext.misc;

import javax.swing.SwingUtilities;

/**
 * This class comes from the SwingWorker class described at the URL below(3rd version), but has substantially changed;
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *
 */
public abstract class SwingWorker {
  private Object value;  // see getValue(), setValue()
  private Throwable exception;
  private Thread thread;
  protected HandlingRunnable notifier;

  /** 
   * Class to maintain reference to current worker thread
   * under separate synchronization control.
   */
  private static class ThreadVar {
    private Thread thread;
    ThreadVar(Thread t) { thread = t; }
    synchronized Thread get() { return thread; }
    synchronized void clear() { thread = null; }
  }

  private ThreadVar threadVar;

  /** 
   * Get the value produced by the worker thread, or null if it 
   * hasn't been constructed yet.
   */
  public synchronized Object getValue() {
    return value; 
  }

  /** 
   * Set the value produced by worker thread 
   */
  private synchronized void setValue(Object x) { 
    value = x; 
  }

  /** 
   * Get the value produced by the worker thread, or null if it 
   * hasn't been constructed yet.
   */
  public synchronized Throwable getException() {
    return exception; 
  }

  /** 
   * Set the value produced by worker thread 
   */
  private synchronized void setException(Throwable x) { 
    exception = x; 
  }

  /** 
   * Compute the value to be returned by the <code>get</code> method. 
   */
  public abstract Object work() throws Throwable;

  /**
   * Called on the event dispatching thread (not on the worker thread)
   * after the <code>work</code> method has returned.
   */
  public void finished() {
    if (notifier != null) {
      notifier.run(getValue(), getException());
    }
  }

  /**
   * A new method that interrupts the worker thread.  Call this method
   * to force the worker to stop what it's doing.
   */
  public void interrupt() {
    Thread t = threadVar.get();
    if (t != null) {
      t.interrupt();
    }
    threadVar.clear();
  }

  /**
   * Return the value created by the <code>work</code> method.  
   * Returns null if either the constructing thread or the current
   * thread was interrupted before a value was produced.
   * 
   * @return the value created by the <code>work</code> method
   */
  public Object get() {
    while (true) {  
      Thread t = threadVar.get();
      if (t == null) {
        return getValue();
      }
      try {
        t.join();
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // propagate
        return null;
      }
    }
  }


  /**
   * Create a thread that will call the <code>work</code> method
   * and then run 
   * 
   */
  public SwingWorker(HandlingRunnable notifier) {
    this.notifier = notifier;
    final Runnable doFinished = new Runnable() {
       public void run() { finished(); }
    };

    Runnable doConstruct = new Runnable() { 
      public void run() {
        try {
          setValue(work());
        } catch (Throwable t) {
          setException(t);
        }
        finally {
          threadVar.clear();
        }

        SwingUtilities.invokeLater(doFinished);
      }
    };

    Thread t = new Thread(doConstruct);
    threadVar = new ThreadVar(t);
  }

  /**
   * Start the worker thread.
   */
  /*public void start() {
    Thread t = threadVar.get();
    if (t != null) {
      t.start();
    }
  }

  /** Run the work in the calling thread, and then the <code>notifier</code> callback.*/
  /*public Object run() throws Throwable {
    Object o = null;
    Throwable t = null;

    try {
      o = work();
    } catch (Throwable th) {
      t = th;
      setException(t);
    }
    setValue(o);
    finished();
    if (t != null)
      throw t;
    return o;
  }*/
  public void start(boolean threaded) {
    Thread t = threadVar.get();
    if (t != null) {
      if (threaded)
	t.start();
      else
	t.run();
    }
  }
}
