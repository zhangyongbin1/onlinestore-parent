package com.onlinestore.activemq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 测试spring与activeMq整合
 */
public class TestSpringActivemq{

    @Test
    public void testJmsTemplate(){
        //初始化spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
        //从容器中获取JMSTemplate
        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
        //从容器中获取destination 对象
        //从容器中获得Destination对象
        Destination destination = (Destination) applicationContext.getBean("test-queue");
        //发送消息
        jmsTemplate.send(destination, new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage("spring activemq send queue message");
                return message;
            }
        });
    }

}
