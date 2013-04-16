package eu.hurion.vaadin.heroku;

import org.apache.catalina.deploy.FilterMap;
import org.testng.annotations.Test;

import javax.servlet.DispatcherType;

import static eu.hurion.vaadin.heroku.FilterMapBuilder.mapFilter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FilterMapBuilderTest {

    public static final String FILTER_NAME = "test_filter";

    @Test
    public void filterName(){
        final FilterMap testFilter = mapFilter(FILTER_NAME).build();
        assertThat(FILTER_NAME, is(testFilter.getFilterName()));
    }

    @Test
    public void mapToUrl(){
        final FilterMap testFilter = mapFilter(FILTER_NAME).toUrlPattern("/*", "/test/*")
                                                           .toUrlPattern("/prod/*").build();
        assertThat(testFilter.getURLPatterns()[0], is("/*"));
        assertThat(testFilter.getURLPatterns()[1], is("/test/*"));
        assertThat(testFilter.getURLPatterns()[2], is("/prod/*"));
    }

    @Test
    public void mapToServlet(){
        final FilterMap testFilter = mapFilter(FILTER_NAME).toServlet("servlet1", "servlet2")
                                                           .toServlet("servlet3").build();
        assertThat(testFilter.getServletNames()[0], is("servlet1"));
        assertThat(testFilter.getServletNames()[1], is("servlet2"));
        assertThat(testFilter.getServletNames()[2], is("servlet3"));
    }

    @Test
    public void dispatcherType(){
        final FilterMap testFilter = mapFilter(FILTER_NAME).withDispatcher(DispatcherType.INCLUDE)
                                                           .withDispatcher(DispatcherType.REQUEST).build();
        assertThat(testFilter.getDispatcherNames()[0], is(DispatcherType.INCLUDE.name()));
        assertThat(testFilter.getDispatcherNames()[1], is(DispatcherType.REQUEST.name()));

    }
}
