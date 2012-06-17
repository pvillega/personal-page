This page contains a brief summary of **Java Persistence API (JPA)**. It only gives basic information, for more details about life cycle and other functionalities check the EJB 3.0 reference or some book like **EJB3 in Action**.

Persistence is probably one of the biggest concerns of most enterprise applications. Data is key and we have to be sure it's consistent and our application can manage its changes properly. To that end lots of alternatives have been developed, with O/R mapping systems taking the led in the latest years due to the benefits of using an intermediate API that's closer to the OO model used in the business layer instead of direct interaction with the RDBMS.

EJB entities are the answer of EJB 3,0 to the persistence issue and it's implementation using JPA follows the same basic patterns as other components like session beans. That means they are POJOs with specific annotations (@Entity for JPA entities) that are capable of inheritance and whose non-abstract classes requires a public or protected no-argument constructor.

JPA is the most complex part of EJB 3.0. Not only controls the most critical component of our application but it provides a large number of options for a developer to be able to adapt it to his needs. I will try to break this complexity in some sections, like they do in the book EJB in Action, focussing on a few aspects of JPA. Remember this is just a quick summary that won't cover lots of aspects of JPA.

## Basic domain concepts: entities and primary keys

As said above JPA is an O/R mapping system. That means it maps Java POJOs (objects) to data stored in the database as tables and rows (relational data). To achieve this JPA does a heavy use of annotations. In this section I will show basic POJOs with some common annotations and explain how they are used. That will enables us to map our tables to the application, although by itself it won't let our application work. Let's start with a basic entity:

    @Entity
    public class BasicEntity implements Serializable {
    
        private static final long serialVersionUID = 1L;
    
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;
    
        @Transient
        private String disposableCode;
    
        /**
         * Get the value of disposableCode
         *
         * @return the value of disposableCode
         */
        public String getDisposableCode() {
            return disposableCode;
        }
    
        /**
         * Set the value of disposableCode
         *
         * @param disposableCode new value of disposableCode
         */
        public void setDisposableCode(String name) {
            this.disposableCode = name;
        }
    
        public Long getId() {
            return id;
        }
    
    
        public void setId(Long id) {
            this.id = id;
        }
    
        @Override
        public int hashCode() {
            int hash = 0;
            hash += (id != null ? id.hashCode() : 0);
            return hash;
        }
    
        @Override
        public boolean equals(Object object) {
            // TODO: Warning - this method won't work in the case the id fields are not set
            if (!(object instanceof BasicEntity)) {
                return false;
            }
    
            BasicEntity other = (BasicEntity) object;
            if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
                return false;
            }
    
            return true;
        }
    
        @Override
        public String toString() {
            return "jpa.BasicEntity[id=" + id + "]";
        }
    }

In this example we can see some interesting characteristics of an entity. First one, the entity is marked with the @Entity annotation, that identifies this POJO as an EJB 3,0 entity. Another important step is to override the methods hashcode, equals and toString. This is not mandatory but it's considered a best practice to implement hashcode and equals so they use our primary key to identify our entity, and changing toString allows us to log and debug the application easily.

The persistence provider will store the values of any property or field that has public or protected setters and getters, which can't be declared final, so no especial annotation is required to enable their persistence. In this example you can see we use a @Transient annotation in one field. This is equivalent to the transient keyword related to Serialization and tells the persistence manager to not persist that specific field.

Another detail about the annotations: in this example they are on the fields of the classes. You could write them on the getter methods of those fields, but whatever system you choose you have to stick to it for all you entities. The only difference is that using field-annotation you could make your fields public and not use any getter or setter method and they would be persisted, while using the annotation in the getter method obliges you to implement all the getters/setters and make the field private. Given that the first way is a very dangerous and bad practice, so you will have a private field with getter and setters in your entities, choosing one over the other is usually a matter of taste.

I've left the @Id annotation for last. This annotation, as you can guess, tells the entity where is stored our unique id. The field that can be marked as Id has some restrictions: it must be a primitive, its wrapper or a Serializable type. Types like double or float are forbidden due to their lack of precision (due to the way computers work, you should know what I mean) and that could cause two different entities to be considered the same.

But the @Id annotation is marking only one field and we can't have more than one @Id in our class, so what happens if we have a primary key that uses several fields? There are two approaches to it.

The first approach uses the @IdClass annotation as follows:

    @Entity
    @IdClass(BasicEntityPK.class)
    public class BasicEntity implements Serializable {
        @Id
        private Long id;
    
        @Id
        private String name;
    
        //other non Id fields
        //no need to implement equals and hashcode
        //getters and setters
    
    }
    
    public class BasicEntityPK implements Serializable {
        Long id;
        String name;
    
        public BasicEntityPK(){}
    
        //implement equals and hashcode
    }

