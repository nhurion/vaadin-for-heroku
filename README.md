Vaadin for Heroku  [![Build Status](https://secure.travis-ci.org/nhurion/vaadin-for-heroku.png?branch=master)](http://travis-ci.org/nhurion/vaadin-for-heroku)
=================

Easily build a [Vaadin](https://vaadin.com/home) application and deploy it on [Heroku](http://www.heroku.com).

Uses [Embed for Vaadin](https://vaadin.com/directory#addon/embed-for-vaadin) to configure and start an embedded [tomcat](http://tomcat.apache.org/)
configured to start Vaadin and store session in memcached.

How to use it
-------------

Add the dependency to maven:

    <dependency>
      <groupId>eu.hurion.vaadin.heroku</groupId>
      <artifactId>vaadin-for-heroku</artifactId>
      <version>1.0</version>
    </dependency>

Then make a class with a main method to launch the server;

    public static void main(final String[] args) {
        herokuServer(forApplication(YourVaadinApplication.class)).start();
    }

By default, the configuration assume you're using the memcache add-on.
If you prefer using the memcachier add-on, use

    public static void main(final String[] args) {
        herokuServer(forApplication(YourVaadinApplication.class))
                .withMemcachedSessionManager(memcachierAddOn())
                .start();
    }

Finally, make a Procfile to point to you class

    web:    java $JAVA_OPTS -cp target/classes:target/dependency/* path.to.your.Launcher

For ease of development, another pre-configured server is available:

    public static void main(final String[] args) {
        localServer(forApplication(YourVaadinApplication.class)).start();
    }

This local server does not require memcached to be running/configured, launch on port 8080 and automatically open a browser.
Best way to use it is to make another launcher in your test sources and use it from your IDE.

See [hello-vaadin-heroku](https://github.com/nhurion/hello-vaadin-heroku) for a complete example.

Changelog
=========
1.0
---
* migrate to Vaadin 7.1.2

0.5
---
* fix of #2

0.4
---
* enforce consistent version of tomcat dependencies and psuh to version 7.0.29

0.3
---
* added possibility to define and map filter and application listener
* localServer and herokuServer now take a VaadinForHeroku, configure it for local/heroku and return it
* added testServer to configure a server for usage in "unit" tests.
* bumped to tomcat 7.0.29
