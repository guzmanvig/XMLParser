/**
 * Class that represents an XML tag implementing the XMLElementComponents interface.
 * The tag can be either a start tag or an end tag.
 * A tag name can contain only characters 'a-z', 'A-Z', '0-9', :, _, and -.
 * However, a tag name cannot start with a number or -.
 */
class XMLTag implements XMLElementComponent {

  private String currentTagString = "";
  private boolean isComplete = false;

  String getTagName() {
    //TODO: clean this
    int tagLength = currentTagString.length();
    if (tagLength == 0 || tagLength == 1) {
      return "";
    } else {
      if (currentTagString.charAt(1) == '/') {
        if (currentTagString.length() == 2) {
          return "";
        } else {
          if (currentTagString.charAt(currentTagString.length() - 1) == '>') {
            return currentTagString.substring(2, currentTagString.length() - 1);
          } else {
            return currentTagString.substring(2);
          }
        }
      } else {
        if (currentTagString.charAt(currentTagString.length() - 1) == '>') {
          return currentTagString.substring(1, currentTagString.length() - 1);
        } else {
          return currentTagString.substring(1);
        }
      }
    }
  }

  private void start(char startChar) throws InvalidXMLException {
    if (!isStartSpecialCharacter(startChar)) {
      throw new InvalidXMLException("Invalid tag start character " + startChar);
    }
    currentTagString = "" + startChar;
  }

  boolean isStartTag() {
    if (currentTagString.length() >= 2) {
      return currentTagString.charAt(1) != '/';
    } else {
      //Since we don't now, we assume it is
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
      currentTagString = currentTagString + c;
    } else if (isEndSecondSpecialCharacter(c)) {
      if (currentTagString.length() == 1) {
        throw new InvalidXMLException("Tag must have a non empty name");
      }
      currentTagString = currentTagString + c;
      isComplete = true;
    } else if (isValidCharacter(c)) {
      if (hasOnlySpecialStartCharacter() && isInvalidFirstCharacter(c)) {
        throw new InvalidXMLException("Invalid first character in tag: " + c);
      }
      if (previousCharacterIsEndFirstCharacter() && isStartTag()) {
        throw new InvalidXMLException("Invalid character after /: " + c);
      }
      currentTagString = currentTagString + c;
    } else {
      throw new InvalidXMLException("Invalid character in tag: " + c);
    }
  }

  private static boolean isEndFirstSpecialCharacter(char c) {
    return c == '/';
  }

  private static boolean isEndSecondSpecialCharacter(char c) {
    return c == '>';
  }

  static boolean isStartSpecialCharacter(char c) {
    return c == '<';
  }

  private boolean hasOnlySpecialStartCharacter() {
    return currentTagString.length() == 1;
  }

  private boolean previousCharacterIsEndFirstCharacter() {
    return currentTagString.charAt(currentTagString.length() - 1) == '/';
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
