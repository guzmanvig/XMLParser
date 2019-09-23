import java.util.ArrayList;

/**
 * Class that implements a XMLParser.
 * This class prints a detailed output for the current input.
 */
public final class XMLInfoLogger extends AbstractXMLParser {

  private static final String START_TAG_STRING = "Started:";
  private static final String END_TAG_STRING = "Ended:";
  private static final String CHARACTERS_STRING = "Characters:";

  private String output;


  /**
   * Prints the input. It only prints the complete determined elements so far.
   * If the element is a start tag it prints: Started:tagName\n
   * If the element is a string it prints: Characters:string\n
   * If the element is a end tag it prints: Ended:tagName\n
   * @return the string that shows the current parsed XML.
   */
  @Override
  public String output() {
    output = "";
    obtainOutputForXMLElement(rootElement);
    return output;
  }

  private void obtainOutputForXMLElement(XMLElement element) {
    // Get the output for all the complete children
    obtainOutputOfChildren(element);

    // If there is a child being processed, get the output for that child
    XMLElement childBeingProcessed = element.getChildBeingProcessed();
    if (childBeingProcessed != null && !childBeingProcessed.isCompleted()) {
      obtainOutputForXMLElement(childBeingProcessed);
    }
  }

  private void obtainOutputOfChildren(XMLElement element) {
    ArrayList<XMLElementComponent> children = element.getChildren();
    for (int i = 0; i < children.size(); i++) {
      XMLElementComponent child = children.get(i);
      if (child instanceof XMLElement) {
        obtainOutputForXMLElement((XMLElement) child);
      } else if (child instanceof XMLTag) {
        obtainOutputForXMLTag((XMLTag) child);
      } else if (child instanceof XMLString) {
        // If there is a next complete element, string is shown in the output
        if (i != children.size() - 1) {
          obtainOutputForXMLString((XMLString) child);
          // If the there is a complete element in the child being processed, also show it
        } else if (elementHasAtLeastOneCompleteChild(element.getChildBeingProcessed())) {
          obtainOutputForXMLString((XMLString) child);
        }
      }
    }
  }

  private void obtainOutputForXMLTag(XMLTag tag) {
    if (tag.isStartTag()) {
      appendToOutput(START_TAG_STRING + tag.getName());
    } else {
      appendToOutput(END_TAG_STRING + tag.getName());
    }
  }

  private void appendToOutput(String stringToAppend) {
    output = output + stringToAppend + "\n";
  }

  private void obtainOutputForXMLString(XMLString string) {
    appendToOutput(CHARACTERS_STRING + string.getString());
  }

  private boolean elementHasAtLeastOneCompleteChild(XMLElement element) {
    if (element == null) {
      return false;
    } else {
      return (!element.isCompleted() && element.getChildren().size() != 0);
    }
  }

}
