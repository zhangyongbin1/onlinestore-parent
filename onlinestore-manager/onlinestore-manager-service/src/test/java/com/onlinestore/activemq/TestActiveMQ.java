package com.onlinestore.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;
import javax.swing.plaf.synth.SynthTextAreaUI;

/**
 * 测试activeMq的API接口
 */
public class TestActiveMQ {
    @Test
    public void testQueueProducer() throws Exception{//点对点模式下的消息生产者
        //1.创建连接工厂对象connectionFactory对象。需要指定mq服务的ip及端口
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.26.128:61616");
        //2.使用连接工程对象connectionFactory创建一个连接connection对象
        Connection connection = connectionFactory.createConnection();
        //3.开启连接
        connection.start();
        //4.使用connection对象创建一个session对象
        /**
         * 第一个参数是是否开启事务。保证数据的最终一致，可以使用消息队列实现
         * 如果第一个参数为true，第二参数自动忽略。如果不开启事务false，第二参数为消息的应答模式。一般自动应答就可以
         */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.使用session对象创建一个Destination对象，有两种形式，queue、topic
        //参数就是消息队列的名称
        Queue queue = session.createQueue("test-queue");
        //6.使用session对象创建一个生产者对象,参数是Destination对象queue
        MessageProducer producer = session.createProducer(queue);
        //7.使用session生产一个message
        /*TextMessage textMessage = new ActiveMQTextMessage();
		textMessage.setText("hello activemq");*/
        TextMessage message = session.createTextMessage("hello activemq123");
        //8.发送消息
        producer.send(message);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testQueueConsumer() throws Exception{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.26.128:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //使用session创建一个destination，destination应该和消息发送端一致
        Queue queue = session.createQueue("test-queue");
        MessageConsumer consumer = session.createConsumer(queue);
        //向consumer对象中设置一个messageListener对象用来接收消息
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                //获取消息内容
                if(message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String text = textMessage.getText();
                        //打印消息内容
                        System.out.println(text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //系统等待接收消息
		/*while(true) {
			Thread.sleep(100);
		}*/
        System.in.read();
		consumer.close();
		session.close();
		connection.close();
    }

    @Test
    public void testTopicProducer() throws Exception{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.26.128:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("test-topic");
        MessageProducer producer = session.createProducer(topic);
        TextMessage textMessage = session.createTextMessage("testActiveMqtopic");
        producer.send(textMessage);
        producer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testTopicConsumer() throws Exception{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.26.128:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("test-topic");
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if(message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        String text = textMessage.getText();
                        System.out.println(text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        System.out.println("topic消费者.。。。");
        System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();
    }
}
