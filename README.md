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
      <version>0.2</version>
    </dependency>

Then make a class with a main method to launch the server;

    public static void main(final String[] args) {
        herokuServer(YourVaadinApplication.class).start();
    }

By default, the configuration assume you're using the memcache add-on.
If you prefer using the memcachier add-on, use

    public static void main(final String[] args) {
        herokuServer(YourVaadinApplication.class)
                .withMemcachedSessionManager(memcachierAddOn())
                .start();
    }

Finally, make a Procfile to point to you class

    web:    java $JAVA_OPTS -cp target/classes:target/dependency/* path.to.your.Launcher

For ease of development, another pre-configured server is available:

    public static void main(final String[] args) {
        localServer(YourVaadinApplication.class).start();
    }

This local server does not require memcached to be running/configured, launch on port 8080 and automatically open a browser.
Best way to use it is to make another launcher in your test sources and use it from your IDE.

See [hello-vaadin-heroku](https://github.com/nhurion/hello-vaadin-heroku) for a complete example.