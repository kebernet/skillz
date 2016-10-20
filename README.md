Skillz
======

_"You know, like nunchuck skills, bow hunting skills, computer hacking skills. 
Girls only like guys who have great skills."_

[![Build Status](https://travis-ci.org/kebernet/skillz.svg?branch=master)](https://travis-ci.org/kebernet/skillz)


NOTICE
------

This project is currently under development and not intended for use. 

Purpose
-------

The purpose of this project is to provide a simplified method for developing Amazon Alexa skills in
Java.

The Alexa Skills Kit provided by Amazon is fine, but it feels... less than modern... in the Java world.
Skillz provides a new method for developing Alexa skills built on top of the Amazon kit, but more in the
style of JAX-RS and other meta-data based frameworks. This makes skills easier to develop, and easier to
test!

Rather than implementing a Speechlet and configuring a servlet to handle it, you can simple annotate a class
and have the invocation of your methods handled by the Skillz framework. For example:

```java
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
            "Say {greeting|goodnight}, {name}." // <- Keep all your metadata with your code!
    })
    @ResponseFormatter(Formatters.SimplePlainTextTell.class)
    public String say(
        @Slot(name="greeting", type=AmazonSlotTypes.LITERAL) String greeting, //<- Get slots/values passed as parameters
        @Slot(name="name", type=AmazonSlotTypes.UnitedStates.FIRST_NAME) String name){
        return greeting+", "+name;
    }
}
```

First Steps
-----------

See the [API subproject docs](api/README.md) for help getting started.

If you want to quickstart a [DropWizard project](dropwizard/README.md) you can
look at the module for Skillz.

Notes
-----

Rather than a re-implementation of the Alexa Skills Kit, Skillz is built as a layer on top
of it. This was done with the belief that it would mean that it would be easier to support
future changes to the base API without breaking existing skills. 





