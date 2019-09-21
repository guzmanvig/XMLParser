package xmlparser;

public class XMLString implements XMLElementComponent {

  XMLString() {

  }

  XMLString(XMLString xmlString) {

  }

  @Override
  public XMLString start(char startChar) throws InvalidXMLException {
      return null;
  }

  public void finish() {

  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public XMLString processChar(char c) throws InvalidXMLException {
    return null;
  }
}
