/**
 * Objects that implement this interface are those that can be put inside an XMLElement.
 * This can be: Tags, Strings, and XMLElements
 */
interface XMLElementComponent {

  /**
   * An element has started when it has at least one valid character in it.
   * @return the element has started.
   */
  boolean isStarted();

  /**
   * An element is complete when the input is such that it can be completely determined from start
   * to finish.
   * @return the element is complete.
   */
  boolean isCompleted();

  /**
   * The element processes a char and make any changes accordingly.
   * @param c the char to be processed,
   * @throws InvalidXMLException when the character is invalid in the current state of the element
   */
  void processChar(char c) throws InvalidXMLException;

}
