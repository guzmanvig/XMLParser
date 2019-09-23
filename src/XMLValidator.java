/**
 * Class that implements a XMLParser.
 * This class prints a the current state of the output.
 */
public final class XMLValidator extends AbstractXMLParser {

  private static final String EMPTY_STATUS = "Status:Empty";
  private static final String INCOMPLETE_STATUS = "Status:Incomplete";
  private static final String VALID_STATUS =  "Status:Valid";

  /**
   * Prints the input.
   * If the parsed XML is valid and complete, it prints: Status:Valid.
   * If the parsed XML is valid so far, but not complete, it prints: Status:Incomplete.
   * If the parser did not receive any input yet, it prints: Status:Empty.
   * @return the string that shows the current status of the XML.
   */
  @Override
  public String output() {
    if (!rootElement.isStarted()) {
      return EMPTY_STATUS;
    } else {
      if (rootElement.isCompleted()) {
        return VALID_STATUS;
      } else {
        return INCOMPLETE_STATUS;
      }
    }
  }
}
