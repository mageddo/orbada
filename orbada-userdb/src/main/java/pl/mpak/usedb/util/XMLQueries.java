package pl.mpak.usedb.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Types;

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

import pl.mpak.usedb.UseDBException;
import pl.mpak.usedb.core.Query;
import pl.mpak.usedb.core.QueryField;
import pl.mpak.util.variant.VariantException;

public class XMLQueries {

  private Document document;
  private Element root;
  private boolean storeNulls;
  private String method = "xml";
  private String recordName = "record";
  
  /**
   * <p>Tworzy dokument DOM oraz element g³ówny o nazwie rootName
   * @param rootName
   * @throws ParserConfigurationException
   */
  public XMLQueries(String rootName) throws ParserConfigurationException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    document = builder.newDocument();
    root = document.createElement(rootName);
    document.appendChild(root);
  }
  
  /**
   * <p>Zapisuje w strukturze rekordy z Query, kolumny oraz ich wartoœci.<br>
   * Poszczególne rekordy bêd¹ zapisane w strukturze "record".
   * @param query wynik, która ma zostaæ zapisany
   * @param queryName nazwa struktury rekordów, np nazwa tabeli 
   * @return
   * @throws UseDBException
   * @throws DOMException
   * @throws VariantException
   * @throws SQLException
   * @throws IOException
   * @see setStoreNulls()
   * @see setRecordName()
   */
  public Element appendQuery(Query query, String queryName) throws UseDBException, DOMException, VariantException, SQLException, IOException {
    
    Element results = document.createElement(queryName);
    root.appendChild(results);

    Element columns = document.createElement("columns");
    results.appendChild(columns);
    for (int i=0; i<query.getFieldCount(); i++) {
      QueryField field = query.getField(i);
      Element node = document.createElement("column");
      node.setAttribute("name", field.getFieldName());
      node.setAttribute("type", SQLUtil.typeToString(field.getDataType()));
      node.setAttribute("size", String.valueOf(field.getDisplaySize()));
      node.setAttribute("scale", String.valueOf(field.getScale()));
      node.setAttribute("nullable", String.valueOf(field.isNullable()));
      columns.appendChild(node);
    }

    while (!query.eof()) {
      Element row = document.createElement(recordName);
      results.appendChild(row);
      for (int i=0; i<query.getFieldCount(); i++) {
        if (!query.getField(i).isNull() || storeNulls) {
          Element node = document.createElement(query.getField(i).getFieldName());
          if (!query.getField(i).isNull()) {
            switch (query.getField(i).getDataType()) {
              case Types.ARRAY:
              case Types.BINARY:
              case Types.LONGNVARCHAR:
              case Types.LONGVARBINARY:
              case Types.LONGVARCHAR:
              case Types.VARBINARY:
                node.appendChild(document.createCDATASection(query.getField(i).getValue().getString()));
                break;
              default:
                node.appendChild(document.createTextNode(query.getField(i).getValue().getString()));
            }
          }
          else {
            node.setAttribute("null", "true");
          }
          row.appendChild(node);
        }
      }
      query.next();
    }
    
    return results;
  }
  
  private Transformer prepareTransform(String encoding) throws TransformerConfigurationException {
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.METHOD, method);
    transformer.setOutputProperty(OutputKeys.ENCODING, encoding == null ? Charset.defaultCharset().name() : encoding);
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    return transformer;
  }
  
  /**
   * <p>Zachowuje strukturê dokumentu w OutputStream
   * @param output
   * @param encoding jeœli null, zachowany bêdzie w domyœlnym kodowaniu znaków
   * @throws TransformerException
   */
  public void store(OutputStream output, String encoding) throws TransformerException {
    DOMSource domSource = new DOMSource(document);
    Transformer transformer = prepareTransform(encoding);
    StreamResult sr = new StreamResult(output);
    transformer.transform(domSource, sr);
  }

  /**
   * <p>Zachowuje strukturê dokumentu w Writer
   * @param writer
   * @param encoding jeœli null, zachowany bêdzie w domyœlnym kodowaniu znaków
   * @throws TransformerException
   */
  public void store(Writer writer, String encoding) throws TransformerException {
    DOMSource domSource = new DOMSource(document);
    Transformer transformer = prepareTransform(encoding);
    StreamResult sr = new StreamResult(writer);
    transformer.transform(domSource, sr);
  }

  /**
   * <p>Zachowuje strukturê dokumentu w File
   * @param file
   * @param encoding jeœli null, zachowany bêdzie w domyœlnym kodowaniu znaków
   * @throws TransformerException
   */
  public void store(File file, String encoding) throws TransformerException {
    DOMSource domSource = new DOMSource(document);
    Transformer transformer = prepareTransform(encoding);
    StreamResult sr = new StreamResult(file);
    transformer.transform(domSource, sr);
  }

  public Document getDocument() {
    return document;
  }

  /**
   * <p>Element g³ówny struktury
   * @return
   */
  public Element getRoot() {
    return root;
  }

  public boolean isStoreNulls() {
    return storeNulls;
  }

  /**
   * <p>Pozwala ustawiæ znacznik umieszczania w strukturze kolumn z wartoœciami pustymi, null
   * @param storeNulls
   */
  public void setStoreNulls(boolean storeNulls) {
    this.storeNulls = storeNulls;
  }

  public String getMethod() {
    return method;
  }

  /**
   * <p>Domyœlnie "xml"
   * @param method
   */
  public void setMethod(String method) {
    this.method = method;
  }

  public String getRecordName() {
    return recordName;
  }

  /**
   * <p>Domyœlnie "record"
   * @param recordName
   */
  public void setRecordName(String recordName) {
    this.recordName = recordName;
  }

}
