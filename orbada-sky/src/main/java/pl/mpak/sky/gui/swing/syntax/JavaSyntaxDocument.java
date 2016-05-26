package pl.mpak.sky.gui.swing.syntax;

import java.awt.Color;

import javax.swing.text.Segment;

import pl.mpak.sky.gui.swing.SwingUtil;

public class JavaSyntaxDocument extends SyntaxDocument {
  private static final long serialVersionUID = 1227415492029306963L;

  public final static int KEYWORD = 1;
  public final static int STATIC_FIELD = 2;
  public final static int CLASS_FIELD = 3;
  public final static int COMMENT = 4;
  public final static int OPERATOR = 5;
  public final static int STRING = 6;
  public final static int EXCEPTION = 7;
  public final static int IDENTIFIER = 8;
  public final static int NUMERIC = 9;
  public final static int PRIMITIVE_TYPE = 10;
  public final static int ANNOTATION = 11;
  public final static int BRACKET = 12;
  public final static int SPECJAL_CHAR = 13;
  public final static int DOCUMENTATION = 20;
  public final static int DOCUMENTATION_KEYWORD = 21;
  public final static int DOCUMENTATION_HTML_TAG = 22;
  
  public JavaSyntaxDocument() {
    super();
    setIgnoreTokentForImmediateSnippet(new int[] {STRING, COMMENT, DOCUMENTATION, DOCUMENTATION_KEYWORD, DOCUMENTATION_HTML_TAG});
    addStyle(KEYWORD, new SyntaxStyle("Key word", SwingUtil.Color.NAVY, true, false));
    addStyle(STATIC_FIELD, new SyntaxStyle("Static field", Color.BLUE, false, true));
    addStyle(CLASS_FIELD, new SyntaxStyle("Class field", Color.BLUE));
    addStyle(COMMENT, new SyntaxStyle("Comment", Color.GRAY));
    addStyle(OPERATOR, new SyntaxStyle("Operator", Color.BLUE));
    addStyle(STRING, new SyntaxStyle("String", SwingUtil.Color.GREEN));
    addStyle(EXCEPTION, new SyntaxStyle("Exception", Color.RED));
    addStyle(IDENTIFIER, new SyntaxStyle("Identifier", Color.BLACK));
    addStyle(NUMERIC, new SyntaxStyle("Numeric", Color.BLUE));
    addStyle(PRIMITIVE_TYPE, new SyntaxStyle("Primitive type", SwingUtil.Color.FUCHSIA));
    addStyle(ANNOTATION, new SyntaxStyle("Annotation", Color.GRAY, true, true));
    addStyle(DOCUMENTATION, new SyntaxStyle("Documentation", new Color(63, 97, 159)));
    addStyle(DOCUMENTATION_KEYWORD, new SyntaxStyle("Documentation key word", new Color(63, 97, 159), true, false));
    addStyle(DOCUMENTATION_HTML_TAG, new SyntaxStyle("Documentation html tag", new Color(127, 127, 159)));
    addStyle(BRACKET, new SyntaxStyle("Brackets", Color.BLACK));
    addStyle(SPECJAL_CHAR, new SyntaxStyle("Special Char", Color.BLACK));
    
    defaultAll();
  }
  
