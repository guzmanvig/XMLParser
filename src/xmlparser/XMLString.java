package xmlparser;

class XMLString implements XMLElementComponent {

  private String currentString;

  XMLString() {

  }

  XMLString(XMLString xmlString) {

  }

  @Override
  public void start(char startChar) throws InvalidXMLException {
    currentString = "" + startChar;
  }

  @Override
  public XMLString createCopy() {
    return null;
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
