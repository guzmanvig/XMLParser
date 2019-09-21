package xmlparser;

abstract class AbstractXMLParser implements XMLParser{

  /**
   * Root element of this parser
   */
  final XMLElement rootElement;

  /**
   * Abstract constructor so classes that inherit this class are forced to call super and therefore
   * a value to the rootElement will be assigned.
   * @param rootElement Root element of the parser
   */
  AbstractXMLParser(XMLElement rootElement) {
    this.rootElement = rootElement;
  }

  /**
   * Creates a new XMLParser to maintain immutability
   * @param rootElement Root element of the new XMLParser
   * @return The new XMLParser
   */
  abstract XMLParser createXMLParser(XMLElement rootElement);

  /**
   * Creates a copy of the root element. Used to maintain immutability
   * @return  The new root element
   */
  private XMLElement copyRootElement() {
     return new XMLElement(rootElement);
  }

  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    XMLElement rootElement = copyRootElement();

    if (!rootElement.isStarted()) {
      rootElement.start(c);
    } else {
      rootElement.processChar(c);
    }

    return createXMLParser(rootElement);
  }

}