  public void defaultKeyWords() {
    addSingleKeyWord("abstract", KEYWORD);
    addSingleKeyWord("continue", KEYWORD); 
    addSingleKeyWord("for", KEYWORD); 
    addSingleKeyWord("new", KEYWORD);  
    addSingleKeyWord("switch", KEYWORD);  
    addSingleKeyWord("assert", KEYWORD); 
    addSingleKeyWord("default", KEYWORD); 
    addSingleKeyWord("goto", KEYWORD); 
    addSingleKeyWord("package", KEYWORD); 
    addSingleKeyWord("synchronized", KEYWORD); 
    addSingleKeyWord("do", KEYWORD); 
    addSingleKeyWord("if", KEYWORD); 
    addSingleKeyWord("private", KEYWORD); 
    addSingleKeyWord("this", KEYWORD); 
    addSingleKeyWord("break", KEYWORD); 
    addSingleKeyWord("implements", KEYWORD); 
    addSingleKeyWord("protected", KEYWORD); 
    addSingleKeyWord("throw", KEYWORD); 
    addSingleKeyWord("else", KEYWORD); 
    addSingleKeyWord("import", KEYWORD); 
    addSingleKeyWord("public", KEYWORD); 
    addSingleKeyWord("throws", KEYWORD); 
    addSingleKeyWord("case", KEYWORD); 
    addSingleKeyWord("enum", KEYWORD); 
    addSingleKeyWord("instanceof", KEYWORD); 
    addSingleKeyWord("return", KEYWORD); 
    addSingleKeyWord("transient", KEYWORD);  
    addSingleKeyWord("catch", KEYWORD); 
    addSingleKeyWord("extends", KEYWORD); 
    addSingleKeyWord("try", KEYWORD); 
    addSingleKeyWord("final", KEYWORD); 
    addSingleKeyWord("interface", KEYWORD); 
    addSingleKeyWord("static", KEYWORD); 
    addSingleKeyWord("void", KEYWORD); 
    addSingleKeyWord("class", KEYWORD); 
    addSingleKeyWord("finally", KEYWORD); 
    addSingleKeyWord("strictfp", KEYWORD); 
    addSingleKeyWord("volatile", KEYWORD); 
    addSingleKeyWord("const", KEYWORD); 
    addSingleKeyWord("native", KEYWORD); 
    addSingleKeyWord("super", KEYWORD); 
    addSingleKeyWord("while", KEYWORD); 
  }

  public void defaultPrimitiveTypes() {
    addSingleKeyWord("long", PRIMITIVE_TYPE); 
    addSingleKeyWord("float", PRIMITIVE_TYPE); 
    addSingleKeyWord("short", PRIMITIVE_TYPE); 
    addSingleKeyWord("char", PRIMITIVE_TYPE); 
    addSingleKeyWord("boolean", PRIMITIVE_TYPE); 
    addSingleKeyWord("double", PRIMITIVE_TYPE); 
    addSingleKeyWord("byte", PRIMITIVE_TYPE); 
    addSingleKeyWord("int", PRIMITIVE_TYPE); 
    addSingleKeyWord("String", PRIMITIVE_TYPE);
    addSingleKeyWord("Integer", PRIMITIVE_TYPE);
    addSingleKeyWord("Long", PRIMITIVE_TYPE);
    addSingleKeyWord("Double", PRIMITIVE_TYPE);
    addSingleKeyWord("Float", PRIMITIVE_TYPE);
    addSingleKeyWord("null", PRIMITIVE_TYPE);
  }
  
