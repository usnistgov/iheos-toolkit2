package gov.nist.toolkit.errorrecording.client.assertions;

import gov.nist.toolkit.errorrecording.client.helpers.ResourceLoader;

import java.io.*;
import java.util.*;

/**
 * Created by diane on 9/30/2016.
 */
public class AssertionLoader {
    ResourceLoader RESOURCELOADER = ResourceLoader.getResourceLoader();

    public AssertionLoader(){

    }

    public Map<String, List<String>> loadAssertions() {

        // Load Toolkit Assertions from file
        InputStream is = RESOURCELOADER.getToolkitAssertions();

        String line = "";
        String cvsSplitBy = ",";
        List<String> list = new ArrayList<>();
        Map<String, List<String>> assertionsMap = new HashMap<>();

        // Populate assertions map
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip the first line (column names)
            br.readLine();

            // Process the rest
            while ((line = br.readLine()) != null) {

                String[] ta = line.split(cvsSplitBy);

                list.add(ta[1]); list.add(ta[2]); list.add(ta[3]);
                assertionsMap.put(ta[0], list);

                System.out.println("Toolkit Assertion [TA= " + ta[0] + " , Toolkit Error Message=" + ta[1] + " , Gazelle Scheme ID=" + ta[2] +
                        " , Gazelle Assertion ID=" + ta[3] + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assertionsMap;
    }

}
