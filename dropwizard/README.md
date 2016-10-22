Skillz for Dropwizard
=====================

If you are developing "headless" applications, [Dropwizard](http://dropwizard.io) is a good option. To help you with
that, there is a Skillz ```Bundle``` to bootstrap your application quickly.

Getting Started
---------------


 1.  Add the bundle to your bootstrap.
 
     ```java 
        
         @Override
         public void initialize(Bootstrap<Configuration> bootstrap) {
             super.initialize(bootstrap);
             SkillzBundle bundle = new SkillzBundle();
             bootstrap.addBundle(bundle);
         }
     ```
     
 2.  There is no step 2.
 
 
 You can, of course configure the bundle with ```FormatterMappings```, A ```TypeFactory```, 
 or change the base bath of the skills you deploy. But if you just want to get started 
 quickly, this is all you need.
 
 