  public void defaultExceptions() {
    addSingleKeyWord("Throwable", EXCEPTION); 
    addSingleKeyWord("Error", EXCEPTION); 
    addSingleKeyWord("Exception", EXCEPTION);  
    addSingleKeyWord("AssertionError", EXCEPTION); 
    addSingleKeyWord("AWTError", EXCEPTION); 
    addSingleKeyWord("CoderMalfunctionError", EXCEPTION); 
    addSingleKeyWord("FactoryConfigurationError", EXCEPTION); 
    addSingleKeyWord("LinkageError", EXCEPTION); 
    addSingleKeyWord("ThreadDeath", EXCEPTION); 
    addSingleKeyWord("TransformerFactoryConfigurationError", EXCEPTION); 
    addSingleKeyWord("VirtualMachineError", EXCEPTION);
    addSingleKeyWord("AclNotFoundException", EXCEPTION);
    addSingleKeyWord("ActivationException", EXCEPTION);
    addSingleKeyWord("AlreadyBoundException", EXCEPTION);
    addSingleKeyWord("ApplicationException", EXCEPTION);
    addSingleKeyWord("AWTException", EXCEPTION);
    addSingleKeyWord("BackingStoreException", EXCEPTION);
    addSingleKeyWord("BadLocationException", EXCEPTION);
    addSingleKeyWord("CertificateException", EXCEPTION);
    addSingleKeyWord("ClassNotFoundException", EXCEPTION);
    addSingleKeyWord("CloneNotSupportedException", EXCEPTION);
    addSingleKeyWord("DataFormatException", EXCEPTION);
    addSingleKeyWord("DestroyFailedException", EXCEPTION);
    addSingleKeyWord("ExpandVetoException", EXCEPTION);
    addSingleKeyWord("FontFormatException", EXCEPTION);
    addSingleKeyWord("GeneralSecurityException", EXCEPTION);
    addSingleKeyWord("GSSException", EXCEPTION);
    addSingleKeyWord("IllegalAccessException", EXCEPTION);
    addSingleKeyWord("InstantiationException", EXCEPTION);
    addSingleKeyWord("InterruptedException", EXCEPTION);
    addSingleKeyWord("IntrospectionException", EXCEPTION);
    addSingleKeyWord("InvalidMidiDataException", EXCEPTION);
    addSingleKeyWord("InvalidPreferencesFormatException", EXCEPTION);
    addSingleKeyWord("InvocationTargetException", EXCEPTION);
    addSingleKeyWord("IOException", EXCEPTION);
    addSingleKeyWord("LastOwnerException", EXCEPTION);
    addSingleKeyWord("LineUnavailableException", EXCEPTION);
    addSingleKeyWord("MidiUnavailableException", EXCEPTION);
    addSingleKeyWord("MimeTypeParseException", EXCEPTION);
    addSingleKeyWord("NamingException", EXCEPTION);
    addSingleKeyWord("NoninvertibleTransformException", EXCEPTION);
    addSingleKeyWord("NoSuchFieldException", EXCEPTION);
    addSingleKeyWord("NoSuchMethodException", EXCEPTION);
    addSingleKeyWord("NotBoundException", EXCEPTION);
    addSingleKeyWord("NotOwnerException", EXCEPTION);
    addSingleKeyWord("ParseException", EXCEPTION);
    addSingleKeyWord("ParserConfigurationException", EXCEPTION);
    addSingleKeyWord("PrinterException", EXCEPTION);
    addSingleKeyWord("PrintException", EXCEPTION);
    addSingleKeyWord("PrivilegedActionException", EXCEPTION);
    addSingleKeyWord("PropertyVetoException", EXCEPTION);
    addSingleKeyWord("RefreshFailedException", EXCEPTION);
    addSingleKeyWord("RemarshalException", EXCEPTION);
    addSingleKeyWord("RuntimeException", EXCEPTION);
    addSingleKeyWord("SAXException", EXCEPTION);
    addSingleKeyWord("ServerNotActiveException", EXCEPTION);
    addSingleKeyWord("SQLException", EXCEPTION);
    addSingleKeyWord("TooManyListenersException", EXCEPTION);
    addSingleKeyWord("TransformerException", EXCEPTION);
    addSingleKeyWord("UnsupportedAudioFileException", EXCEPTION);
    addSingleKeyWord("UnsupportedCallbackException", EXCEPTION);
    addSingleKeyWord("UnsupportedFlavorException", EXCEPTION);
    addSingleKeyWord("UnsupportedLookAndFeelException", EXCEPTION);
    addSingleKeyWord("URISyntaxException", EXCEPTION);
    addSingleKeyWord("UserException", EXCEPTION);
    addSingleKeyWord("XAException", EXCEPTION); 
  }
  
  public void defaultAll() {
    defaultKeyWords();
    defaultPrimitiveTypes();
    defaultExceptions();
  }

