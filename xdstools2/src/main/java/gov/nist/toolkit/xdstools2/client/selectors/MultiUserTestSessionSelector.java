package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdstools2.client.command.command.IsTestSessionValidCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEventHandler;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildTestSessionCommand;

/**
 *
 */
public class MultiUserTestSessionSelector {
    HTML currentTestSession = new HTML();
    TextBox textBox = new TextBox();
    HorizontalPanel panel;
    boolean canChangeTs = false;
    boolean canCreateNewTs = false;
    boolean canDeleteTs = false;

    public MultiUserTestSessionSelector() {
        this(true,true,true);
        build();
        link();
    }

    public MultiUserTestSessionSelector(boolean canChangeTs, boolean canCreateNewTs, boolean canDeleteTs) {
        this.canChangeTs = canChangeTs;
        this.canCreateNewTs = canCreateNewTs;
        this.canDeleteTs = canDeleteTs;
    }

    // Listen on the EventBus in the future
    protected void link() {

        // Test sessions reloaded
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                for (String str : event.testSessionNames) {
                    currentTestSession.setText(str);
                    break;
                }
            }
        });


        // SELECT
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                if (event.getChangeType() == TestSessionChangedEvent.ChangeType.SELECT) {
                    currentTestSession.setText(event.getValue());
                }
            }
        });
    }

    // Initialize screen now
    protected void build() {

        panel = new HorizontalPanel();

        panel.add(new HTML("Test Session: "));

        panel.add(currentTestSession);
        panel.add(new HTML("&nbsp;"));

//        listBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent changeEvent) {
//                String newValue = listBox.getValue(listBox.getSelectedIndex());
//                if (NONSELECTION.equals(newValue)) return;
//                ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(newValue);
//                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, newValue));
//            }
//        });

        if (canChangeTs) {
            textBox.removeStyleName("testSessionInputMc");
            textBox.addStyleName("testSessionInputMc");
            panel.add(textBox);
            /*
            textBox.addKeyPressHandler(new KeyPressHandler()
            {
                @Override
                public void onKeyPress(KeyPressEvent event_)
                {
                    boolean enterPressed = KeyCodes.KEY_ENTER == event_
                            .getNativeEvent().getKeyCode();
                    if (enterPressed) {
                        String value = textBox.getValue().trim();
                        event_.preventDefault();
                        event_.stopPropagation();
                        change(value);
                    }
                }
            });
            */

            //
            // Change Test Session Button
            //
            Button changeTestSessionButton = new Button("Change");
            panel.add(changeTestSessionButton);
            changeTestSessionButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    String value = textBox.getValue().trim();
                    change(value);
                }
            });
            panel.add(new HTML("&nbsp;&nbsp;"));
        }


        if (canCreateNewTs) {
            //
            // Add Button
            //
            Button newTestSessionButton = new Button("Create New");
            panel.add(newTestSessionButton);
            newTestSessionButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    textBox.setValue("");
                    add();
                }
            });

        }

        if (canDeleteTs) {
            //
            // Delete Button
            //
            Button delTestSessionButton = new Button("Delete");
            panel.add(delTestSessionButton);
            delTestSessionButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    String value = currentTestSession.getText();
                    if (value!=null && !"".equals(value)) {
                        boolean answer = Window.confirm("Delete Test Session: " + value + "?");
                        if (answer) {
                            ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.DELETE, value));
                            currentTestSession.setText("");
                        }
                    }

                }
            });
        }


    }

    void change(final String testSession) {
        if (!"default".equals(testSession)) {
            CommandContext request = new CommandContext(null,testSession);
            new IsTestSessionValidCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Sorry.");
                }

                @Override
                public void onComplete(Boolean result) {
                    if (result) {
                        ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
                        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession));
                        textBox.setValue("");
                    } else {
                        textBox.setValue("");
                        new PopupMessage("Sorry.");
                    }
                }
            }.run(request);
        } else {
            textBox.setValue("");
            new PopupMessage("Sorry.");
        }

    }

    void add() {
        new BuildTestSessionCommand() {
            @Override
            public void onComplete(TestSession result) {
                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.ADD, result.toString()));
            }
        }.run(new CommandContext());
    }

    public Widget asWidget() { return panel; }

}