As you can see we have our main entity class with several @Id fields assigned and a related class that contains the same fields. In this case we implement the equals and hashcode methods in the PK class, not in our entity. With this approach the container will use the entity to persist data and will map internally the fields to the PK class, which will be used when it needs to compare two entities.

The other alternative is the use of the @EmbeddedId annotation, like this:

    @Entity
    public class BasicEntity implements Serializable {
    
        @EmbeddedId
        private BasicEntityPK basicEntityPk;
        //other fields
        //no need to implement equals and hashcode
        //getters and setters
    
    }
    
    @Embeddable
    public class BasicEntityPK{
    
        Long id;
        String name;
    
        public BasicEntityPK(){}
        //implement equals and hashcode
    }

The skeleton is quite similar to the @IdClass one but in this case instead of having the primary key fields on both classes, we just put them in our PK class, and we embed that class into our entity as its primary key. Choosing between any of those methods is a matter of personal taste: one is harder to maintain due to replication, the other makes access to PK fields more awkward. Keep in mind that although you can use those structures sometimes is preferable the use of a surrogate key (we will review it later) as Id, as it will free you code from extra classes and it has better performance.

Now that we have talk about the @Embeddable annotation it might be a good time to explain its meaning. In our data model we may have some data that's always related to a certain object but that has sense by itself, like the address of a user. This address contains several fields (city, street) so it may be an entity but it's always related to a user. So in the object model it makes sense to implement it as its own object but in the relational model it will be in the same table as the user. To solve this JPA has the option to embed objects into entities, allowing us to declare this kind of relationship. An example:

    @Entity
    public class UserEntity implements Serializable {
    
        @Id
        protected Long id;
        protected String name;
        //other fields
    
        @Embedded
        protected Address address; 
    
        //implement equals and hashcode
        //getters and setters
    
    }
    
    
    @Embeddable
    public class Address implements Serializable {
    
        protected String street;
        protected String city;
        //other fields
    
        //implement equals and hashcode
    
    }

## Basic domain concepts: entities relationships

The data components in our model will have some kind of relationships between them: one-to-one, one-to-many, many-to-one or many-to-many. These relationships must be defined in our entities so the persistence provider can maintain the proper relations and the data consistency. In this section I will describe the annotations that manage that.

First one is the @OneToOne annotation, that defines a one-to-one relationship.This relationship can be unidirectional or bidirectional. Let's start with a bidirectional one:

    @Entity
    public class UserEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @OneToOne
        protected VisaInfoEntity visaInfoEntity;
        //other fields
    
        //implement equals and hashcode
        //getters and setters
    }
    
    @Entity
    public class VisaInfoEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @OneToOne(mapedBy = "visaInfoEntity", optional = "false")
        protected UserEntity userEntity;
    
        //other fields
        //implement equals and hashcode
        //getters and setters
    }

In this example we see the relation defined by the annotations in the parameters. Each class contain a parameter of the class that represents the entity related to it marked with @OneToOne. Notice that VisaInfoEntity adds some parameters to it's annotation, telling the persistence provider that the "owner" of the relation is the UserEntity class and which field is leading that mapping. The optional parameter set to false tells us there will always be a VisaInfoEntity for each user and a user for each VisaInfoEntity. If we want to convert this to a unidirectional link we just need to remove the reference to UserEntity and its annotation from VisaInfoEntity. With that the container will know our UserEntity may have zero or one VisaInfoEntity related to it.

The second type of relation is defined by the annotations @OneToMany and @ManyToOne. Those annotations usually go in pair to define both sides of the relation one-to-many. Usually in these relations one entity has a field of type Set or List of the other entity. One example:

    @Entity
    public class UserEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @OneToMany (mappedBy ="userEntity");
        protected Set visaInfoEntity;
        //other fields
        //implement equals and hashcode
        //getters and setters
    
    }
    
    
    @Entity
    public class VisaInfoEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @ManyToOne
        protected UserEntity userEntity;
    
        //other fields
        //implement equals and hashcode
        //getters and setters
    
    }

As you can see the owning part of the relation is VisaInfoEntity. In these type of relations the many-to-one part is always the leader, something to be aware of.

Last type of relations is defined by the annotation @ManyToMany. The relation is quite similar to the one-to-many explained above, but we can choose which entity is leading the relationship. An example:

    @Entity
    public class UserEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @ManyToMany (mappedBy ="userEntity");
        protected Set visaInfoEntity;
    
        //other fields
        //implement equals and hashcode
        //getters and setters
    
    }
    
    
    @Entity
    public class VisaInfoEntity implements Serializable {
    
        @Id
        protected Long id;
    
        @ManyToMany
        protected Set userEntity;
    
        //other fields
        //implement equals and hashcode
        //getters and setters
    
    }

## Mapping entities to DB

