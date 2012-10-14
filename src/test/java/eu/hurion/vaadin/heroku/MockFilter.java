package eu.hurion.vaadin.heroku;

import org.testng.Assert;

import javax.servlet.*;
import java.io.IOException;

public class MockFilter implements Filter {

    private boolean shouldBeInvoked;
    private boolean wasInvoked;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        this.wasInvoked = true;
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
    }

    public void setExpectedInvocation(final boolean shouldBeInvoked) {
        this.shouldBeInvoked = shouldBeInvoked;
    }

    public void verify(  ) {
        if (this.shouldBeInvoked) {
            Assert.assertTrue(this.wasInvoked, "Expected MockFilter to be invoked");
        } else {
            Assert.assertFalse(this.wasInvoked,"Expected MockFilter not to be invoked.");
        }
    }
}
