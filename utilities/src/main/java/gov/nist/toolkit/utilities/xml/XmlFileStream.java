package gov.nist.toolkit.utilities.xml;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XmlFileStream {
    //		get the root element (in this case the envelope)
    OMElement omElement;
    FileReader fr;
    //		create the parser
    XMLStreamReader parser;

    public static XmlFileStream parse_xml(File infile) throws FactoryConfigurationError, XdsInternalException {

        XmlFileStream x = new XmlFileStream();

        try {
            x.fr = new FileReader(infile.getCanonicalFile());
            x.parser = XMLInputFactory.newInstance().createXMLStreamReader(x.fr);
        } catch (XMLStreamException e) {
            throw new XdsInternalException("Could not create XMLStreamReader from " + infile.getName());
        } catch (FileNotFoundException e) {
            throw new XdsInternalException("Could not find input file " + infile.getAbsolutePath());
        } catch (IOException e) {
            throw new XdsInternalException("Could not find input file " + infile.getAbsolutePath());
        }

        //		create the builder
        StAXOMBuilder builder = new StAXOMBuilder(x.parser);

        x.omElement =  builder.getDocumentElement();
        if (x.omElement == null)
            throw new XdsInternalException("No document element");

        return x;
    }

    public OMElement getOmElement() {
        return omElement;
    }

    public FileReader getFr() {
        return fr;
    }

    public XMLStreamReader getParser() {
        return parser;
    }
}
