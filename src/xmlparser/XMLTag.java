package xmlparser;

public class XMLTag implements XMLElementComponent {

  XMLTag() {

  }

  XMLTag(XMLTag xmlTag) {

  }

  private String getTagName(){
    return "";
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
