/**
 * 
 */
package edu.wustl.mir.erl.ihe.xdsi.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

/**
 * Helper class for xml namespace contexts for CDA Documents
 */
public class UtilNamespaceContext implements Serializable, NamespaceContext {
   private static final long serialVersionUID = 1L;

   QName[] gnames;

   /**
    * Create an instance implementing {@link NamespaceContext} using the passed
    * List of {@link QName}s.
    * 
    * @param namespaces QName list.
    */
   public UtilNamespaceContext(QName[] namespaces) {
      this.gnames = namespaces;
   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
    */
   @Override
   public String getNamespaceURI(String prefix) {
      for (QName qname : gnames) {
         if (qname.getPrefix().equals(prefix)) return qname.getNamespaceURI();
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
    */
   @Override
   public String getPrefix(String namespaceURI) {
      for (QName qname : gnames) {
         if (qname.getNamespaceURI().equals(namespaceURI))
            return qname.getPrefix();
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
    */
   @Override
   public Iterator<String> getPrefixes(String namespaceURI) {
      List <String> list = new ArrayList <>();
      for (QName qname : gnames) {
         if (qname.getNamespaceURI().equals(namespaceURI))
            list.add(qname.getPrefix());
      }
      return list.iterator();
   }

}
