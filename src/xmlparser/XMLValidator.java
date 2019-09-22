package xmlparser;

public final class XMLValidator extends AbstractXMLParser {

  XMLValidator() {
    super(new XMLElement());
  }

  XMLValidator(XMLElement rootElement) {
    super(rootElement);
  }

  @Override
  XMLParser createXMLParser(XMLElement rootElement) {
    return new XMLValidator(rootElement);
  }

  @Override
  public String output() {
    return null;
  }
}