We know how to build entities and define relations between them. Now we need to map those entities to the tables of the database, as we will see next. Before looking at the example, be aware that using annotations for that purpose hard-codes the structure, so any changes to it will require a new version of the application. This is usually not a big deal but if you need to be able to change the mappings without compiling, use the XML descriptor version of the fields.

Next there's an example of a mapped entity. The meaning of the annotations is quite obvious, I will comment them after the code:

    @Entity
    @Table(name = "USERS")
    @SecondaryTable(name = "USER_PICTURES", pkJoinColumns = @PrimaryKeyJoinColumn(name = "USER_ID"))
    public class UserEntity implements Serializable {
      private static final long serialVersionUID = 1L;
     
      @Id
      @Column(name = "USER_ID", nullable = false, insertable=false, updatable=false)
      @GeneratedValue(strategy = GenerationType.AUTO)
      protected Long id;
    
    
      @Column(name = "USER_NAME", nullable = false)
      protected String name;
    
      @Column(name = "USER_LOGIN", nullable = false, length = 5)
      protected String login; @Enumerated(EnumType.ORDINAL)
    
      @Column(name = "USER_TYPE", nullable = false)
      protected UserType userType;
    
      @Column(name = "PICTURE", table = "USER_PICTURES")
      @Lob
      @Basic(fetch = FetchType.LAZY)
      protected byte[] picture;
    
      @Column(name = "CREATION_DATE", nullable = false)
      @Temporal(TemporalType.DATE)
      protected Date creationDate;
    
      @Embedded
      protected Address address;
    
      //setters, getters, hashcode, equals, etc
    
    }
    
    @Embeddable
    public class Address implements Serializable {
    
    //fields, setters, getters, hashcode, equals, etc
    }

The first annotation which we must pay attention to is @Table. This annotation specifies which table contains the columns we will use for the entity. If not specified (both the name or the tag) the system will use a table with the same name as the class.

The @Column annotation declares the mapping to a column of the table. As you can see, accepts some extra parameters to indicate if the field can be null or it can be modified using update or insert. The persistence manager assumes the columns exist in the class table, but you can specify another table if needed as you can see at the picture field. As with the @Table annotation, if you don't define the name of the column or you8 don't add the annotation it will be assumed the column has the same name as the field.

There are some extra annotations for specific types of data. The @Enumerated annotation allows us to store enumerations. We can use two modes: Ordinal or String. The first will store the integer associated to the enumeration value while the second will store the complete string.

The @Lob annotation tell the manager this field will be stored in a CLOB (for char[] or String fields) or BLOB (any other type) column in the database. Usually you add the @Basic annotation to a @Lob object to enable lazy loading, as @Lob objects are usually very memory intensive.

The @Temporal allows us to store date-related information. By default it uses a granularity of timestamp, but we can choose between DATE, TIME or TIMESTAMP.

