/**
 * Class that represents an XML tag implementing the XMLElementComponents interface.
 * The tag can be either a start tag or an end tag.
 * A tag name can contain only characters 'a-z', 'A-Z', '0-9', :, _, and -.
 * However, a tag name cannot start with a number or -.
 */
class XMLTag implements XMLElementComponent {

  private String currentTagString = "";
  private boolean isComplete = false;

  /**
   * Gets the tag name that has been processed so far.
   * @return the name that has been processed so far
   */
  String getName() {
    String name = currentTagString.replace("<", "");
    name = name.replace(">", "");
    if (name.charAt(0) == '/') {
      name = name.replace("/", "");
    }
    return name;
  }


  /**
   * Checks if this tag is a start tag or an end tag.
   * @return true if it is a start tag or if the current input is not enough to determine it.
   */
  boolean isStartTag() {
    if (currentTagString.length() >= 2) {
      return currentTagString.charAt(1) != '/';
    } else {
      //Since the current input is not enough to know, return true.
      // It won't matter to the parser output since it only prints completed elements.
      // The element will check if its an end tag to compare it with its first tag, in this state,
      // the comparison doesn't make sense, so returning true avoids that comparison.
      return true;
    }
  }

  /**
   * The tag has started if it has at least one valid character.
   * @return the tag has started.
   */
  @Override
  public boolean isStarted() {
    return currentTagString.length() != 0;
  }

  /**
   * The tag is completed when the char > is processed.
   * @return the tag is completed.
   */
  @Override
  public boolean isCompleted() {
    return isComplete;
  }


  /**
   * Processes a char. If the character is valid in the current state of the tag, it adds it to the
   * internal tag string.
   * @param c the char to be processed,
   * @throws InvalidXMLException when the processed char makes the current tag invalid.
   */
  @Override
  public void processChar(char c) throws InvalidXMLException {
    if (!isStarted()) {

      start(c);

    } else if (isEndFirstSpecialCharacter(c)) {

      addChar(c);

    } else if (isEndSecondSpecialCharacter(c)) {

      complete(c);

    } else if (isValidCharacter(c)) {

      processCharForTagName(c);

    } else {
      throw new InvalidXMLException("Invalid character in tag: " + c);
    }
  }

  private void start(char startChar) throws InvalidXMLException {
    if (!isStartSpecialCharacter(startChar)) {
      throw new InvalidXMLException("Invalid tag start character " + startChar);
    }
    addChar(startChar);
  }

  static boolean isStartSpecialCharacter(char c) {
    return c == '<';
  }

  private void addChar(char c) {
    currentTagString = currentTagString + c;
  }

  private static boolean isEndFirstSpecialCharacter(char c) {
    return c == '/';
  }

  private static boolean isEndSecondSpecialCharacter(char c) {
    return c == '>';
  }

  private void complete(char endChar) throws InvalidXMLException {
    if (currentTagString.length() == 1) {
      throw new InvalidXMLException("Tag must have a non empty name");
    }
    addChar(endChar);
    isComplete = true;
  }

  private void processCharForTagName(char c) throws InvalidXMLException {
    if (hasOnlySpecialStartCharacter() && isInvalidFirstCharacter(c)) {
      throw new InvalidXMLException("Invalid first character in tag: " + c);
    }
    if (previousCharacterIsEndFirstSpecialCharacter() && isStartTag()) {
      throw new InvalidXMLException("Invalid character after /: " + c);
    }
    addChar(c);
  }

  private boolean hasOnlySpecialStartCharacter() {
    return currentTagString.length() == 1;
  }

  private boolean previousCharacterIsEndFirstSpecialCharacter() {
    return currentTagString.charAt(currentTagString.length() - 1) == '/';
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

  private static boolean isInvalidFirstCharacter(char c) {
    return isDigit(c) || c == '-';
  }

}
