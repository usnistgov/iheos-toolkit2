package gov.nist.toolkit.testkitutilities.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Organizes UI to gather information from user about a section (data input)
 */
public class Gather  implements Serializable, IsSerializable {
    private String prompt;
    private String input;

    public Gather() {
    }

    public Gather(String prompt, String input) {
        this.prompt = prompt;
        this.input = input;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
