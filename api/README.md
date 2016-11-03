Skills API
==========

This contains the core Skillz API classes, and everything you should need to create
a simple JavaEE/Servlet-based implementation of an Alexa skill.

Getting Started
---------------

### Review the Amazon docs. 

This will make your life a lot easier. 

[https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/](https://developer.amazon.com/public/solutions/alexa/alexa-skills-kit/)

We will not be covering the whys and wherefores here. We are assuming you understand how
the ASK works and what the components of a skill are.


### Dependencies




### Configuring for JavaEE/Simple Servlets

The simplest way to get started is to simply add the ```Skillz Filter``` class to 
your ```web.xml``` file.

```xml
    <filter>
       <filter-name>SkillzFilter</filter-name>
       <filter-class>net.kebernet.skillz.SkillzFilter</filter-class>
       <init-param>
          <param-name>prefixPath</param-name>
          <!-- this needs to match your url pattern, but is optional in
               the case of /* -->
          <param-value>/</param-value>
       </init-param>
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

```Registry``` is the internal registry of skill classes. If you simply construct onewith the no-args constructor, it should be fine, and this constructor is annotated with
```@Inject``` . 

```TypeFactory``` is the Skillz-specific abstraction used for constructing new 
instances of classes. You will want to provide an implementation of this appropriate
to your system. This might typically look like, for a Guice module:

```java
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

```java
    public class SearchForThings {
        public List<String> search(String query) {
            //... do the search
        }
    }
```

 1.  Annotate with ```@Skill```.
     
     The first step is to put a skill annotation and declare a path for your
     skill:
     
     ```java
     @Skill(path="/searchMyStuffSkill")
     public class SearchForThings {
     ```
     
 2.  Annotate a method with ```@Intent```. 
  
     We are not going to get into what intents are and how they work here. Please
     refer to the Amazon documentation for that. Needless to say, we want our method
     to be exposed as an Intent for Alexa to invoke:
     
     ```java
        @Intent("Search")
        public List<String> search(String query)
     ```
     
 3.  Annotate your parameters with where they should come from in the ASK request.
     here we are going to use a Slot called "query".
     
     ```java
         @Intent("Search")
         public List<String> search(@Slot(name="query", type=AmazonSlotTypes.LITERAL) String query)
     ```
     
     Again, we are not going to get into the various slot types. Please refer to the
     ASK documentation for that. But here we have defined a slot named ```query``` 
     that we use to perform the search. Getting pretty close!
     
 4.  Annotate your method with "Utterances" that Amazon will use as templates for user
     input.
     
     ```java
         @Intent("Search")
         @Utterances({
            "search for {aurora coffee|query}",
            "find {aurora coffee|query}",
            "give me {aurora coffee|query}"
         })
         public List<String> search(@Slot(name="query", type=AmazonSlotTypes.LITERAL) String query)
      ```
      
      These are the lines you will use to populate the Amazon web site later.
      
      Sometimes you might want to include a some generated utterances. If you want to do this, you can
      have an utterance that looks like:
      ```java
         @Utterances("include-url:http://my.host/myUtteranceGenerator")
      ```
      
      This should return a line-by line list of utterances that will be included in the generation with
      the intent.
      
 5.   Almost there... Now we have a ```List<String>``` containing our search results,
      but we need to turn that into a SpeechletResponse. The first step is to put
      a ```@ResponseFormatter``` annotation on our method:
      
      ```java
         @Intent("Search")
         @Utterances({
            "search for {aurora coffee|query}",
            "find {aurora coffee|query}",
            "give me {aurora coffee|query}"
         })
         @ResponseFormatter(SearchFormatter.class)
         public List<String> search(@Slot(name="query", type=AmazonSlotTypes.LITERAL) String query)
      ```
      
      And once we have done that, we need to implement the formatter. This will turn
      our list of Strings our search method was already returning into an English 
      expression that Alexa will speak back to the user.
      
      ```java
        public class SearchFormatter implements Formatter<List<String>> {
        
            public SpeechletResponse apply(List<String> value, SpeechletRequet request, Session session){
            
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
----------------

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

See also [https://en.wikipedia.org/wiki/OGNL](https://en.wikipedia.org/wiki/OGNL)

Formatting Output
-----------------

You have already seen that ```Formatter```s are the class for turning your response objects into
```SpeechletResponse``` types. But there are a lot of typical cases where you might want to make
some important decisions in your "view" layer. To facilitate this, there is the ```AbstractBundleFormatter```
class.

This is a class you can subclass to build some smarter ```Formatter``` types. There is also the 
"format" subproject, that includes a ```MustacheBundle``` you can use to template out your responses.
 
 Let's go back to our simple example and create the ```SearchFormatter```.
 
 ```java
 
    public class SearchFormatter extends AbstractBundleFormatter<List<String>> {
    
        public SearchFormatter(){
            // Create the formatter with our bundles
            super(new MustacheBundle("/search_results", "en"), new ConstantBundle("/reprompt", "en"));
        }
        public boolean isAskResponse(List<String> response, SpeechletRequest request, Session session){
            return response == null || response.isEmpty(); // <-- if the response was empty, we will
                                                           // ask the user for a new query.
        }
    
        public String getCardTitle(List<String> response , SpeechletRequest request, Session session){
            return response == null || response.isEmpty() ? null : // <-- If we don't find anything we
                "My Thing's Search Results";                       // don't need to put anything in the
                                                                   // user's timeline.
        }
        // The Card icons to use if we return a card.
        public String getCardLargeImage(List<String> response, SpeechletRequest request, Session session){
            return response == null || response.isEmpty() ? null : Constants.MY_LARGE_IMAGE_URL;
        }
        public String getCardSmallImage(List<String> response, SpeechletRequest request, Session session){
            return response == null || response.isEmpty() ? null : Constants.MY_SMALL_IMAGE_URL;
        }
        
    }
 
 ```
 
 Now we need some content. Since we are using the ```MustacheBundle```, we can create a file called 
 ```search_results.txt.mustache``` in our resources. This will be the file we format the results 
 list with:
 
 ```mustache
    {{#response.isEmpty}}
        I'm sorry. I couldn't find anything for that. Please try again.
    {{/response}}
    {{^response.isEmpty}}
        I found {{response.size}} things! They are:
        {{#response}}
            {{.}}
        {{/response}}
    {{/response}}
 ```
 
 Next, a template for our timeline card in ```search_resutls.card.mustache```
 
 ```mustache
    You found these things using My Search Thing:
    {{#response}}
        {{.}}
    {{/response}}
 
 ```
 
 And finally, ```reprompt.txt```. This is the ```ConstantBundle``` text file we will use to as
 a reprompt if we didn't find anything for the user.
 ```
    I'm sorry. I didn't catch that. Please try a new search now.
 ```
 
 