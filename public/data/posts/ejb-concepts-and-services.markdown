This page contains a brief summary of **EJB 3.0 Services** and related concepts. It only gives basic information, for more details about life cycle and other functionalities check the EJB 3.0 reference or some book like **EJB3 in Action**.

Next I will describe some key concepts related to EJB 3.0

## EJB Context

The EJBContext interface (definition available [here](http://docs.oracle.com/javaee/5/api/javax/ejb/EJBContext.html)) provides methods to interact with services like transaction, security, timer or even to do JNDI lookups. Both Session and Message beans implement this interface through inheritance.

You can access this interface through the SessionContext object to be able to call its methods. Depending on which bean are you using (Session or MDB) the container will add some extra methods to the object that may be useful for your bean. Examples of use of context in Session and Message-Driven beans:

    @Stateless
    public class PlaceBean implements PlaceLocal {
    
        @Resource
        private SessionContext context;
    }
    
    
    @MessageDriven
    public class MDBTestBean implements MessageListener {
    
        @Resource
        private MessageDrivenContext context;
    }

## Dependency Injection

JEE 5 introduces dependency injection to remove all the hassle related to JNDI calls. You still can do JNDI calls using SessionContext objects but the easiest and recommended way is to use DI.

There are several annotations for this purpose. @EJB is used to inject beans while @Resource can be used as a more versatile mechanism of DI. @Resource allows you to provide a JNDI name of a resource defined in the container and it processes all the calls needed to load that resource, and it can be related to an instance variable or a setter method that follows java bean format.

Examples of dependency injection:

    @Stateless
    public class OtherBeanBean implements OtherBeanLocal {
    
        @Resource(name = "jms/ShippingErrorQueue")
        private Destination errorQueue;
    }
    
    @Stateless
    public class PlaceBean implements PlaceLocal {
    
        @EJB
        private OtherBeanLocal otherBeanBean;
    }

## Interceptors

Interceptors are a form of Aspect-Oriented Programming (AOP) in EJB 3. They are objects that are automatically triggered when some EJB method is invoked.

This is achieved using the @Interceptors annotation, which can be applied to a method or an entire class (in that case it will affect all methods of the class, you can use the annotation @ExcludeClassInterceptors on a method to ignore the interceptor for that method). The annotation receives as parameters a list of class objects that are used as interceptors.

Each interceptor class must contain one (and only one) method annotated as @AroundInvoke, that signals the method to be used as interceptor. This method always has the same format, returning an Object and receiving as its only parameter an InvocationContext object. This class provides methods to check the name of the method called, parameters and other details and allows us to modify the parameters passed to the business method if needed.

The interceptor to end always with a call to the "proceed" method of InvocationContext to signal teh container it can proceed with the next step (next interceptor or the original method itself). Not calling proceed will halt the chain and the business method will never be accessed. One example of an interceptor and the method intercepted:

    public class InterceptorExample {
    
        @AroundInvoke
        public Object logMethod(InvocationContext invocationContext) throws Exception{
            log.warn("Method: "+invocationContext.getMethod().getName());
            return invocationContext.proceed();
        }
    }
    
    
    
    @Stateless
    public class PlaceBean implements PlaceLocal {
    
        @Interceptors(InterceptorExample.class)
        public void method(){
    
         }
    }

## Timer

EJB 3 includes a basic timer service to schedule jobs. You can queue objects into the timer inside a bean and assign a method of that bean to process the timer events. Timer can be used to schedule single-events or recurring ones.

One bean can have only one @Timeout method and it must follow the same structure (void return and receiving a Timer as parameter). You can only receive timer events in the same bean they were created (not same instance!) so you can't use Timer to send message across beans by itself, you will need to use JMS in the @Timeout method.

Example of a simple timer service with a method setting the timer and the @Timeout consumer:

    @Stateless
    public class PlaceBean implements PlaceLocal {
    
       @Resource
        TimerService timerService;
    
       public void createTimer(){
           String attachment = "attachment";
           timerService.createTimer(10*1000, 10*1000, attachment);
       }
    
       @Timeout
       public void scheduledMethod(Timer timer){
           String attachment = (String) timer.getInfo();
       }
    
    }

## Transactions

The JEE container does most of the heavy lifting on transactions, as implementing a working transaction manager is no easy task and is prone to errors. You should review concepts like ACID (atomicity, Consistency, Isolation, Durability) and two-phase commit before proceeding, to properly understand what's going on behind the scenes. In JEE the JTA API is responsible of transaction management.

There are two kind of transactions, container managed transaction (CMT) and bean managed transaction (BMT). CMT is simpler and the container does most of the work while the BMT provides more control over the process in exchange of more coding effort.

To use CMT we annotate with @TransactionManagement the class of the bean, indicating with the parameter the transaction will be managed by the container. Then we can flag the class or any transactional methods with @TransactionAttribute. This annotation tells the container how to behave when running the bean/method annotated: create a new transaction, reuse any existing transaction, never use a transaction... We must be aware our method may be called form another method that has started its own transaction, so its important to specify the behaviour of our bean. It has 6 choices: Required, Requires New, Supports, Mandatory, Not Supported and Never.

To rollback a transaction you must use the EJBContext, calling its "setRollbackOnly" method. You can check if any underlying CMT transaction has been marked for rollback using "getRollbackOnly", that way you can exit long operations before they finish as the transaction will fail anyway. An example of a CMT bean:

    @Stateless
    @TransactionManagement(TransactionManagementType.CONTAINER)
    public class CMTBean implements CMTLocal {
    
        @Resource
        private SessionContext context;
    
        @TransactionAttribute(TransactionAttributeType.REQUIRED)
        public void transactionMethod(String p1, String p2) {
            try{
                //code
            }catch(Exception e){
                context.setRollbackOnly();
            }
        }
    
    }

BMT is the other type of transaction, one which gives us a granular control over the transaction itself. Instead of it being related to a method we can specify the start of the transaction and its end. It works around the UserTransaction interface of the JTA API, using this interface to start, commit and rollback the transaction. The only annotation used is @TransactionManagement, to tell the container this bean is BMT.

The interface also contains some additional methods to manage the transactions. The method "setRollbackOnly" is used to mark the transaction as "roll back" if our BMT interacts with CMT beans, The method "getStatus" gives us detailed information of the transactional status, while "setTransactionTimeout" allows us to change the timeout of the transaction.

An example of BMT:

    @Stateless
    @TransactionManagement(TransactionManagementType.BEAN)
    public class BMTBean implements CMTLocal {
    
        @Resource
        private UserTransaction userTransaction;
    
        public void transactionMethod(String p1, String p2) {
            try{
                userTransaction.begin();
                //code
                userTransaction.commit();
            }catch(Exception e){
                userTransaction.rollback();
            }
        }
    }

## Security

JEE provides a flexible security model, based on the standard concepts of users, groups and roles, that manages both authentication and authorization. This is achieved using the JASS API, that abstract the underlying authentication systems (like LDAP). If you use a web front-end you will probably define some security aspects in web.xml. This section will only review the part related to EJB.

The most common use case is called "declarative security", consisting on some annotations that tell the container who can access which method. One example should be enough to understand it:

    @DeclareRoles({"RoleA", "RoleB", "RoleC"})
    @Stateless
    public class DeclarativeSecurityBean implements DeclarativeSecurityLocal {
    
        @RolesAllowed({"RoleA","RoleC"})
        public void businessMethod1() {
        }
    
        @PermitAll
        public void businessMethod2() {
        }
    
        @DenyAll
        public void businessMethod3() {
        }    
    
    }

On the other hand, programmatic security uses the EJBContext to check the role of the user. This system should not be used unless completely necessary due to requirements, as it makes the security layer much harder to manage, One good point about this kind of security is that it can be used along interceptors, but even with this advantage if we have several roles managing the system is no easy task.

One example will show why it's not recommended (think about an application with several beans and roles):

    @Stateless
    public class ProgrammaticSecurityBean implements ProgrammaticSecurityLocal {
    
       @Resource
       private SessionContext context;
    
        public void businessMethod() {
            if(!context.isCallerInRole("RoleA") && !context.isCallerInRole("RoleB")){
               return;
            }
            //code for other roles
        }
    }

