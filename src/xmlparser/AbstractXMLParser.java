package xmlparser;

abstract class AbstractXMLParser implements XMLParser{

  final XMLElement rootElement;

  AbstractXMLParser() {
    this(new XMLElement());
  }

  AbstractXMLParser(XMLElement rootElement) {
    this.rootElement = rootElement;
  }

  abstract XMLParser createXMLParser(XMLElement rootElement);

  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    XMLElement rootElementCopy = new XMLElement(rootElement);
    if (!rootElementCopy.isStarted()) {
      rootElementCopy.start(c);
    } else {
      rootElementCopy.processChar(c);
    }

    return createXMLParser(rootElementCopy);
  }

}
