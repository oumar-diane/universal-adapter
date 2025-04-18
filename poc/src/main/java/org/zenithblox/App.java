package org.zenithblox;

import org.apache.logging.log4j.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 *
 */
interface MessageListener {
    void onMessage(String message);
}

class EventConsumer {
    private final BlockingQueue<String> queue;

    public EventConsumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void registerListener(MessageListener listener) {
        new Thread(() -> {
            while (true) {
                try {
                    String message = queue.take();
                    listener.onMessage(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

}

class EventProducer {
    private final BlockingQueue<String> queue;

    public EventProducer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public boolean sendMessage(String message) {
        System.out.println("Sending message: " + message);
        return queue.offer(message);
    }
}

public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        EventConsumer consumer = new EventConsumer(queue);
        EventProducer producer = new EventProducer(queue);

        consumer.registerListener((message) -> {;
            System.out.println("Received message: " + message);
        });

        producer.sendMessage("Hello");

        Thread.sleep(1000); // Wait for the message to be processed
        System.out.println( "Hello World!" );
    }
}
