package pl.mpak.usedb.br;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import pl.mpak.usedb.UseDBException;
import pl.mpak.util.StringUtil;
import pl.mpak.util.variant.VariantException;
import pl.mpak.util.variant.VariantType;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class XMLUtils {

  // XML loading and saving methods for BufferedRecord

  // The required DTD URI for exported properties
  private static final String BR_DTD_URI = "http://orbada.sourceforge.net/usedb/br.dtd";
  private static final String BR_XSL_URI = "http://orbada.sourceforge.net/usedb/br.xsl";
  private static final String BINARY_DATA_TYPE = "binary.base64";

  private static final String BR_DTD =
      "<!-- DTD for BufferedRecord -->"
      + "<!ELEMENT record ( comment?, entry* ) >"
      + "<!ATTLIST record version CDATA #FIXED \"1.0\">"
      + "<!ATTLIST record key-columns CDATA \"\">"
      + "<!ATTLIST record table-name CDATA \"\">"
      + "<!ELEMENT comment (#PCDATA) >"
      + "<!ELEMENT entry (#PCDATA) >"
      + "<!ATTLIST entry field CDATA #REQUIRED>"
      + "<!ATTLIST entry dt CDATA \"\">";

  /**
   * Version number for the format of exported properties files.
   */
  private static final String EXTERNAL_XML_VERSION = "1.0";

  static void load(BufferedRecord br, InputStream in) throws IOException, UseDBException {
    Document doc = null;
    try {
      doc = getLoadingDoc(in);
    } catch (SAXException saxe) {
      throw new InvalidPropertiesFormatException(saxe);
    }
    Node brElement = doc.getChildNodes().item(1);
    String xmlVersion = EXTERNAL_XML_VERSION;
    if (xmlVersion.compareTo(EXTERNAL_XML_VERSION) > 0)
      throw new InvalidPropertiesFormatException(
          "Exported BufferedRecord file format version " + xmlVersion
              + " is not supported. This mPak.UseDb installation can read"
              + " versions " + EXTERNAL_XML_VERSION + " or older. You"
              + " may need to install a newer version of mPak.UseDB.");
    importBufferedRecord(br, brElement);
  }

  static Document getLoadingDoc(InputStream in) throws SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setIgnoringElementContentWhitespace(true);
    dbf.setValidating(true);
    dbf.setCoalescing(true);
    dbf.setIgnoringComments(true);
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      db.setEntityResolver(new Resolver());
      db.setErrorHandler(new EH());
      InputSource is = new InputSource(in);
      return db.parse(is);
    } catch (ParserConfigurationException x) {
      throw new Error(x);
    }
  }

  static void importBufferedRecord(BufferedRecord br, Node propertiesElement) throws UseDBException {
    NodeList entries = propertiesElement.getChildNodes();
    int numEntries = entries.getLength();
    int start = numEntries > 0 && entries.item(0).getNodeName().equals("comment") ? 1 : 0;
    for (int i = start; i < numEntries; i++) {
      Element entry = (Element) entries.item(i);
      if (entry.hasAttribute("field")) {
        Node n = entry.getFirstChild();
        String val = (n == null) ? null : n.getNodeValue();
        if (val != null && !"".equals(val)) {
          BufferedRecordField field = br.fieldByName(entry.getAttribute("field"));
          if (field != null) {
            try {
              if (entry.hasAttribute("dt")) {
                if (StringUtil.equals(entry.getAttribute("dt"), BINARY_DATA_TYPE)) {
                  field.setBinary(new BASE64Decoder().decodeBuffer(val));
                }
                else {
                  field.setString(val);
                  field.getValue().cast(field.getValueType());
                }
              }
              else {
                field.setString(val);
                field.getValue().cast(field.getValueType());
              }
            } catch (Exception e) {
              throw new UseDBException(e);
            }
          }
        }
      }
    }
  }

  static void save(BufferedRecord br, OutputStream os, String comment, String encoding) throws IOException, DOMException, VariantException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
      db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException pce) {
      assert (false);
    }
    Document doc = db.newDocument();
    Element record = (Element)doc.appendChild(doc.createElement("record"));
    record.setAttribute("table-name", br.getTableName());
    StringBuilder keyColumns = new StringBuilder();
    for (BufferedRecordField field : br.getPrimaryKeyFields()) {
      if (keyColumns.length() > 0) {
        keyColumns.append(",");
      }
      keyColumns.append(field.getFieldName());
    }
    record.setAttribute("key-columns", keyColumns.toString());

    if (comment != null) {
      Element comments = (Element) record.appendChild(doc.createElement("comment"));
      comments.appendChild(doc.createTextNode(comment));
    }

    Iterator<BufferedRecordField> i = br.fieldList.iterator();
    while (i.hasNext()) {
      BufferedRecordField field = i.next();
      Element entry = (Element) record.appendChild(doc.createElement("entry"));
      entry.setAttribute("field", field.getFieldName());
      if (field.getValue().getValueType() == VariantType.varBinary) {
        entry.setAttribute("dt", BINARY_DATA_TYPE);
        try {
          entry.appendChild(doc.createCDATASection(new BASE64Encoder().encode(field.getValue().getBinary())));
        } catch (Exception e) {
        }
      }
      else {
        entry.appendChild(doc.createCDATASection(field.getValue().getString()));
      }
    }
    emitDocument(doc, os, encoding);
  }

  static void emitDocument(Document doc, OutputStream os, String encoding) throws IOException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = null;
    try {
      t = tf.newTransformer();
      t.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, BR_DTD_URI);
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty(OutputKeys.METHOD, "xml");
      t.setOutputProperty(OutputKeys.ENCODING, encoding);
    } catch (TransformerConfigurationException tce) {
      assert (false);
    }
//    Node pi = doc.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"" +BR_XSL_URI +"\"");
//    Element root = doc.getDocumentElement();
//    doc.insertBefore(pi, root);
    
    DOMSource doms = new DOMSource(doc);
    StreamResult sr = new StreamResult(os);
    try {
      t.transform(doms, sr);
    } catch (TransformerException te) {
      IOException ioe = new IOException();
      ioe.initCause(te);
      throw ioe;
    }
  }

  private static class Resolver implements EntityResolver {
    public InputSource resolveEntity(String pid, String sid) throws SAXException {
      if (sid.equals(BR_DTD_URI)) {
        InputSource is;
        is = new InputSource(new StringReader(BR_DTD));
        is.setSystemId(BR_DTD_URI);
        return is;
      }
      throw new SAXException("Invalid system identifier: " + sid);
    }
  }

  private static class EH implements ErrorHandler {
    public void error(SAXParseException x) throws SAXException {
      throw x;
    }

    public void fatalError(SAXParseException x) throws SAXException {
      throw x;
    }

    public void warning(SAXParseException x) throws SAXException {
      throw x;
    }
  }

}
