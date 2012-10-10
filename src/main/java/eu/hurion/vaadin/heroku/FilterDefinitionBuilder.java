package eu.hurion.vaadin.heroku;

import com.google.common.collect.Maps;
import org.apache.catalina.deploy.FilterDef;

import javax.servlet.Filter;
import java.util.Map;

/**
 * Builder for a {@code FilterDef}
 */
public class FilterDefinitionBuilder {
    private String description;
    private String displayName;
    private Filter filter;
    private String filterClass;
    private final String filterName;
    private String largeIcon;
    private Map<String, String> parameters = Maps.newHashMap();
    private String smallIcon;
    private String asyncSupported;

    private FilterDefinitionBuilder(final String filterName) {
        this.filterName = filterName;
    }

    public static FilterDefinitionBuilder filterDefinition(final String filterName){
        return new FilterDefinitionBuilder(filterName);
    }

    public FilterDefinitionBuilder withDescription(final String description) {
        this.description = description;
        return this;
    }

    public FilterDefinitionBuilder withDisplayName(final String displayName) {
        this.displayName = displayName;
        return this;
    }

    public FilterDefinitionBuilder withFilter(final Filter filter) {
        this.filter = filter;
        return this;
    }

    public FilterDefinitionBuilder withFilterClass(final String filterClass) {
        this.filterClass = filterClass;
        return this;
    }

    public FilterDefinitionBuilder withFilterClass(final Class<?> filterClass){
        this.filterClass = filterClass.getName();
        return this;
    }

    public FilterDefinitionBuilder withLargeIcon(final String largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public FilterDefinitionBuilder withParameter(final String name, final String value) {
        if (parameters.containsKey(name)) {
            // The spec does not define this but the TCK expects the first
            // definition to take precedence
            return this;
        }
        this.parameters.put(name,value);
        return this;
    }

    public FilterDefinitionBuilder withSmallIcon(final String smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public FilterDefinitionBuilder withAsyncSupported(final String asyncSupported) {
        this.asyncSupported = asyncSupported;
        return this;
    }

    FilterDef build() {
        final FilterDef filterDef = new FilterDef();
        filterDef.setFilterName(filterName);
        filterDef.setFilter(filter);
        filterDef.setFilterClass(filterClass);
        filterDef.setAsyncSupported(asyncSupported);
        filterDef.setDescription(description);
        filterDef.setDisplayName(displayName);
        filterDef.setLargeIcon(largeIcon);
        filterDef.setSmallIcon(smallIcon);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            filterDef.addInitParameter(parameter.getKey(),parameter.getValue());
        }
        return filterDef;
    }
}