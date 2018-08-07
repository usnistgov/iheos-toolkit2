package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
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
public class MultiUserTestSessionSelector extends ConfirmTestSessionChange {
    private HTML currentTestSession = new HTML("");
    private TextBox textBox = new TextBox();
    private HorizontalPanel panel;
    private boolean canChangeTs = false;
    private boolean canCreateNewTs = false;
    private boolean canDeleteTs = false;
    private boolean canExitTs = true;
    private String userMode;
    private Button changeTestSessionButton = new Button("Change");
    private Button delTestSessionButton = new Button("Delete");
    private Button exitTestSessionButton = new Button("Exit");

    public MultiUserTestSessionSelector(TabWatcher tabWatcher) {
        this(true,true,true,"Multiuser");
        this.tabWatcher = tabWatcher;
        build();
        link();
    }

    public MultiUserTestSessionSelector(boolean canChangeTs, boolean canCreateNewTs, boolean canDeleteTs, String userMode) {
        super(null);
        this.canChangeTs = canChangeTs;
        this.canCreateNewTs = canCreateNewTs;
        this.canDeleteTs = canDeleteTs;
        this.userMode = userMode;
    }

    // Initialize screen now
    protected void build() {

        panel = new HorizontalPanel();

        panel.add(new HTML("Test Session:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));

        panel.add(currentTestSession);

        panel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));


        if (canDeleteTs) {
            buildDelete();
        }
        if (canChangeTs) {
            buildChange();
        }
        if (canExitTs) {
            buildExit();
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
                    if (event.getValue()!=null && !"".equals(event.getValue()))
                        currentTestSession.setText(event.getValue());
                    else {
                        currentTestSession.setText(Xdstools2.getInstance().defaultTestSession);
                    }
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
        delTestSessionButton.getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
        panel.add(delTestSessionButton);
        delTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                final String value = currentTestSession.getText();
                if (value == null)
                    return;
                if (value.equals(Xdstools2.getInstance().defaultTestSession))
                    return;
                if (!"".equals(value)) {

                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" title=\"Delete\" height=\"16\" width=\"16\"/>");
                    safeHtmlBuilder.appendHtmlConstant("Confirm Delete Test Session " + currentTestSession.getText());

                    VerticalPanel body = new VerticalPanel();
                    body.add(new HTML("<p>Delete test session?<br/></p>"));

                    Button actionBtn =  new Button("Ok");
                    actionBtn.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            doChange(Xdstools2.getInstance().defaultTestSession);
                            ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.DELETE, value));
//                            String ts = Xdstools2.getInstance().defaultTestSession;
//                            if (ts.equals(""))
//                                ts = "None.";
                            setDeleteAccess();
                            Xdstools2.getInstance().getTestSessionManager().delete(value);
                            doChange(Xdstools2.getInstance().defaultTestSession);
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
                        if (!PasswordManagement.isSignedIn)
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

    private void buildExit() {
        //
        // Exit Button
        //
        Button exitTestSessionButton = new Button("Exit");
        panel.add(exitTestSessionButton);
        exitTestSessionButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                String current = currentTestSession.getText();
                if (current.equals(Xdstools2.getInstance().defaultTestSession))
                    return;
                if (!"".equals(current) && !"None.".equals(current)) {
                    VerticalPanel body = new VerticalPanel();
                    String alertMessage = "";
                    if ((tabWatcher!=null && tabWatcher.getTabCount()>0)) {
                        alertMessage = "<b>Note</b>: This action will close " + tabWatcher.getTabCount() + " tab(s).";
                    }
                    body.add(new HTML("<p>Exit test session?<br/>"
                            + alertMessage
                            + "</p>"));

                    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                    if (!"".equals(alertMessage)) {
                        safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/ic_announcement_black_36dp_1x.png\" title=\"Alert\" height=\"16\" width=\"16\"/>&nbsp;");
                    }
                    safeHtmlBuilder.appendHtmlConstant("Exit Test Session");

                    Button actionBtn =  new Button("Ok");
                    actionBtn.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            doChange(Xdstools2.getInstance().defaultTestSession);
                        }
                    });
                    new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionBtn);
                } else {
                    doChange(Xdstools2.getInstance().defaultTestSession);
                }
            }
        });

        panel.add(new HTML("&nbsp;&nbsp;"));

    }

    public void exitTestSession() {
        if (currentTestSession.equals(Xdstools2.getInstance().defaultTestSession))
            return;
        doChange(Xdstools2.getInstance().defaultTestSession);
    }

    public void change(final String testSession) {
        String current = currentTestSession.getText();
        if (current.equals("") || (testSession!=null && testSession.equals(current)))
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
                        // Alert only when switching from one test session to another but not when going from no-test-session to a test session (initial state).
                        if (!"".equals(currentTestSession.getText()) && !"None.".equals(currentTestSession.getText()) && !PasswordManagement.isSignedIn) {
                           confirm(testSession);
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

    private void setDeleteAccess() {
        if (canDeleteTs) {
            delTestSessionButton.setEnabled(!"None.".equals(currentTestSession.getText()));
        }
    }

    protected void setChangeAccess() {
        changeTestSessionButton.setEnabled(textBox.getValue().length()>0 && !textBox.getValue().equals(currentTestSession.getText()));
    }

    public void doChange(String testSession) {
//        if (Xdstools2.getInstance().defaultTestSession != null)
//            textBox.setValue(Xdstools2.getInstance().defaultTestSession);
//        else
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
                if (!PasswordManagement.isSignedIn)
                    tabWatcher.closeAllTabs();
            }
        }.run(new CommandContext());
    }

    public void reload() {
        Xdstools2.getInstance().getTestSessionManager().load("");
    }

    public Widget asWidget() { return panel; }

}
