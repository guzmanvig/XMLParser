package xmlparser;

public final class XMLInfoLogger extends AbstractXMLParser{

  XMLInfoLogger() {
    super(new XMLElement());
  }

  XMLInfoLogger(XMLElement rootElement) {
    super(rootElement);
  }

  @Override
  XMLParser createXMLParser(XMLElement rootElement) {
    return new XMLInfoLogger(rootElement);
  }

  @Override
  public String output() {
    return null;
  }
}
