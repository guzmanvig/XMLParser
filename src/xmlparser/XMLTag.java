package xmlparser;

class XMLTag implements XMLElementComponent {

  private String currentTagString;

  XMLTag(String currentTagString) {
    this.currentTagString = currentTagString;
  }

  XMLTag(XMLTag xmlTag) {
    this(xmlTag.currentTagString);
  }

  XMLTag() {
    this("");
  }

  String getTagName(){
    // Remove the first '<' and the last '/>'
    return currentTagString.substring(1, currentTagString.length() - 3);
  }

  @Override
  public void start(char startChar) throws InvalidXMLException {
    if (isStartSpecialCharacter(startChar))
      throw new InvalidXMLException("Invalid tag start character " + startChar);
    currentTagString = "" + startChar;
  }

  @Override
  public XMLTag createCopy() {
    return new XMLTag(this);
  }

  boolean isStartTag() {
    return currentTagString.charAt(1) != '/';
  }

  @Override
  public boolean isStarted() {
    return currentTagString.length() != 0;
  }


  @Override
  public boolean processChar(char c) throws InvalidXMLException {
    boolean finished = false;
    if (isValidCharacter(c)) {
      if (hasOnlySpecialStartCharacter() && isInvalidFirstCharacter(c)) {
          throw new InvalidXMLException("Invalid first character in tag: " + c);
      } else {
        currentTagString = currentTagString + c;
        if (isEndSpecialCharacter(c)) {
          finished = true;
        }
      }

    } else {
      throw new InvalidXMLException("Invalid character in tag: " + c);
    }
    return finished;
  }

  private boolean isEndSpecialCharacter(char c) {
    return c == '>';
  }

  private boolean isStartSpecialCharacter(char c) {
    return c == '<';
  }

  private boolean hasOnlySpecialStartCharacter() {
    return currentTagString.length() == 1;
  }

  private static boolean isInvalidFirstCharacter(char c) {
    return isDigit(c) || c == '-';
  }

  private static boolean isValidCharacter(char c) {
    return isDigit(c) || isLetter(c) || c == ':' || c == '_' || c == '-';
  }

  private static boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  private static boolean isLetter(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');

  }
}
