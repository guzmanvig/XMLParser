abstract class AbstractXMLParser implements XMLParser{

  protected XMLElement rootElement = new XMLElement();

  @Override
  public XMLParser input(char c) throws InvalidXMLException {
    rootElement.processChar(c);
    return this;
  }

}