  protected boolean tokenizeLine(Segment line, LineInfo info, int lastToken) {
    //int oldToken = info.lastStyleId();
    
    info.clear();
    int index = line.offset;
    int start = index;
    int length = line.count +line.offset;
    
    while (index < length) {
      switch (line.array[index]) {
        case '\\': {
          if (lastToken == STRING) {
            if (index +1 < length && line.array[index +1] == '\"') {
              index++;
            }
          }
          break;
        }
        case '"': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            lastToken = STRING;
            start = index;
          }
          else if (lastToken == STRING) {
            info.addToken(STRING, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          break;
        }
        case '\'': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            lastToken = STRING;
            start = index;
          }
          else if (lastToken == STRING) {
            info.addToken(STRING, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          break;
        }
        case '/': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            start = index;
            if (index +1 < length && line.array[index +1] == '*') {
              int i = index +2;
              while (i < length && line.array[i] == ' ') {
                i++;
              }
              if (i < length && line.array[i] == '*') {
                lastToken = DOCUMENTATION;
              }
              else {
                lastToken = COMMENT;
              }
              start = index;
            }
            else if (index +1 < length && line.array[index +1] == '/') {
              index+=2;
              while (index < length && line.array[index] == ' ') {
                index++;
              }
              info.addToken(COMMENT, length -start);
              index = length -1;
              start = index +1;
            }
            else {
              info.addToken(OPERATOR, 1);
              start = index +1;
            }
          }
          break;
        }
        case '*': {
          if ((lastToken == COMMENT || lastToken == DOCUMENTATION) && 
              index +1 < length && line.array[index +1] == '/') {
            index++;
            info.addToken(lastToken, index -start +1);
            lastToken = NONE;
            start = index +1;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(OPERATOR, 1);
            start = index +1;
          }
          break;
        }
        case '!':
        case '&':
        case '|':
        case '-':
        case '+':
        case '>':
        case '=': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(OPERATOR, 1);
            start = index +1;
          }
          break;
        }
        case '@': {
          if (lastToken == DOCUMENTATION && index +1 < length && (Character.isLetter(line.array[index +1]) || line.array[index] == '_')) {
            info.addToken(DOCUMENTATION, index -start);
            StringBuilder bf = new StringBuilder("@");
            index++;
            start = index;
            while (index < length && 
                (Character.isLetter(line.array[index]) || 
                 Character.isDigit(line.array[index]) || 
                 line.array[index] == '_')) {
              bf.append(line.array[index]);
              index++;
            }
            info.addToken(DOCUMENTATION_KEYWORD, bf.length());
            start = index;
            index--;
          }
          else if (lastToken == NONE && index +1 < length && (Character.isLetter(line.array[index +1]) || line.array[index] == '_')) {
            info.addToken(ANNOTATION, index -start);
            StringBuilder bf = new StringBuilder("@");
            index++;
            start = index;
            while (index < length && 
                (Character.isLetter(line.array[index]) || 
                 Character.isDigit(line.array[index]) || 
                 line.array[index] == '_')) {
              bf.append(line.array[index]);
              index++;
            }
            info.addToken(ANNOTATION, bf.length());
            start = index;
            index--;
          }
          break;
        }
        case '<': {
          if (lastToken == DOCUMENTATION) {
            info.addToken(DOCUMENTATION, index -start);
            StringBuilder bf = new StringBuilder("<");
            index++;
            start = index;
            while (index < length && line.array[index] != '>' && line.array[index] != '\n') {
              bf.append(line.array[index]);
              index++;
            }
            if (line.array[index] == '>') { 
              bf.append('>');
              index++;
            }
            info.addToken(DOCUMENTATION_HTML_TAG, bf.length());
            start = index;
            index--;
          }
          else if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(OPERATOR, 1);
            start = index +1;
          }
          break;
        }
        case ']':
        case '[':
        case '}':
        case '{':
        case ')':
        case '(': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(BRACKET, 1);
            start = index +1;
          }
          break;
        }
        case ',':
        case ';':
        case '.': {
          if (lastToken == NONE) {
            info.addToken(NONE, index -start);
            info.addToken(SPECJAL_CHAR, 1);
            start = index +1;
          }
          break;
        }
        default: {
          if (lastToken == NONE) {
            if (Character.isDigit(line.array[index])) {
              info.addToken(NONE, index -start);
              start = index;
              while (index < length && Character.isDigit(line.array[index])) {
                index++;
              }
              if (index < length && line.array[index] == '.') {
                index++;
                while (index < length && Character.isDigit(line.array[index])) {
                  index++;
                }
              }
              if (index < length && (line.array[index] == 'e' || line.array[index] == 'E')) {
                index++;
                if (index < length && (line.array[index] == '-' || line.array[index] == '+')) {
                  index++;
                  while (index < length && Character.isDigit(line.array[index])) {
                    index++;
                  }
                }
              }
              info.addToken(NUMERIC, index -start);
              start = index;
              index--;
            }
            else if (Character.isLetter(line.array[index]) || line.array[index] == '_') {
              StringBuilder bf = new StringBuilder();
              info.addToken(NONE, index -start);
              start = index;
              while (index < length && 
                  (Character.isLetter(line.array[index]) || 
                   Character.isDigit(line.array[index]) || 
                   line.array[index] == '_')) {
                bf.append(line.array[index]);
                index++;
              }
              int style = getStyle(bf.toString());
              if (style != NONE) {
                info.addToken(style, bf.length());
              }
              else {
                info.addToken(IDENTIFIER, bf.length());
              }
              start = index;
              index--;
            }
          }
        }
      }
      index++;
    }
    
    info.addToken(lastToken, index -start);
    
    return true; //lastToken != NONE || (oldToken != info.lastStyleId() && oldToken != NONE);
  }
  
