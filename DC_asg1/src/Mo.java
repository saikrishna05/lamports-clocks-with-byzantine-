import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Mo implements Runnable{

    // queues for communication
    private BlockingQueue<Message> queue;
    private BlockingQueue<Message> mqueue;


    // current po clock values in MO.
    private int po1= 0;
    private int po2 = 0;
    private int po3 = 0;
    private int po4 =0;

    // initial clock values for poOffset
    private int po1Offset = 0;
    private int po2Offset = 0;
    private int po3Offset = 0;
    private int po4Offset = 0;

    // Master clock
    AtomicInteger masterClock = new AtomicInteger(0);

    public Mo(BlockingQueue<Message> q, BlockingQueue<Message> qq){
        this.queue= q;
        this.mqueue = qq;
    }

    @Override
    public void run() {

        try{
            for(int i=0; i < 100; i++){

                System.out.println(i + ":MO:" + masterClock);

                // Random
                int event = new Random().nextInt(2);

                // Increment Event
                if(event == 0){
                    //System.out.println("MO" + ":random:" + masterClock);
                    masterClock.getAndIncrement();
                    Thread.sleep(1000);
                }else{

                    // averages po clocks and sets its value
                    int average = (masterClock.get() + po1 + po2 + po3 + po4)/5;
                    masterClock.set(average);

                    //System.out.println("MO" + ":listen:" + masterClock);

                    // checks for all events which were pushed by PO
                    List<Message> waitingEvents = new ArrayList<>();
                    queue.drainTo(waitingEvents);

                    // for each even in list perform respective action
                    for(Message msg: waitingEvents){
                        String[] triggerEvent = msg.getEvent().split(":");

                        //if event type was send from PO
                        if(triggerEvent[1].equalsIgnoreCase("send")){
                            //System.out.println("MO" + ":ls:" + masterClock);

                            // Gets new po clock values
                            if(Integer.parseInt(triggerEvent[0]) == 1){
                                po1 = Integer.parseInt(triggerEvent[2]);
                            }else if(Integer.parseInt(triggerEvent[0]) == 2){
                                po2 = Integer.parseInt(triggerEvent[2]);
                            }else if(Integer.parseInt(triggerEvent[0]) == 3){
                                po3 = Integer.parseInt(triggerEvent[2]);
                            }else{
                                po4 = Integer.parseInt(triggerEvent[2]);
                            }
                        }else{
                            //System.out.println("MO" + ":LR:" + masterClock);

                            //If event was receive
                            po1Offset = masterClock.get() - po1;
                            po2Offset = masterClock.get() - po2;
                            po3Offset = masterClock.get() - po3;
                            po4Offset = masterClock.get() - po4;

                            //calculates offset and pushes new offset values into the queue
                            if(Integer.parseInt(triggerEvent[0]) == 1){
                                Message message = new Message();
                                String offset = "1:"+po1Offset;
                                message.setEvent(offset);
                                mqueue.put(message);
                            }else if(Integer.parseInt(triggerEvent[0]) == 2){
                                Message message = new Message();
                                String offset = "2:"+po2Offset;
                                message.setEvent(offset);
                                mqueue.put(message);
                            }else if(Integer.parseInt(triggerEvent[0]) == 3){
                                Message message = new Message();
                                String offset = "3:"+po3Offset;
                                message.setEvent(offset);
                                mqueue.put(message);
                            }else{
                                Message message = new Message();
                                String offset = "4:"+po4Offset;
                                message.setEvent(offset);
                                mqueue.put(message);
                            }

                            po1Offset = 0;
                            po2Offset = 0;
                            po3Offset = 0;
                            po4Offset = 0;
                            masterClock.getAndIncrement();

                        }
                    }
                    Thread.sleep(3000);

                }

            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
