API
===

This contains the core Skillz API classes, and everything you should need to create
a simple JavaEE/Servlet-based implementation of an Alexa skill.

Getting Started
---------------

### Configuring for JavaEE/Simple Servlets

The simplest way to get started is to simply add the ```Skillz Filter``` class to 
your ```web.xml``` file.

```
    <filter>
       <filter-name>SkillzFilter</filter-name>
       <filter-class>net.kebernet.skillz.SkillzFilter</filter-class>
    </filter>
    
    <filter-mapping>
       <filter-name>SkillzFilter</filter-name>
       <url-pattern>/*</url-pattern>
    </filter-mapping>
```

This will give you a stock implementation of the filter with default values for 
all the implementation classes. You can also bind the ```url-pattern``` to a sub-path
such as ```/skills/*``` if you would like. When you do that, all the ```path``` values
declared in your ```@Skill``` annotations will be relative to that path. 

The ```SkillzFilter``` will NOT have any impact on anything else on whatever path you
bind it to. If there is no skill at the requested path, it will simply pass the values
through.

### Configuring for Dependency Injection

The ```SkillzFilter``` contains an ```@Inject``` annotated constructor that you can
use if you are using Guice/Dagger/Spring to configure your servlet environment. 
It requires:

```Registry registry, TypeFactory factory, FormatterMappings mappings```

```Registry``` is the internal registry of skill classes. If you simply construct one
with the no-args constructor, it should be fine, and this constructor is annotated with
```@Inject``` . 

```TypeFactory``` is the Skillz-specific abstraction used for constructing new 
instances of classes. You will want to provide an implementation of this appropriate
to your system. This might typically look like, for a Guice module:

```
    @Provides
    @Singleton
    TypeFactory createTypeFactory(final Injector injector){
        return new TypeFactory() {
               @Override
               public <T> T create(Class<T> type) {
                   return injector.createIntance(type);
               }
           };
    }
```

There is also a ```DefaultTypeFactory``` implementation that will simply attempt to
build everything with a no-args constructor.

The TypeFactory will be used for all types in the system, so you will want to ensure
Formatters, etc are properly configured.

```FormatterMappings``` is a simple map-type structure that maps a return object from
a skill method to a ```Formatter``` that will render it to a ```SpeechletResponse```
to send back to Alexa. You can either provide a class reference, that will be passed
to the ```TypeFactory``` for instantiation, or a ```Provider<T extends Formatter>``` 
implementation.


Creating Your First Skill
-------------------------

To create a skill you need to annotate a POJO class with the Skillz annotations. This
will let you take in data from Alexa, and in the end, return a proper ```SpeechletResponse```
to the Alexa Skills Kit.

Let's take a look at a simple class:

```
    public class SearchForThings {
        public List<String> search(String query) {
            //... do the search
        }
    }
```

 1.  Annotate with ```@Skill```.
     
     The first step is to put a skill annotation and declare a path for your
     skill:
     
     ```
     @Skill(path="/searchMyStuffSkill")
     public class SearchForThings {
     ```
     
 2.  Annotate a method with ```@Intent```. 
  
     We are not going to get into what intents are and how they work here. Please
     refer to the Amazon documentation for that. Needless to say, we want our method
     to be exposed as an Intent for Alexa to invoke:
     
     ```
        @Intent("Search")
        public List<String> search(String query)
     ```
     
 3.  Annotate your parameters with where they should come from in the ASK request.
     here we are going to use a Slot called "query".
     
     ```
         @Intent("Search")
         public List<String> search(@Slot(name="query", type="AmazonSlotTypes.LITERAL") String query)
     ```
     
     Again, we are not going to get into the various slot types. Please refer to the
     ASK documentation for that. But here we have defined a slot named ```query``` 
     that we use to perform the search. Getting pretty close!
     
 4.  Annotate your method with "Utterances" that Amazon will use as templates for user
     input.
     
     ```
         @Intent("Search")
         @Utterances({
            "search for {query|aurora coffee}",
            "find {query|aurora coffee}",
            "give me {query|aurora coffee}"
         })
         public List<String> search(@Slot(name="query", type="AmazonSlotTypes.LITERAL") String query)
      ```
      
      These are the lines you will use to populate the Amazon web site later.
      
 5.   Almost there... Now we have a ```List<String>``` containing our search results,
      but we need to turn that into a SpeechletResponse. The first step is to put
      a ```@ResponseFormatter``` annotation on our method:
      
      ```
         @Intent("Search")
         @Utterances({
            "search for {query|aurora coffee}",
            "find {query|aurora coffee}",
            "give me {query|aurora coffee}"
         })
         @ResponseFormatter(SearchFormatter.class)
         public List<String> search(@Slot(name="query", type="AmazonSlotTypes.LITERAL") String query)
      ```
      
      And once we have done that, we need to implement the formatter. This will turn
      our list of Strings our search method was already returning into an English 
      expression that Alexa will speak back to the user.
      
      ```
        public class SearchFormatter implements Formatter<List<String>> {
        
            public SpeechletResponse apply(List<String> value){
            
                final StringBuilder response = new StringBuilder("I found ")
                    .append(value.size())
                    .append("things. They are ");
                values.forEach( s-> response.append(s).append(' ') );   
                PlainTextSpeechOutput output = PlainTextOutputBuilder.withText(response.toString())
                                                    .build();
            
                return SpeechletResponse.newTellResponse(output);
            }
        
        }
      ```
      
      That is kind of painful, right? Check out the ```formatter``` subproject for 
      ways to simplify creating formatters.
      
 6.   Finally we need to populate our skill's metadata in Amazon's UI, but we have
      defined all that in our classes! To get the information, simply start your
      container and request:
      
      ```
      https://hostname/searchMyStuffSkill?utterances
      ```
      
      to get the utterances text, or 
      
      ```
      https://hostname/searchMyStuffSkill?intents
      ```
      
      to get your Intents and Slot definitions. 
      
 7. Profit!
 
Intent Lifecycle
================

At this point in the process, you have surely looked at the Amazon documentation and
understand your intent can be invoked in several ways. You can use the following 
annotations to get methods on your class invoked at different points in the lifecycle:

 1.  ```@SessionStarted``` If the user has created a session you will get this call
 2.  ```@Launched``` Is called when your Skill is launched for interactive mode.
 3.  ```@SessionEnded``` Is called when a user session ends.
 
You might want to get extended information about the request of the session for 
your methods that are not included in the ```@Slot``` values. To do this you use the
```@ExpressionValue``` annotation. This allows you to specify an OGNL expression 
that will be evaluate against the SpeechletRequest (request) and Session (session)
objects.

For example, ```@ExpressionValue("session.attributes")``` gives you a ```Map<>```
of the current session values. ```@ExpressionValue("session.attributes.selectedItemId")```
will give you the "selectedItemId" key from the map. 
```@ExpressionValue("session.user.userId")``` returns the unique user id.

See also [https://en.wikipedia.org/wiki/OGNL]