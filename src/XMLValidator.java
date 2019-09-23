public final class XMLValidator extends AbstractXMLParser {

  private static final String EMPTY_STATUS = "Status:Empty";
  private static final String INCOMPLETE_STATUS = "Status:Incomplete";
  private static final String VALID_STATUS =  "Status:Valid";

  XMLValidator(){

  }

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
