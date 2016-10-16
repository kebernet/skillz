Skillz
======

_"You know, like nunchuck skills, bow hunting skills, computer hacking skills. 
Girls only like guys who have great skills."_


NOTICE
------

This project is currently under development and not intended for use.


Purpose
-------

The purpose of this project is to provide a simplified method for developing Amazon Alexa skills in
Java.

The Alexa Skills Kit provided by Amazon is fine, but it feels... less than modern... in the Java world.
Skillz provides a new method for developing Alexa skills built on top of the Amazon kit, but more in the
style of JAX-RS and other meta-data based frameworks.

Rather than implementing a Speechlet and configuring a servlet to handle it, you can simple annotate a class
and have the invocation of your methods handled by the Skillz framework. For example:

```
@Skill(path="/burnsallen")      // <- this is the path your skill will live at.
public class BurnsAndAllen {

    @Launched     // <- This method will be invoked upon launch.
    @ResponseFormatter(Formatters.SimplePlainTextTell.class) // <- Use an annotation to define how to format
                                                             // a SpeechletResponse, or register type formatters
                                                             // for your return classes.
    public String hello(){
        return "Gracie, come look!";
    }

    @Intent("GeorgeAndGracie")  //<- No more switching for events.
    @Utterances({
            "Say {greeting|goodnight}, {name|gracie}." // <- Keep all your metadata with your code!
    })
    @ResponseFormatter(Formatters.SimplePlainTextTell.class)
    public String say(
        @Slot(name="greeting", type="AMAZON.LITERAL") String greeting, //<- Get slots/values passed as parameters
        @Slot(name="name", type="AMAZON.LITERAL") String name){
        return greeting+", "+name;
    }
}
```





