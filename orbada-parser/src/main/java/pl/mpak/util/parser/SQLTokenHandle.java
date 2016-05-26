package pl.mpak.util.parser;

import java.io.IOException;

public class SQLTokenHandle implements TokenHandle {

  public final static int BLANK = 1;
  public final static int IDENTIFIER = 2;
  public final static int STRING = 3;
  public final static int QUOTEDSYMBOL = 4;
  public final static int STRINGSYMBOL = 5;
  public final static int NUMBER = 6;
  public final static int COMMENT = 7;
  public final static int LINE_COMMENT = 8;
  public final static int CHAR = 9;
  public final static int OPERATOR = 10;
  public final static int BINDPARAM = 11;

  public SQLTokenHandle() {
    super();
  }

  public int readToken(Tokenizer parser, StringBuffer tokenBuffer) throws IOException {
    int token = NONE;
    
    if (parser.readedCh() <= 32 && !parser.isEof()) {
      token = BLANK;
      do {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      } while (parser.readedCh() <= 32 && !parser.isEof());
    }
    else if (parser.areReadedCh("\"'`")) {
      int ch = parser.readedCh();
      if (ch == '"') {
        token = QUOTEDSYMBOL;
      }
      else if (ch == '`') {
        token = STRINGSYMBOL;
      }
      else {
        token = STRING;
      }
      parser.readCh();
      while (!parser.isEof()) {
        if (parser.readedCh() == ch) {
          parser.readCh();
          if (parser.readedCh() == ch) {
            tokenBuffer.append(ch);
          }
          else {
            break;
          }
        }
        else {
          tokenBuffer.append((char)parser.readedCh());
        }
        parser.readCh();
      }
    }
    else if (parser.isReadedCh('/')) {
      int ch = parser.readedCh();
      parser.readCh();
      if (parser.isReadedCh('*')) {
        token = COMMENT;
        parser.readCh();
        while (!parser.isEof()) {
          if (parser.isReadedCh('*')) {
            ch = parser.readedCh(); 
            parser.readCh();
            if (parser.isReadedCh('/')) {
              parser.readCh();
              break;
            }
            else {
              tokenBuffer.append((char)ch);
            }
          }
          else {
            tokenBuffer.append((char)parser.readedCh());
            parser.readCh();
          }
        }
      }
      else {
        token = OPERATOR;
        tokenBuffer.append((char)ch);
      }
    }
    else if (Character.isJavaIdentifierStart(parser.readedCh()) && parser.readedCh() != 0) {
      token = IDENTIFIER;
      do {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      } while (parser.readedCh() != 0 && 
          (Character.isJavaIdentifierPart(parser.readedCh()) ||
           parser.readedCh() == '$' ||
           parser.readedCh() == '#'));
    }
    else if (parser.isReadedCh('-') || 
        parser.isReadedCh('+') || 
        parser.isReadedCh('=') || 
        parser.isReadedCh('*') || 
        parser.isReadedCh('%')) {
      // --, +, =, *, %
      char ch = (char)parser.readedCh();
      parser.readCh();
      // --
      if (parser.isReadedCh('-')) {
        token = LINE_COMMENT;
        parser.readCh();
        while (!parser.isEof()) {
          if (parser.isReadedCh('\n')) {
            parser.readCh();
            break;
          }
          else {
            tokenBuffer.append((char)parser.readedCh());
            parser.readCh();
          }
        }
      }
      else {
        // -, +, =, *, %
        tokenBuffer.append(ch);
        token = OPERATOR;
      }
    }
    else if (parser.isReadedCh('>')) {
      // >, >=
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      token = OPERATOR;
      if (parser.isReadedCh('=')) {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
    }
    else if (parser.isReadedCh('<')) {
      // <, <=, <>
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      token = OPERATOR;
      if (parser.isReadedCh('=')) {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
      else if (parser.isReadedCh('>')) {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
    }
    else if (parser.isReadedCh('(')) {
      // Oracle outer join (+)
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      if (parser.isReadedCh('+')) {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
        if (parser.isReadedCh(')')) {
          tokenBuffer.append((char)parser.readedCh());
          parser.readCh();
          token = OPERATOR;
        }
        else {
          token = CHAR;
        }
      }
      else {
        token = CHAR;
      }
    }
    else if (parser.isReadedCh('!')) {
      // !=
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      if (parser.isReadedCh('=')) {
        token = OPERATOR;
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
      else {
        token = CHAR;
      }
    }
    else if (parser.isReadedCh('|')) {
      // ||
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      if (parser.isReadedCh('|')) {
        token = OPERATOR;
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
      else {
        token = CHAR;
      }
    }
    else if (parser.isReadedCh(':')) {
      // :=
      // ::
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      if (parser.isReadedCh('=') || parser.isReadedCh(':')) {
        token = OPERATOR;
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      }
      else {
        token = CHAR;
      }
    }
    else if (Character.isDigit(parser.readedCh())) {
      token = NUMBER;
      do {
        tokenBuffer.append((char)parser.readedCh());
        parser.readCh();
      } while (Character.isDigit(parser.readedCh()) || parser.areReadedCh(".eE"));
    }
    else if (parser.isReadedCh('?')) {
      // ?
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
      token = BINDPARAM;
    }
    else 
    {
      token = CHAR;
      tokenBuffer.append((char)parser.readedCh());
      parser.readCh();
    }
    
    return token;
  }

}
