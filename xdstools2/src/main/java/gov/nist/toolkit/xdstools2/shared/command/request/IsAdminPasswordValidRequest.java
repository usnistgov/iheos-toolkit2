package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class IsAdminPasswordValidRequest extends CommandContext{
    String passwordToValidate;

    public IsAdminPasswordValidRequest(){}
    public IsAdminPasswordValidRequest(CommandContext context, String passwordToValidate){
        copyFrom(context);
        this.passwordToValidate = passwordToValidate;
    }

    public String getPasswordToValidate() {
        return passwordToValidate;
    }

    public void setPasswordToValidate(String passwordToValidate) {
        this.passwordToValidate = passwordToValidate;
    }
}
