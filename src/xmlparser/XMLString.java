package xmlparser;

public class XMLString implements XMLElementComponent {

  XMLString() {

  }

  XMLString(XMLString xmlString) {

  }

  @Override
  public void start(char startChar) throws InvalidXMLException {

  }

  @Override
  public void finish() {

  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public boolean processChar(char c) throws InvalidXMLException {
    return false;
  }
}
