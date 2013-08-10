package eu.hurion.vaadin.heroku;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TestApplication extends UI {

    public static final String TEST_LABEL = "test";
    public static final String TEST_LABEL_ID = "test-label";

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        setContent(buildContent());
    }

    private ComponentContainer buildContent() {
        final VerticalLayout layout = new VerticalLayout();
        final Label testLabel = new Label(TEST_LABEL);
        testLabel.setId(TEST_LABEL_ID);
        layout.addComponent(testLabel);
        return layout;
    }
}
