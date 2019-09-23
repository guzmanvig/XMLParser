/**
 * Abstract parser. Every XMLParser should inherit from this class.
 * It creates the root element and implements the input method.
 * It is up to each parser to implement the output method.
 */
abstract class AbstractXMLParser implements XMLParser {

  XMLElement rootElement = new XMLElement();

  /**
   * Processes a new char and changes the current state of the parser accordingly
   * @param c the input character
   * @return the parser modified by the char
   * @throws InvalidXMLException if the character cannot be processed since it will not form a valid
   * xml
   */
  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    rootElement.processChar(c);
    return this;
  }

}