//  public String[] getWordsAt(int off) throws BadLocationException {
//    Segment segment = new Segment();
//    SyntaxDocument.LineInfo info = getLineInfoAtOffset(off, segment);
//    if (info != null) {
//      Token token = info.getTokenAtOffset(off -getLineOffsetAt(off));
//      if (token != null) {
//        ArrayList<String> words = new ArrayList<String>();
//        int[] styles = {KEYWORD, EXCEPTION, IDENTIFIER, STATIC_FIELD, CLASS_FIELD, PRIMITIVE_TYPE};
//        String w = info.getStringAtOffset(token.offset, segment);
//        if (token.anyOfStyle(styles)) {
//          words.add(w);
//        }
//        else if (token.styleId == STRING && w.length() > 0) {
//          if (w.charAt(0) == '"') {
//            words.add(w.substring(1, w.length() -1));
//          }
//          else {
//            words.add(w);
//          }
//        }
//        Token prev = token.prev;
//        if (prev != null && prev.length == 0) {
//          prev = prev.prev;
//        }
//        while (prev != null) {
//          w = info.getStringAtOffset(prev.offset, segment).trim();
//          while (w.length() == 0) {
//            prev = prev.prev;
//            if (prev != null && prev.length == 0) {
//              prev = prev.prev;
//            }
//            if (prev == null) {
//              break;
//            }
//            w = info.getStringAtOffset(prev.offset, segment).trim();
//          }
//          if (!w.equals(".")) {
//            break;
//          }
//          prev = prev.prev;
//          if (prev != null && prev.length == 0) {
//            prev = prev.prev;
//          }
//          if (prev != null) {
//            w = info.getStringAtOffset(prev.offset, segment);
//            if (prev.anyOfStyle(styles)) {
//              words.add(0, w);
//            }
//            else if (prev.styleId == STRING) {
//              words.add(0, w.substring(1, w.length() -1));
//            }
//            else {
//              break;
//            }
//            prev = prev.prev;
//            if (prev != null && prev.length == 0) {
//              prev = prev.prev;
//            }
//          }
//          else {
//            break;
//          }
//        }
//        Token next = token.next;
//        if (next != null && next.length == 0) {
//          next = next.next;
//        }
//        while (next != null) {
//          w = info.getStringAtOffset(next.offset, segment).trim();
//          while (w.length() == 0) {
//            next = next.next;
//            if (next != null && next.length == 0) {
//              next = next.next;
//            }
//            if (next == null) {
//              break;
//            }
//            w = info.getStringAtOffset(next.offset, segment).trim();
//          }
//          if (!w.equals(".")) {
//            break;
//          }
//          next = next.next;
//          if (next != null && next.length == 0) {
//            next = next.next;
//          }
//          if (next != null) {
//            w = info.getStringAtOffset(next.offset, segment);
//            if (next.anyOfStyle(styles)) {
//              words.add(w);
//            }
//            else if (next.styleId == STRING) {
//              words.add(w.substring(1, w.length() -1));
//            }
//            else {
//              break;
//            }
//            next = next.next;
//            if (next != null && next.length == 0) {
//              next = next.next;
//            }
//          }
//          else {
//            break;
//          }
//        }
//        return words.toArray(new String[words.size()]);
//      }
//    }
//    return super.getWordsAt(off);
//  }

  public void setTextComponent(SyntaxEditor textComponent) {
    super.setTextComponent(textComponent);
    ((SyntaxEditor)textComponent).setComments(new CommentSpec[] {new CommentSpec("/*", "*/"), new CommentSpec("//", "\n")});
  }

  public String toString() {
    return "Java Syntax Highlight";
  }

}
