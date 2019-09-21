package xmlparser;

/**
 * Objects that implement this interface are those that can be put inside an XMLElement.
 * This can be: Tags, Strings, and XMLElements
 */
interface XMLElementComponent {
  XMLElementComponent start(char startChar) throws InvalidXMLException;
  XMLElementComponent createCopy();
  boolean isStarted();
  boolean isFinished();
  XMLElementComponent processChar(char c) throws InvalidXMLException;
}
