package eu.hurion.vaadin.heroku;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class MockApplicationListener implements ServletContextListener {
    private static boolean wasInvoked;
    private static boolean shouldBeInvoked;

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        wasInvoked = true;
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    }

    public static void setShouldBeInvoked(final boolean shouldBeInvoked){
        MockApplicationListener.shouldBeInvoked = shouldBeInvoked;
    }


    public static void verify(){
        if (shouldBeInvoked){
            assertTrue(wasInvoked, "Application listener should have been called");
        } else {
            assertFalse(wasInvoked, "Application listener should not have been called");
        }
    }
}
