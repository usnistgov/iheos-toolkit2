package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by skb1 on 9/20/2016.
 */
public class TestPartFileDTO implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;

    String partName;
    TestPartFileType partType;
    String file;
    String content;
    String htlmizedContent;
    List<String> stepList = new ArrayList<>();
    Map<String,TestPartFileDTO> stepTpfMap = new HashMap<String,TestPartFileDTO>();
    List<TestPartFileDTO> contentBundle;


    public enum TestPartFileType {
        SECTION_TESTPLAN_FILE,
        STEP_METADATA_FILE,
        CONTENTBUNDLE
    }

    public TestPartFileDTO() {
    }

    public TestPartFileDTO(TestPartFileType partType) {
        this.partType = partType;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public TestPartFileType getPartType() {
        return partType;
    }

    public void setPartType(TestPartFileType partType) {
        this.partType = partType;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtlmizedContent() {
        return htlmizedContent;
    }

    public void setHtlmizedContent(String htlmizedContent) {
        this.htlmizedContent = htlmizedContent;
    }

    public List<String> getStepList() {
        return stepList;
    }

    public void setStepList(List<String> stepList) {
        this.stepList = stepList;
    }

    public Map<String, TestPartFileDTO> getStepTpfMap() {
        return stepTpfMap;
    }

    public void setStepTpfMap(Map<String, TestPartFileDTO> stepTpfMap) {
        this.stepTpfMap = stepTpfMap;
    }

    public List<TestPartFileDTO> getContentBundle() {
        return contentBundle;
    }

    public void setContentBundle(List<TestPartFileDTO> contentBundle) {
        this.contentBundle = contentBundle;
    }
}
