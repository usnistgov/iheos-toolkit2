package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.testkitutilities.client.ConfTestPropertyName;
import gov.nist.toolkit.xdsexception.client.TkNotFoundException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfTestPropertyLoader {
    public static final String PROPERTY_FILE_NAME = "ConfTest.properties";
    /**
     * Input state
     */
    private Input i;
    private class Input {
        File testDir;
    }
    /**
     * Output state
     */
    private Output o;
    private class Output {
        Properties properties = new Properties();
        Map<ConfTestPropertyName,String> map = new HashMap<>();
    }

    public ConfTestPropertyLoader(File testDir) {
        i = new Input();
        i.testDir = testDir;
        o = new Output();
    }

    public ConfTestPropertyLoader load() throws ToolkitRuntimeException {
        File file = new File(i.testDir, PROPERTY_FILE_NAME);
        try {
            if (file.exists())
                o.properties.load(new FileInputStream(file));
        } catch (Exception ex) {
            throw new ToolkitRuntimeException(" file: " + file.toString() + "\nException: " + ex.toString());
        }
        return this;
    }

    public ConfTestPropertyLoader map() throws TkNotFoundException {
        o.map.clear();
        for (final String name: o.properties.stringPropertyNames()) {
            ConfTestPropertyName cfPn = ConfTestPropertyName.find(name);
            if (cfPn!=null) {
                o.map.put(cfPn, o.properties.getProperty(name));
            } else {
              throw new TkNotFoundException("Could not find this name: " + name, "");
            }
        }
        return this;
    }

    public Map<ConfTestPropertyName,String> getMap() throws TkNotFoundException {
        if (o.map.isEmpty()) {
            map();
        }
        return o.map;
    }

}

