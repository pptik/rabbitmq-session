package producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by fiyyanp on 1/5/2017.
 */
public class Sender {

    private static final String HOST = "167.205.7.226";
    private static final String QUEUE_NAME = "hello";
    private static final String VHOST = "/sabugaSquad";
    private static final String USERNAME = "fiyyan" ;
    private static final String PASSWORD = "123qweasd";
    private static final String EXCHANGE_NAME = "logs_topic";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setVirtualHost(VHOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //define exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String messageCool = "cool";
        String messageKeren = "keren";

        //publish
        channel.basicPublish(EXCHANGE_NAME, "sabuga.cool", null, messageCool.getBytes("UTF-8"));//publish message ke queue
        channel.basicPublish(EXCHANGE_NAME, "sabuga.keren", null, messageKeren.getBytes("UTF-8"));//publish message ke queue
        System.out.println(" [x] Sent '" + messageCool + "'");
        System.out.println(" [x] Sent '" + messageKeren + "'");

        channel.close();
        connection.close();
    }
}
