/**
 * Objects that implement this interface are those that can be put inside an XMLElement.
 * This can be: Tags, Strings, and XMLElements
 */
interface XMLElementComponent {
  boolean isStarted();
  boolean isCompleted();
  void processChar(char c) throws InvalidXMLException;
}
