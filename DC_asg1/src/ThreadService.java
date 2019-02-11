import java.util.concurrent.ArrayBlockingQueue;
        import java.util.concurrent.BlockingQueue;

public class ThreadService {

    public static void main(String[] args) {

        //Creating BlockingQueue used for communication between threads
        // queue for communication from po tp mo
        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(1000);
        // queue for communication from mo to po
        BlockingQueue<Message> mqueue = new ArrayBlockingQueue<>(1000);

        //1st arg: queue for communication
        //2nd arg: queue for communication
        //3rd arg: PO id
        //4th arg: failure flag
        Po po1 = new Po(queue, mqueue, 1, "NO");
        Po po2 = new Po(queue, mqueue, 2, "No");
        Po po3 = new Po(queue, mqueue, 3, "NO");
        Po po4 = new Po(queue, mqueue, 4, "NO");
        Mo mo = new Mo(queue, mqueue);

        // start mo thread
        new Thread(mo).start();

        // start po threads
        new Thread(po1).start();
        new Thread(po2).start();
        new Thread(po3).start();
        new Thread(po4).start();

        System.out.println("Po and Mo has been started");
    }

}
