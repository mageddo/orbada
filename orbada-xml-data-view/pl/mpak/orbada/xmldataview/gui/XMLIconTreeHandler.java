/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.mpak.orbada.xmldataview.gui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author akaluza
 */
public class XMLIconTreeHandler extends DefaultHandler {

  private DefaultMutableTreeNode root,  currentNode;

  public DefaultMutableTreeNode getRoot() {
    return root;
  }

  @Override
  public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
    String eName = lName;
    if ("".equals(eName)) {
      eName = qName;
    }
    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new ITag(eName));
    if (currentNode == null) {
      root = newNode;
    } else {
      currentNode.add(newNode);
    }
    currentNode = newNode;
    if (attrs.getLength() > 0) {
      DefaultMutableTreeNode aNode = new DefaultMutableTreeNode("- attributes");
      currentNode.add(aNode);
      for (int i = 0; i < attrs.getLength(); i++) {
        aNode.add(new DefaultMutableTreeNode(attrs.getQName(i) + " = " + attrs.getValue(i)));
      }
    }
  }

  @Override
  public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    currentNode = (DefaultMutableTreeNode) currentNode.getParent();
  }

  @Override
  public void characters(char buf[], int offset, int len) throws SAXException {
    String s = new String(buf, offset, len).trim();
    ((ITag) currentNode.getUserObject()).addData(s);
  }
  
}
