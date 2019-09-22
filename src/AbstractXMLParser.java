abstract class AbstractXMLParser implements XMLParser{

  protected XMLElement rootElement;

  AbstractXMLParser(XMLElement rootElement) {
    this.rootElement = rootElement;
  }

  abstract XMLParser createXMLParser(XMLElement rootElement);

  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    XMLElement rootElementCopy = new XMLElement(rootElement);
    rootElementCopy.processChar(c);
    return createXMLParser(rootElementCopy);
  }

}