For the primary key of the application you may use (if it's a surrogate key) the @GeneratedValue annotation. This annotation will tell the persistence manager the field's value is generated by the database. That means it will only exist once the entity has been commited, so we careful when accessing items not commited yet as they will have a null here.

The @GeneratedValue annotation has 4 available strategies to assign the value. The simplest one is IDENTITY and assigns an incremental value to the key. The second one is SEQUENCE, that requires a sequence to be defined both in the database and in EJB (using @SequenceGenerator). The third one is TABLE, which uses a a table as generator of the values (with @TableGenerator). The last strategy is AUTO, where the persistence manager decides which one is the best strategy.

I have skipped an annotation in the class header, @SecondaryTable. This one is used when we need to map an entity to several tables, in this case because we store the BLOB object in another table. As you can see, we tell the annotation which table it has to use and the name of join field. You can use several secondary tables in the same entity. Although this annotation doesn't seem so useful, it allows you to distribute content on several tables, which can improve the performance of the RDBMS as it happens in this example where the BLOB would slow queries done to the user table. That said be aware that this annotation is not the same as @Embedded, which we saw before and refers to content in the same table.

As you look at the code, you might wonder if maybe we need to specify join keys for tables relationship as we did in the @SecondaryTable annotation. The answer is yes, there are some annotations (three) to that end. I will briefly comment them:

+ **@JoinColumn**: this annotation allows you to specify primary/foreign relations in the database model, when the referencing entity has a primary key and the referenced entity it's own primary key and a foreign key mapped to the primary key of the referencing entity. It can be used in one-to-one and one-to-many/many-to-one relations.
+ **@PrimaryKeyJoinColumn**: this annotation allows you to specify primary/foreign relations in the database model when both referencing and referenced entities share the primary key of the referencing table and the referenced table uses it's primary key as foreign key. It can be used in one-to-one relations.
+ **@JoinTable**: this annotation allows you to specify primary/foreign relations in the database model when you have a many-to-many relation so you need to use an intermediate table to store the mappings. Is used in conjunction with the @JoinColumn annotation to specify the relation between the main tables and the intermediate one.

One last subject to talk about is how to map inheritance. As we said, JPA are POJO and they can use OO characteristics as inheritance, but how can we write that into the database? I won't enter in too much detail, but to this aim we have the @Inheritance annotation. It has 3 related strategies:

+ **Single table**: all data of the different subclases will be stored in the same table and we use one column (specified in the @DiscriminatorColumn annotation) to differentiate between subclasses. Each subclass will be marked with @DiscriminatorValue to tell the persistence manager which value maps to that type of entity.
+ **Joined Tables**: we use one-to-one relations to model the inheritance. @DiscriminatorColumn and @DiscriminatorValue are used the same way but we also need to define the @PrimaryKeyJoinColumn between the tables. Is the recommended method.
+ **Table per class**: we use one table per class, including one for the parent entity. No relations must be defined, and the inherited columns are duplicated across tables.Due to possible issues with polymorphism and entity retrieving, is not recommended to use this method

## Entity Manager

We've seen how to map the entities to the database, but you'll probably noticed I said nothing about how the entities are persisted. This section will talk about the Entity Manager, the interface responsible of all CRUD operations that allows you to retrieve and store entities from/into the database. An important consequence of this is that JPA being managed by this interface and not the container they don't have access to services like injection, timers, etc. It's not a big loose due to the way entities are (commonly) used but it's something to be aware of. I won't talk about the lifecycle in detail, just keep in mind the Entity Manager is only aware of entities in it's context (that can be as short as a method call or as long as a session), and an entity is not managed until you persist it.

A basic demonstration of the use of the Entity Manager follows. I won't comment the code as it's quite obvious:

    @Stateless
    public class ItemManagerBeanBean implements ItemManagerBeanLocal {
    
        @PersistenceContext(unitName="contextName")
        private EntityManager em;
    
        public Item addItem(String[] params...){
            Item it = new Item();
            //do something
            em.persist(it);
            return it;
        }
    
        public Item updateItem(Item it){
            em.merge(it);
            return it;
        }
    
        public Item undoItemChanges(Item it){
            //we merge to attach to the entity manager before refreshing
            em.refresh(em.merge(it));
            return it;
        }
    
        public void deleteItem(Item it){
            //we merge to attach to the entity manager before removing
            em.remove(em.merge(it));
        }
    
        public Item getItem(long key){
            return em.find(Item.class, key);
        }
    
    }

There's a scenario that we must discuss in more detail. We may have an entity that contains another entity (maybe in a one-to-one relation) and both are new entities not stored (yet) in the database. Although it seems a logic behaviour that when we persist the enclosing entity both are saved into the database, this is not the default behaviour. This is justified as cascading can be resource intensive when propagating to several instances, and probably most of them have not been modified which means we are wasting resources.

This functionality is defined by the "cascade" property that you can set when declaring relationships between entities (@OneToOne, etc). By default this property is empty so no persistence operation applied to one entity is propagated to related entities. You can change this by setting cascade to one of these values: MERGE, PERSIST, REFRESH, REMOVE or ALL. The first four tell the Entity Manager to propagate only the selected operation while the last one propagates all the persistence operations.

## Query API

The query API is a mechanism that allows you to find entities using SQL or JPQL, hiding the datasource behind the Entity Manager. It's a complement to the find method that allows you to recover several related entities a once. It allows for two kind of queries: named and dynamic.

Named queries are intended to be stored and reused, allowing for some optimizations. They are defined at class level and can receive parameters, and one entity may have several named queries. Using named queries you can isolate the SQL/JPQL from the places where is used, making it more maintainable. You may even define them in xml descriptors, decoupling them from the code.

An example of a named query would be:

    @Entity
    @NameQuery(name="findAll", query="Select c from category c where c.name like :name");
    public class category implements Serializable{
      //fields and methods
    
      public Category getCategoryByName(String name){
         //assume em is an instance of the Persistence Manager (Entity Manager)
         Query query = em.createNamedQuery("findAll"); //find named query by name
         query.setParamenter("name",name);
         return query.getResultList(); //returns list of entities
      }
    }

Dynamic queries are generated "on the fly", usually queries received from some other component. When possible is recommended to work with named queries, due to the performance enhancements they allow. En example of a dynamic query:

     //assume em is an instance of the Persistence Manager (Entity Manager)
     Query query = em.createQuery("Select c from categories c");
     return query.getResultList(); //returns list of entities

## JPQL and SQL

JPA accepts both JPQL and SQL for queries. Although they look quite similar, SQL works with the relational environment while JPQL works with entities, thus behaving in quite a different way behind the curtain. The recommendation for JPA is to use JPQL always, unless some odd exception requires us to use SQL.

I wont enter in details about JPQL as a brief tutorial won't cut it. I recommend you to check a good book on the subject.