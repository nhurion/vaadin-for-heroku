package eu.hurion.vaadin.heroku;

import org.apache.catalina.deploy.FilterDef;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FilterDefinitionBuilderTest {

    public static final String FILTER_NAME = "test";


    private FilterDefinitionBuilder baseFilterDefinition() {
        return FilterDefinitionBuilder.filterDefinition(FILTER_NAME);
    }


    @Test(expectedExceptions = RuntimeException.class)
    public void filterNameCannotBeNull() {
        FilterDefinitionBuilder.filterDefinition(null);
    }

    @Test
    public void filterNameIsSet() {
        final FilterDef filter = FilterDefinitionBuilder.filterDefinition(FILTER_NAME).build();
        assertThat(filter.getFilterName(), is(FILTER_NAME));
    }

    @Test
    public void descriptionIsSet() {
        final String description = "test filter description";
        final FilterDef filter = baseFilterDefinition()
                .withDescription(description).build();
        assertThat(filter.getDescription(), is(description));
    }

    @Test
    public void displayNameIsSet() {
        final String displayName = "test filter";
        final FilterDef filter = baseFilterDefinition()
                .withDisplayName(displayName).build();
        assertThat(filter.getDisplayName(), is(displayName));
    }

    @Test
    public void filterClass() {
        final FilterDef filter = baseFilterDefinition().
                withFilterClass(MockFilter.class).build();
        assertThat(filter.getFilterClass(), is("eu.hurion.vaadin.heroku.MockFilter"));
    }

    @Test
    public void filterClassAsString() {
        final FilterDef filter = baseFilterDefinition()
                .withFilterClass("eu.hurion.vaadin.heroku.MockFilter").build();
        assertThat(filter.getFilterClass(), is("eu.hurion.vaadin.heroku.MockFilter"));
    }

    @Test
    public void withFilterInstance() {
        final MockFilter mockFilter = new MockFilter();
        final FilterDef filter = baseFilterDefinition()
                .withFilter(mockFilter).build();
        assertThat(filter.getFilter().equals(mockFilter), is(true));
    }

    @Test
    public void parametersIsSet() {
        final FilterDef filter = baseFilterDefinition()
                .withParameter("test_param", "test_param_value").build();
        assertThat(filter.getParameterMap().get("test_param"), is("test_param_value"));
    }

    @Test
    public void onceAParameterIsSetItCannotBeChanged() {
        final FilterDef filter = baseFilterDefinition()
                .withParameter("test_param", "test_param_value")
                .withParameter("test_param", "other_value").build();
        assertThat(filter.getParameterMap().get("test_param"), is("test_param_value"));
    }

    @Test
    public void smallIconIsSet() {
        final FilterDef filter = baseFilterDefinition()
                .withSmallIcon("path_to_small_icon").build();
        assertThat(filter.getSmallIcon(), is("path_to_small_icon"));
    }

    @Test
    public void largeIconIsSet() {
        final FilterDef filter = baseFilterDefinition()
                .withLargeIcon("path_to_large_icon").build();
        assertThat(filter.getLargeIcon(), is("path_to_large_icon"));
    }

    @Test
    public void asyncSupported() {
        final FilterDef filter = baseFilterDefinition()
                .supportAsync().build();
        assertThat(filter.getAsyncSupported(), is("true"));
    }

    @Test
    public void asyncNotSupportedByDefault() {
        final FilterDef filter = baseFilterDefinition().build();
        assertThat(filter.getAsyncSupported(), is("false"));
    }
}
