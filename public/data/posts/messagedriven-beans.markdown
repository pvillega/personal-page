This page contains a brief summary of **EJB 3.0 Message-Driven Beans (MDB)**. It only gives basic information, for more details about life cycle and other functionalities check the EJB 3.0 reference or some book like **EJB3 in Action**.

Message-Driven Beans are an abstraction over Java Messaging Service (JMS). They are used to send and receive messages in your application without having to know all the details about JMS. That said a good understanding of JMS is important for the correct use of MDB. The advantages of MDB over standard JMS coding are the services provided by the container (like pooling) and the reduction on code size due to automation of tasks.

By message (in the context of a JEE application) we mean an asynchronous and loosely coupled communication between system components. Using Message-Oriented Middleware (MOM) messages sent are stored until a consumer is available to read them. We consider two types of communication:

+ **Point-to-point**: the message is stored in a queue and received by one of the N possible consumers of the message
+ **Publish-Subscribe**: message is received by N subscribers that belong to a specific group of receivers

MDB are POJOs like other EJB and require a public constructor without arguments. You can't throw any RuntimeException or RemoteException as that would cause the MDB to be terminated. They also have the following requirements:

+ Implement MessageListener interface

+ use @MessageDriven annotation to configure the queue they are listening and some JMS properties (using @ActivationConfigProperty). Values include:

    + acknowledgeMode: (on Queues) to notify the consumption of the message

    + subscriptionDurability: (on Topics) guaranties delivery of message to offline subscribers

    + messageSelector: filters messages consumed using a property of the message included in the message header


+ it has two lifecycle callbacks (@PreDestroy and @PostConstruct) equivalent to the Session Bean ones.

+ can send messages but you need to follow the JMS API for that, using a Connection Factory, etc.

+ by default the "onMessage" method creates a transaction that rolls-back if there's any error while processing the message, leaving the message in the queue for another consumer.

Example of MDB listening to a Queue (point-to-point communication) named SampleServerQueue:

    @MessageDriven(mappedName = "SampleServerQueue", activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
    })
    public class MDBTestBean implements MessageListener {
    
        //create the references to a destination Queue where we will send messages using DI
        @Resource(name = "jms/ShippingErrorQueue")
        private Destination errorQueue;
    
        @Resource(name = "jms/QueueConnectionFactory")
        private ConnectionFactory connectionFactory;
    
        private Connection jmsConnection;
    
        public MDBTestBean() {
        }
    
        //message consumer, reads a message from the queue
        public void onMessage(Message message) {
        }
    
        //send a message
        public void sendMessage() {
    
            try {
                Session session = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(errorQueue);
                ObjectMessage message = session.createObjectMessage();
    
                //add info to message
                message.setObject("Message");
    
                producer.send(message);
                session.close();
    
            } catch (JMSException ex) {
                Logger.getLogger(MDBTestBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    
        @PostConstruct
        private void onConstruct() {
    
            try {
                jmsConnection = connectionFactory.createConnection();
            } catch (JMSException ex) {
                Logger.getLogger(MDBTestBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    
        @PreDestroy
        private void onDestroy() {
    
            try {
                jmsConnection.close();
            } catch (JMSException ex) {
                Logger.getLogger(MDBTestBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }