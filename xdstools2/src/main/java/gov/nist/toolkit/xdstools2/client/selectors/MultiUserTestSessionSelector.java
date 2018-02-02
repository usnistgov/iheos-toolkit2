package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.command.command.IsTestSessionValidCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionsUpdatedEventHandler;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.TabWatcher;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildTestSessionCommand;

/**
 *
 */
public class MultiUserTestSessionSelector {
    HTML currentTestSession = new HTML("");
    TextBox textBox = new TextBox();
    HorizontalPanel panel;
    boolean canChangeTs = false;
    boolean canCreateNewTs = false;
    boolean canDeleteTs = false;
    String userMode;
    Button changeTestSessionButton = new Button("Change");
    Button delTestSessionButton = new Button("Delete");
    TabWatcher tabWatcher;

    public MultiUserTestSessionSelector(TabWatcher tabWatcher) {
        this(true,true,true,"Multiuser");
        this.tabWatcher = tabWatcher;
        currentTestSession.setText("None.");
        build();
        link();
    }

    public MultiUserTestSessionSelector(boolean canChangeTs, boolean canCreateNewTs, boolean canDeleteTs, String userMode) {
        this.canChangeTs = canChangeTs;
        this.canCreateNewTs = canCreateNewTs;
        this.canDeleteTs = canDeleteTs;
        this.userMode = userMode;
    }

    // Initialize screen now
    protected void build() {

        panel = new HorizontalPanel();

        panel.add(new HTML("Test Session: "));

        panel.add(currentTestSession);

        if (canDeleteTs) {
            buildDelete();
        }
        if (canChangeTs) {
            buildChange();
        }
        if (canCreateNewTs) {
            buildNew();
        }
    }

