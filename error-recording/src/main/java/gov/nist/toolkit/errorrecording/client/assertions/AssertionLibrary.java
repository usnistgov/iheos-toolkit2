package gov.nist.toolkit.errorrecording.client.assertions;

import gov.nist.toolkit.errorrecording.client.helpers.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by diane on 10/13/2016.
 */
public class AssertionLibrary extends ArrayList<Assertion> {
    private ResourceLoader RESOURCELOADER = ResourceLoader.getResourceLoader();
    private static AssertionLibrary instance = null;
    private ArrayList<Assertion> library;

    /**
     * Create an AssertionLibrary from a List of Assertions
     */
    protected AssertionLibrary(){
        loadAssertions();
    }

    public static AssertionLibrary getInstance(){
        if (instance == null){
            instance = new AssertionLibrary();
        }
        return instance;
    }


    /**
     * Specify the Assertions file location (for example, for testing purposes)
     * @param fileLocation
     * @return
     */
    public void loadAssertions(String fileLocation) {
        InputStream is = RESOURCELOADER.getFileAsInputStream(fileLocation);
        loadAssertions(is);
    }

    /**
     * Load the Assertions file from hardcoded location (default mode)
     * @return
     */
    public void loadAssertions() {
        InputStream is = RESOURCELOADER.getToolkitAssertions();
        loadAssertions(is);
    }

    private void loadAssertions(InputStream is) {
        String line = "";
        String cvsSplitBy = ",";

        // Reset the Library
        this.clear();

        // Populate assertions library
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip the first line (column names)
            br.readLine();

            // Process the rest
            Assertion temp;
            while ((line = br.readLine()) != null) {

                // Split with a limit of 5 cells, which allows to keep trailing empty cells
                String[] ta = line.split(cvsSplitBy, 5);

                System.out.println("Toolkit Assertion [TA= " + ta[0] + " , Toolkit Error Message=" + ta[1] +
                        " , Location=" + ta[2] + " , Gazelle Scheme ID=" + ta[3] +
                        " , Gazelle Assertion ID=" + ta[4] + "]");

                temp = new Assertion(ta[0], ta[1], ta[2], ta[3], ta[4]);
                this.add(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return an Assertion based on its ID
     * @param assertionID
     * @return
     */
    public Assertion getAssertion(String assertionID){
        int index = library.indexOf(assertionID);
        return library.get(index);
    }

}
