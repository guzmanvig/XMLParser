/**
 * Class that represents an XML string implementing the XMLElementComponents interface.
 * The string can be between any tags and can contain any character except for < and >.
 */
class XMLString implements XMLElementComponent {

  private String currentString;
  private boolean isCompleted = false;

  XMLString() {
    currentString = "";
  }

  String getString() {
    return currentString;
  }

  /**
   * The string has started when it has at least one valid character.
   * @return the string has started
   */
  @Override
  public boolean isStarted() {
    return currentString.length() != 0;
  }

  /**
   * The string is completed if the character representing the start of a tag is processed.
   * @return the string is completed.
   */
  @Override
  public boolean isCompleted() {
    return isCompleted;
  }

  /**
   * Processes the char. If its a valid char, it will add it to the internal string.
   * A start tag character (<) will complete the string.
   * @param c the char to be processed,
   * @throws InvalidXMLException if the character is invalid. I.e.: >
   */
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
