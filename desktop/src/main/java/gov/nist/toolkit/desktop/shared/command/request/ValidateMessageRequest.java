package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.desktop.client.commands.util.CommandContext;
import gov.nist.toolkit.valsupport.client.ValidationContext;

/**
 * Created by onh2 on 10/21/16.
 */
public class ValidateMessageRequest extends CommandContext {
    private ValidationContext validationContext;

    public ValidateMessageRequest() {}

    public ValidateMessageRequest(CommandContext commandContext, ValidationContext vc) {
        copyFrom(commandContext);
        this.validationContext=vc;
    }

    public ValidationContext getValidationContext() {
        return validationContext;
    }

    public void setValidationContext(ValidationContext validationContext) {
        this.validationContext = validationContext;
    }
}
