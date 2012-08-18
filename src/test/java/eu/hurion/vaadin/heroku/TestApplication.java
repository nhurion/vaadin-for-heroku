package eu.hurion.vaadin.heroku;

import com.vaadin.Application;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class TestApplication extends Application {

    public static final String TEST_LABEL = "test";
    public static final String TEST_LABEL_ID = "test-label";

    @Override
    public void init() {
        final Window window = new Window();
        setMainWindow(window);
        window.setContent(buildContent());
    }

    private ComponentContainer buildContent() {
        final VerticalLayout layout = new VerticalLayout();
        final Label testLabel = new Label(TEST_LABEL);
        testLabel.setDebugId(TEST_LABEL_ID);
        layout.addComponent(testLabel);
        return layout;
    }
}