    // Listen on the EventBus in the future
    protected void link() {

        // Test sessions reloaded
        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionsUpdatedEvent.TYPE, new TestSessionsUpdatedEventHandler() {
            @Override
            public void onTestSessionsUpdated(TestSessionsUpdatedEvent event) {
                for (String str : event.testSessionNames) {
                    currentTestSession.setText(str);
                    setDeleteAccess();
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
                    setDeleteAccess();
                }
            }
        });
    }

    private void buildDelete() {
//        panel.add(new HTML("&nbsp;"));
        //
        // Delete Button
        //
        setDeleteAccess();
        panel.add(delTestSessionButton);
        delTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final String value = currentTestSession.getText();
                if (value!=null && !"".equals(value)) {

                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" title=\"Delete\" height=\"16\" width=\"16\"/>");
                    safeHtmlBuilder.appendHtmlConstant("Confirm Delete Test Session " + currentTestSession.getText());

                    VerticalPanel body = new VerticalPanel();
                    body.add(new HTML("<p>Delete test session?<br/></p>"));

                    Button actionBtn =  new Button("Ok");
                    actionBtn.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            doChange("");
                            ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.DELETE, value));
                            currentTestSession.setText("None.");
                            setDeleteAccess();
                        }
                    });
                    new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionBtn);

                }

            }
        });
        panel.add(new HTML("&nbsp;"));
    }


    private void buildChange() {
        panel.add(new HTML("&nbsp;"));
        textBox.removeStyleName("testSessionInputMc");
        textBox.addStyleName("testSessionInputMc");
        panel.add(textBox);

        textBox.addKeyUpHandler(new KeyUpHandler() {
                                    @Override
                                    public void onKeyUp(KeyUpEvent keyUpEvent) {
                                        setChangeAccess();
                                    }
                                });
        textBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (KeyCodes.KEY_ENTER == keyUpEvent.getNativeKeyCode()) {
                    keyUpEvent.getNativeEvent().preventDefault();
                    keyUpEvent.getNativeEvent().stopPropagation();
                    textBox.cancelKey();
                    String value = textBox.getValue().trim();
                    change(value);
                }
            }
        });

        //
        // Change Test Session Button
        //
        setChangeAccess();
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

    private void buildNew() {
        //
        // Add Button
        //
        Button newTestSessionButton = new Button("Create New");
        panel.add(newTestSessionButton);
        newTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (!"".equals(currentTestSession.getText()) && !"None.".equals(currentTestSession.getText())) {
                    VerticalPanel body = new VerticalPanel();
                    String alertMessage = "";
                    if ((tabWatcher!=null && tabWatcher.getTabCount()>0)) {
                        alertMessage = "<b>Note</b>: This action will close " + tabWatcher.getTabCount() + " tab(s).";
                    }
                    body.add(new HTML("<p>Create a new test session?<br/>"
                            + alertMessage
                            + "</p>"));

                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    if (!"".equals(alertMessage)) {
                        safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/ic_announcement_black_36dp_1x.png\" title=\"Alert\" height=\"16\" width=\"16\"/>&nbsp;");
                    }
                    safeHtmlBuilder.appendHtmlConstant("New Test Session");

                    Button actionBtn =  new Button("Ok");
                    actionBtn.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            doNew();
                        }
                    });
                    new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionBtn);
                } else {
                    doNew();
                }
            }
        });
    }

    private void change(final String testSession) {
        if (testSession!=null && testSession.equals(currentTestSession.getText()))
            return;

        if (!"default".equalsIgnoreCase(testSession) || PasswordManagement.isSignedIn) {
            CommandContext request = new CommandContext(null,testSession);
            new IsTestSessionValidCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Change test session command failed: " + throwable.toString());
                }

                @Override
                public void onComplete(Boolean result) {
                    if (result) {
                        if (!"".equals(currentTestSession.getText()) && !"None.".equals(currentTestSession.getText()) && !PasswordManagement.isSignedIn) {
                            VerticalPanel body = new VerticalPanel();
                            String alertMessage = "";
                            if ((tabWatcher!=null && tabWatcher.getTabCount()>0)) {
                                alertMessage = "<b>Note</b>: This action will close " + tabWatcher.getTabCount() + " tab(s).";
                            }
                            body.add(new HTML("<p>Change test session?<br/>"
                                    + alertMessage
                                    + "</p>"));

                            SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                            if (!"".equals(alertMessage)) {
                                safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/ic_announcement_black_36dp_1x.png\" title=\"Alert\" height=\"16\" width=\"16\"/>&nbsp;");
                            }
                            safeHtmlBuilder.appendHtmlConstant("Confirm Change Test Session to " + testSession);

                            Button actionBtn =  new Button("Ok");
                            actionBtn.addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent clickEvent) {
                                    doChange(testSession);
                                }
                            });
                            new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionBtn);
                        } else {
                            doChange(testSession);
                        }
                    } else {
                        new PopupMessage("Test session cannot be selected.");
                    }
                }
            }.run(request);
        } else {
            new PopupMessage("Test session default cannot be selected in " + userMode + " mode.");
            textBox.setValue("");
            setChangeAccess();
        }

    }

    protected void setDeleteAccess() {
        if (canDeleteTs) {
            delTestSessionButton.setEnabled(!"None.".equals(currentTestSession.getText()));
        }
    }

    protected void setChangeAccess() {
        changeTestSessionButton.setEnabled(textBox.getValue().length()>0 && !textBox.getValue().equals(currentTestSession.getText()));
    }

    private void doChange(String testSession) {
        textBox.setValue("");
        setChangeAccess();
        if (!PasswordManagement.isSignedIn)
            tabWatcher.closeAllTabs();
        ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession));
    }

    private void doNew() {
        textBox.setValue("");
        setChangeAccess();
        add();
    }



    private void add() {
        new BuildTestSessionCommand() {
            @Override
            public void onComplete(TestSession result) {
                ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.ADD, result.toString()));
                tabWatcher.closeAllTabs();
            }
        }.run(new CommandContext());
    }

    public Widget asWidget() { return panel; }

}
