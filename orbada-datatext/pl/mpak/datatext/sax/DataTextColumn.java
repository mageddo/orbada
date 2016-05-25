package pl.mpak.datatext.sax;

public class DataTextColumn {

  public final static int LEFT_ALIGN = 0;
  public final static int RIGHT_ALIGN = 1;
  
  private char dataType;
  int length;
  private String name;
  private int alignment;
  
  public DataTextColumn(int length, String name) {
    this(length, name, 'S', LEFT_ALIGN);
  }
  
  public DataTextColumn(int length, String name, int alignment) {
    this(length, name, 'S', alignment);
  }
  
  public DataTextColumn(int length, String name, char dataType, int alignment) {
    this.length = length;
    this.name = name;
    this.dataType = dataType;
    this.alignment = alignment;
  }

  public int getLength() {
    return length;
  }

  public String getName() {
    return name;
  }

  public char getDataType() {
    return dataType;
  }

  public int getAlignment() {
    return alignment;
  }

}
