package pl.mpak.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class HtmlTransfer implements Transferable {
  private static ArrayList<DataFlavor> htmlFlavors = new ArrayList<DataFlavor>();

  static {
    try {
      htmlFlavors.add(DataFlavor.stringFlavor);
      htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));
      htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  private String html;
  private String text;

  public HtmlTransfer(String html) {
    this.html = html;
  }

  public HtmlTransfer(String html, String text) {
    this.html = "<html><body>" + html + "</body></html>";
    this.text = text;
  }

  public DataFlavor[] getTransferDataFlavors() {
    return htmlFlavors.toArray(new DataFlavor[htmlFlavors.size()]);
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return htmlFlavors.contains(flavor);
  }

  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
    if (String.class.equals(flavor.getRepresentationClass())) {
      if (text == null) {
        return html.replaceAll("<[^>]*>", "");
      } else {
        return text;
      }
    } else if (Reader.class.equals(flavor.getRepresentationClass())) {
      return new StringReader(html);
    } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
      return new ByteArrayInputStream(html.getBytes());
    }
    throw new UnsupportedFlavorException(flavor);
  }

}
