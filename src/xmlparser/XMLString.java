package xmlparser;

class XMLString implements XMLElementComponent {

  private String currentString;
  private boolean isCompleted = false;

  XMLString() {
    currentString = "";
  }

  XMLString(XMLString xmlString) {
    currentString = xmlString.currentString;
    isCompleted = xmlString.isCompleted;
  }

  String getString() {
    return currentString;
  }

  @Override
  public XMLString createCopy() {
    return new XMLString(this);
  }

  @Override
  public boolean isStarted() {
    return currentString.length() != 0;
  }

  @Override
  public boolean isCompleted() {
    return isCompleted;
  }

  @Override
  public void processChar(char c) throws InvalidXMLException {
    if (isInvalidCharacter(c)) {
      throw new InvalidXMLException("Invalid character in string " + c);
    }
    if (isStartTagSpecialCharacter(c)) {
      isCompleted = true;
    }  else {
      currentString = currentString + c;
    }
  }

  private static boolean isInvalidCharacter(char c) {
    return c == '>';
  }

  private static boolean isStartTagSpecialCharacter(char c) {
    return c == '<';
  }
}
