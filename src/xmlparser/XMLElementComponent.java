package xmlparser;

/**
 * Objects that implement this interface are those that can be put inside an XMLElement.
 * This can be: Tags, Strings, and XMLElements
 */
interface XMLElementComponent {
  void start(char startChar) throws InvalidXMLException;
  void finish();
  boolean isStarted();
  boolean processChar(char c) throws InvalidXMLException;
}
