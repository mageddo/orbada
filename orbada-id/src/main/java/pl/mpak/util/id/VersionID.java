package pl.mpak.util.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import pl.mpak.util.CommaDelimiter;
import pl.mpak.util.ExceptionUtil;
import pl.mpak.util.variant.Variant;
import pl.mpak.util.variant.VariantConnectable;

public class VersionID implements VariantConnectable {
  private static final long serialVersionUID = -6340541665539686170L;

  static {
    Variant.registerVariantClass(serialVersionUID, VersionID.class);
  }
  
  public enum VersionString {
    vsNormal,
    vsShort,
    vsLong
  }
  
  private int major;
  private int minor;
  private int release;
  private int build;
  private String info = null;

  public VersionID() {
    super();
    setMajor(0);
    setMinor(0);
    setRelease(0);
    setBuild(0);
  }

  public VersionID(int major, int minor, int release, int build) {
    super();
    setMajor(major);
    setMinor(minor);
    setRelease(release);
    setBuild(build);
  }

  public VersionID(String versionID) {
    super();
    parse(versionID);
  }
  
  private int parseIntDefault(String text, int defaultValue) {
    try {
      return Integer.parseInt(text);
    }
    catch(Exception e) {
      return defaultValue;
    }
  }
  
  private boolean isIntValue(String text) {
    if (text == null || "".equals(text)) {
      return false;
    }
    for (int i=text.length() -1; i >= 0; i--) {
      char ch = text.charAt(i);
      if (!Character.isDigit(ch)) {
        return false;
      }
    }
    return true;
  }
  
  public void parse(String versionID) {
    if (versionID == null || versionID.length() == 0) {
      throw new NumberFormatException("null or empty");
    }

    boolean beta = false;
    boolean alpha = false;
    // MAJOR[.|-]MINOR[[.|-| ]RELEASE[[.|- ]BUILD] | [alpha|beta[[.]BUILD]]][[-| ]EXTRA_INFO]
    if (versionID.contains("beta") && !versionID.contains(".beta") && !versionID.contains("beta.")) {
      versionID = versionID.replace("beta", ".");
      beta = true;
    }
    if (versionID.contains("alpha") && !versionID.contains(".alpha") && !versionID.contains("alpha.")) {
      versionID = versionID.replace("alpha", ".");
      alpha = true;
    }
    String major = CommaDelimiter.getCommaString(versionID, 1, ".-");
    String minor = CommaDelimiter.getCommaString(versionID, 2, ".- ");
    String release = CommaDelimiter.getCommaString(versionID, 3, ".- ");
    String build = CommaDelimiter.getCommaString(versionID, 4, ".- ");
    String info = CommaDelimiter.getCommaString(versionID, 5, "- ");
    if (!isIntValue(release)) {
      info = release;
    }
    else if (!isIntValue(build)) {
      info = build;
    }
    if (alpha) {
      info = "alpha" +(info != null ? " - " +info : "");
    }
    if (beta) {
      info = "beta" +(info != null ? " - " +info : "");
    }
    
    setMajor(major.length() == 0 ? 0 : Short.parseShort(major));
    setMinor(minor.length() == 0 ? 0 : Short.parseShort(minor));
    setRelease(release.length() == 0 ? 0 : parseIntDefault(release, 0));
    setBuild(build.length() == 0 ? 0 : parseIntDefault(build, 0));
    setInfo(info);
  }
  
  public String toString() {
    return toString(VersionString.vsNormal);
  }

  public String toString(VersionString vs) {
    switch (vs) {
      case vsShort:
        return String.format("%d.%d.%d", new Object[] {major, minor, release});
      case vsLong:
        return 
          String.format("%d.%d.%d (%s)", new Object[] {major, minor, release, build}) +
          ((info == null || info.equals("")) ? "" : " " +info);
      default:
        return 
          String.format("%d.%d.%d.%d", new Object[] {major, minor, release, build}) +
          ((info == null || info.equals("")) ? "" : " " +info);
    }
  }

  public void setMajor(int major) {
    this.major = major;
  }

  public int getMajor() {
    return major;
  }

  public void setMinor(int minor) {
    this.minor = minor;
  }

  public int getMinor() {
    return minor;
  }

  public void setRelease(int release) {
    this.release = release;
  }

  public int getRelease() {
    return release;
  }

  public void setBuild(int build) {
    this.build = build;
  }

  public int getBuild() {
    return build;
  }

  public int nextBuild() {
    return ++build;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getInfo() {
    return info;
  }
  
  public int compare(int major, int minor, int release, int build) {
    minor = (minor < 0) ? this.minor : minor;
    release = (release < 0) ? this.release : release;
    build = (build < 0) ? this.build : build;
    if (this.major == major && this.minor == minor && this.release == release && this.build == build) {
      return 0;
    }
    else if (this.major > major ||
        (this.major == major && this.minor > minor) ||
        (this.major == major && this.minor == minor && this.release > release) ||
        (this.major == major && this.minor == minor && this.release == release && this.build > build)) {
      return 1;
    }
    else {
      return -1;
    }
  }
  
  public int compare(int major, int minor) {
    return compare(major, minor, -1, -1);
  }

  public int compare(int major, int minor, int release) {
    return compare(major, minor, release, -1);
  }

  public int compare(VersionID ver) {
    return compare(ver.getMajor(), ver.getMinor(), ver.getRelease(), ver.getBuild());
  }

  public void write(DataOutput dop) {
    try {
      dop.writeInt(getMajor());
      dop.writeInt(getMinor());
      dop.writeInt(getRelease());
      dop.writeInt(getBuild());
    }
    catch (IOException e) {
      ExceptionUtil.processException(e);
    }
  }

  public void read(DataInput dip) {
    try {
      setMajor(dip.readInt());
      setMinor(dip.readInt());
      setRelease(dip.readInt());
      setBuild(dip.readInt());
    }
    catch (IOException e) {
      ExceptionUtil.processException(e);
    }
  }

  public int compareTo(Variant variant) {
    return 0;
  }

  public int getSize() {
    return getStructSize();
  }

  public static int getStructSize() {
    return 4 +4 +4 +4;
  }

  public Object castTo(int valueType) {
    return toString();
  }

}
