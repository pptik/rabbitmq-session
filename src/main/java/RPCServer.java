import com.rabbitmq.client.*;

/**
 * Created by fiyyanp on 1/5/2017.
 */
public class RPCServer {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final String HOST = "167.205.7.226";
    private static final String QUEUE_NAME = "hello";
    private static final String VHOST = "/sabugaSquad";
    private static final String USERNAME = "fiyyan" ;
    private static final String PASSWORD = "123qweasd";

    private static int fib(int n) {
        if (n ==0) return 0;
        if (n == 1) return 1;
        return fib(n-1) + fib(n-2);
    }

    public static void main(String[] args) {
        Connection connection = null;
        Channel channel = null;
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST);
            factory.setVirtualHost(VHOST);
            factory.setUsername(USERNAME);
            factory.setPassword(PASSWORD);

            connection = factory.newConnection();
            channel = connection.createChannel();

            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            channel.basicQos(1);

            //consume request from client
            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

            System.out.println(" [x] Awaiting RPC requests");

            while (true) {
                String response = null;
                String callbackQueue = "";

                QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                //get property from client request
                AMQP.BasicProperties props = delivery.getProperties();

                //get callbackqueue client
                callbackQueue = props.getReplyTo();

                //create properties data for client
                AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                        .Builder()
                        .correlationId(props.getCorrelationId()) //sending back corellation ID
                        .build();

                try {
                    String message = new String(delivery.getBody(),"UTF-8");
                    int n = Integer.parseInt(message);

                    System.out.println(" [.] fib(" + message + ")");
                    response = "" + fib(n); //processing fibbonanci
                }
                catch (Exception e){
                    System.out.println(" [.] " + e.toString());
                    response = "";
                }
                finally {
                    channel.basicPublish( "", callbackQueue, replyProps, response.getBytes("UTF-8"));

                    //send ack to rabbitMQ
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            }
        }
        catch  (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                }
                catch (Exception ignore) {}
            }
        }

    }
}
