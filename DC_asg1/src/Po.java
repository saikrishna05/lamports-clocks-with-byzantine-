
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Po implements Runnable {

    // queues for communication
    private BlockingQueue<Message> queue;
    private BlockingQueue<Message> mqueue;

    // id of PO (1 for Po1, 2 for Po2, 3 for Po3, 4 for Po4)
    private int id;

    //message object which will be pushed to queue
    private Message msg ;

    //initial po clock value
    private int clock = 0;

    // failure flag
    private String failure;

    Po(BlockingQueue<Message> q, BlockingQueue<Message> qq, int id, String failure){
        this.queue=q;
        this.id = id;
        this.mqueue = qq;
        this.msg = new Message();
        this.failure = failure;
    }

    @Override
    public void run() {
        //produce messages
        for(int i=0; i<100; i++){
            // prints po clock value at ith iteration
            System.out.println(i +":PO"+  id+":" + clock);

            // random
            int event = new Random().nextInt(3);

            // when random = 0, increment event, and on failure po then random increment by 20
            if(event == 0){

                int failureRandom = new Random().nextInt(3);
                if(failureRandom == 0 && failure.equalsIgnoreCase("yes")){
                    clock += 20;
                }else{
                    clock++;
                }
                //System.out.println(id + ":random:" + clock);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else if(event == 1){

                // PO send event

                clock++;
                //System.out.println(id + ":send:" + clock);


                // pushes current clock value into queue, if a clock value was already in queue then it will replace it,
                // else push
                // drains all values and checks if send event was already in queue, if yes, it will replace
                List<Message> messageInQueue = new ArrayList<>();
                queue.drainTo(messageInQueue);

                if(!messageInQueue.isEmpty()){
                    for(Message message : messageInQueue){
                        String currentMessage = message.getEvent();
                        if(Integer.parseInt(currentMessage.split(":")[0]) == id){
                            if((currentMessage.split(":")[1]).equalsIgnoreCase("send")){

                                // pushes send event message, this message is delimited on MO
                                msg.setEvent(id + ":send:" + clock);
                            }
                        }else{
                            try {
                                queue.put(message);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            msg.setEvent(id + ":send:" + clock);
                        }
                    }
                }else{
                    msg.setEvent(id + ":send:" + clock);
                }


                try{
                    queue.put(msg);
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }

                clock = correctClockWithOffset(clock);

            }else{
                // Receive event


                // drains all values and checks if send Receive was already in queue, if yes, it will replace
                // sends only message receive
                // also corrects its clock value with offset which was sent my MO
                // correctClockWithOffset() does the clock correction
                List<Message> messageInQueue = new ArrayList<>();
                queue.drainTo(messageInQueue);

                if(!messageInQueue.isEmpty()){
                    for(Message message : messageInQueue){
                        String currentMessage = message.getEvent();
                        if(Integer.parseInt(currentMessage.split(":")[0]) == id){
                            if((currentMessage.split(":")[1]).equalsIgnoreCase("receive")){
                                msg.setEvent(id + ":receive");
                            }
                        }else{
                            try {
                                queue.put(message);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            msg.setEvent(id + ":receive");
                        }
                    }
                }else{
                    msg.setEvent(id + ":receive");
                }


                try{
                    queue.put(msg);
                    Thread.sleep(2000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                clock = correctClockWithOffset(clock);
                //System.out.println(id + ":receive:" + clock);

            }

        }
    }

    private int correctClockWithOffset(int clock) {
        List<Message> offsetString = new ArrayList<>();
        mqueue.drainTo(offsetString);

        if(!offsetString.isEmpty()){
            Message msg = offsetString.get(0);

            if(Integer.parseInt(msg.getEvent().split(":")[0]) == 1){
                clock += Integer.parseInt(msg.getEvent().split(":")[1]) + 1;
            }else if(Integer.parseInt(msg.getEvent().split(":")[0]) == 2){
                clock += Integer.parseInt(msg.getEvent().split(":")[1]) + 1;
            }else if(Integer.parseInt(msg.getEvent().split(":")[0]) == 3){
                clock += Integer.parseInt(msg.getEvent().split(":")[1]) + 1;
            }else{
                clock += Integer.parseInt(msg.getEvent().split(":")[1]) + 1;
            }

        }

        return clock;
    }

}
