package eu.hurion.vaadin.heroku;

import com.vaadin.Application;
import com.vaadin.ui.*;

public class SessionTestApplication extends Application {

    public static final String BUTTON_CAPTION = "Click me";
    public static final String BUTTON_ID = "hello-button";
    public static final String NAME_LABEL = "What is your name?";
    public static final String NAME_ID = "name";

    @Override
    public void init() {
        setMainWindow(buildContent());
    }

    private Window buildContent() {
        final FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);
        formLayout.setSizeUndefined();

        final TextField nameInput = new TextField();
        nameInput.setCaption(NAME_LABEL);
        nameInput.setDebugId(NAME_ID);
        nameInput.setImmediate(true);
        formLayout.addComponent(nameInput);

        final Button showButton = new Button(BUTTON_CAPTION, new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
//                final String greeting = HELLO_WORLD + nameInput.getValue() + " !";
//                getMainWindow().showNotification(greeting, "");
            }
        });
        showButton.setDebugId(BUTTON_ID);
        formLayout.addComponent(showButton);
        final VerticalLayout vl = new VerticalLayout();
        vl.addComponent(formLayout);
        vl.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
        vl.setSizeFull();
        final Window window = new Window();
        window.setContent(vl);

        return window;
    }

}
