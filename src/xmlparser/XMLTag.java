package xmlparser;

public class XMLTag implements XMLElementComponent {

  private final String currentTagString;

  XMLTag(String currentTagString) {
    this.currentTagString = currentTagString;
  }

  XMLTag(XMLTag xmlTag) {
    this(xmlTag.currentTagString);
  }

  XMLTag() {
    this("");
  }

  String getTagName(){
    // Remove the first '<' and the last '/>'
    return currentTagString.substring(1, currentTagString.length() - 3);
  }

  @Override
  public XMLTag start(char startChar) throws InvalidXMLException {
    if (startChar != '<')
      throw new InvalidXMLException("Invalid tag start character " + startChar);
    String resultingTag = currentTagString + startChar;
    return new XMLTag(resultingTag);
  }

  @Override
  public XMLTag createCopy() {
    return new XMLTag(this);
  }

  public boolean isStartTag() {
    return false;
  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public XMLTag processChar(char c) throws InvalidXMLException {
    return null;
  }
}
