Format
======

This project contains utilities for creating Formatters for Skillz. It allows you to package 
templates for formatting your text or SSML responses with Mustache.


Usage
-----


You can create a ```MustacheBundle``` and format your responses with Mustache(.java) templates. 
The bundle will look for ```[resourcePath].[languageCode].[format].mustache``` on the classpath
(```resourcePath``` should likely start with a "/" since it will be loaded relative to the
```MustacheBundle``` classpath).

If the given language code doesn't exist, it will look for a template without a language. Format can
be ```ssml```, ```txt```, or ```card```. Card will always be used to format your card content. If
```ssml``` is present, it will be used to format an ```SsmlSpeechOutput```. If that is not present,
the ```txt``` template will be used to create a ```PlainTextSpeechOutput``` response.

Once it has loaded you template resource, it will be applied using ```response```, ```request```, and
```session``` as root context objects. Response is the object that was returned from your intent or 
service method. Request is the SpeechletRequest Amazon sent to the request, and Session is the 
session attribute.

If you don't require remplating, consider using the ```ConstantBundle``` class in the core API 
project.

You can use these bundles in cooperation with the ```AbstractBundleFormatter``` to encode your 
responses as SpeechletResponses in pretty much any conceivable way. 

