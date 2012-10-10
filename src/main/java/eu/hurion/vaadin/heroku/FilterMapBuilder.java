package eu.hurion.vaadin.heroku;

import com.google.common.collect.Lists;
import org.apache.catalina.deploy.FilterMap;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for a {@link FilterMap}
 */
public class FilterMapBuilder {
    private final String filterName;
    private final List<String> servletNames = Lists.newArrayList();
    private final List<String> urlPatterns = Lists.newArrayList();
    private final List<DispatcherType> dispatcherMapping = Lists.newArrayList();

    private FilterMapBuilder(final String filterName) {
        this.filterName = filterName;
    }

    public static FilterMapBuilder mapFilter(final String filterName){
        return new FilterMapBuilder(filterName);
    }

    public FilterMapBuilder toServlet(final String... servletNames) {
        this.servletNames.addAll(Arrays.asList(servletNames));
        return this;
    }

    public FilterMapBuilder toUrlPattern(final String... urlPatterns) {
        this.urlPatterns.addAll(Arrays.asList(urlPatterns));
        return this;
    }

    public FilterMapBuilder withDispatcher(final DispatcherType... dispatcher) {
        this.dispatcherMapping.addAll(Arrays.asList(dispatcher));
        return this;
    }

    FilterMap build() {
        final FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(filterName);
        for (String servletName : servletNames) {
            filterMap.addServletName(servletName);
        }
        for (String urlPattern : urlPatterns) {
            filterMap.addURLPattern(urlPattern);
        }
        for (DispatcherType dispatcherType : dispatcherMapping) {
            filterMap.setDispatcher(dispatcherType.name());
        }
        return filterMap;
    }
}