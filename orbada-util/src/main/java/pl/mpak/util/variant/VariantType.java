package pl.mpak.util.variant;

public class VariantType {
  public static final int varUnassigned = 0;
  public static final int varNull = 1;
  public static final int varShort = 2;
  public static final int varInteger = 3;
  public static final int varFloat = 4;
  public static final int varDouble = 5;
  public static final int varLong = 6;
  public static final int varDate = 7;
  public static final int varBoolean = 8;
  public static final int varVariant = 9;
  public static final int varByte = 10;
  public static final int varString = 11;
  public static final int varList = 12;
  public static final int varBinary = 13;
  public static final int varBigDecimal = 14;
  public static final int varBigInteger = 15;
  public static final int varJavaObject = 16;
  public static final int varInputStream = 17;
  public static final int varOutputStream = 18;
  public static final int varTime = 19;
  public static final int varTimestamp = 20;
  
  // specjalne pod-typy dla varJavaObject
  public static final int varSubConnectable = 1;
  public static final int varSubSerializable = 2;
  public static final int varSubVariant = 3;
  
}
