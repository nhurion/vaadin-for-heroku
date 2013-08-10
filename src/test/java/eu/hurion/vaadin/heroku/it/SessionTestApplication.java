package eu.hurion.vaadin.heroku.it;


import com.vaadin.event.FieldEvents;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;

public class SessionTestApplication extends UI {

    public static final String BUTTON_CAPTION = "Click me";
    public static final String BUTTON_ID = "hello-button";
    public static final String NAME_LABEL = "What is your name?";
    public static final String NAME_ID = "name";
    public static final String NAME = "name-session";

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        setContent(buildContent());
    }

    private Layout buildContent() {
        final FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);
        formLayout.setSizeUndefined();

        final TextField nameInput = new TextField();
        nameInput.setCaption(NAME_LABEL);
        nameInput.setId(NAME_ID);
        nameInput.setImmediate(true);
        final VaadinSession session = getUI().getSession();
        final String name = (String) session.getAttribute(NAME);
        if (name != null){
            nameInput.setValue(name);
        }
        nameInput.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(final FieldEvents.TextChangeEvent event) {
                final String text = event.getText();
                session.setAttribute(NAME, text);
            }
        });
        formLayout.addComponent(nameInput);

        final Button showButton = new Button(BUTTON_CAPTION, new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent clickEvent) {
                final String greeting = "Hello " + nameInput.getValue() + " !";
                Notification.show(greeting);
            }
        });
        showButton.setId(BUTTON_ID);
        formLayout.addComponent(showButton);
        final VerticalLayout vl = new VerticalLayout();
        vl.addComponent(formLayout);
        vl.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
        vl.setSizeFull();
        return vl;
    }

}
