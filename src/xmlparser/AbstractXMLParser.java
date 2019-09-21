package xmlparser;

abstract class AbstractXMLParser implements XMLParser{

  final XMLElement rootElement = new XMLElement();

  abstract XMLParser createXMLParser(XMLElement rootElement);

  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    XMLElement resultingRootElement;
    if (!rootElement.isStarted()) {
      resultingRootElement = rootElement.start(c);
    } else {
      resultingRootElement = rootElement.processChar(c);
    }

    return createXMLParser(resultingRootElement);
  }

}
