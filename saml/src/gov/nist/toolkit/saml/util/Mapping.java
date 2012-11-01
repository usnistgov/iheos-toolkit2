package gov.nist.toolkit.saml.util;

import java.io.Serializable;

public class Mapping implements Serializable {
   
    private static final long serialVersionUID = 4598721541118599293L;
    private String namespaceURI;
    private int namespaceHash;

    private String prefix;
    private int prefixHash;

    public Mapping(String namespaceURI, String prefix) {
        setPrefix(prefix);
        setNamespaceURI(namespaceURI);
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public int getNamespaceHash() {
        return namespaceHash;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
        this.namespaceHash = namespaceURI.hashCode();
    }

    public String getPrefix() {
        return prefix;
    }

    public int getPrefixHash() {
        return prefixHash;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.prefixHash = prefix.hashCode();
    }

}

