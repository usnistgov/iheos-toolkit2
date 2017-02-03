package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 *
 */
public class ReportDTO implements Serializable, IsSerializable {
    private String name;
    private String value = "Unavailable";
    private String section;
    private String xpath;
    private String escapedCharsInXml;

    public ReportDTO() {
    }

    public ReportDTO(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();

        buf.append("Report ").append(name).append(" section=").append(section)
                .append(" value=").append(value);

        return buf.toString();
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getEscapedCharsInXml() {
        return escapedCharsInXml;
    }

    public void setEscapedCharsInXml(String escapedCharsInXml) {
        this.escapedCharsInXml = escapedCharsInXml;
    }
}
