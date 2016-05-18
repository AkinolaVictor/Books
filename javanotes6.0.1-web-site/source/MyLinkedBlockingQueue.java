import java.util.LinkedList;

/**
 *  As a simple example of using wait() and notify(), this class
 *  implements the three methods of a LinkedBlockingQueue that
 *  are used in the sample program MultiprocessingDemo3.
 */
public class MyLinkedBlockingQueue {
   
   private LinkedList<Runnable> taskList = new LinkedList<Runnable>();
   
   public void clear() {
      synchronized(taskList) {
         taskList.clear();
      }
   }
   
   public void add(Runnable task) {
      synchronized(taskList) {
         taskList.addLast(task);
         taskList.notify();
      }
   }
   
   public Runnable take() throws InterruptedException {
      synchronized(taskList) {
         while (taskList.isEmpty())
            taskList.wait();
         return taskList.removeFirst();
      }
   }

}
