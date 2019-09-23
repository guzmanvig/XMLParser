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
    processXMLElement(rootElement);
    return output;
  }

  private void processXMLElement(XMLElement element) {
    processChildren(element);
    XMLElement childBeingProcessed = element.getChildBeingProcessed();
    if (childBeingProcessed != null && !childBeingProcessed.isCompleted()) {
      processXMLElement(element.getChildBeingProcessed());
    }
  }

  private void processChildren(XMLElement element) {
    ArrayList<XMLElementComponent> children = element.getChildren();
    for (int i = 0; i < children.size(); i++) {
      XMLElementComponent child = children.get(i);
      if (child instanceof XMLElement) {
        processXMLElement((XMLElement) child);
      } else if (child instanceof XMLTag) {
        processXMLTag((XMLTag) child);
      } else if (child instanceof XMLString) {
        // If there is a next complete element, string is valid
        if (i != children.size() - 1) {
          processXMLString((XMLString) child);
          // If the processing element has already a starting tag
        } else if (childBeingProcessedHasValidStartTag(element.getChildBeingProcessed())) {
          processXMLString((XMLString) child);
        }
      }
    }
  }

  private void processXMLTag(XMLTag tag) {
    if (tag.isStartTag()) {
      appendToOutput(START_TAG_STRING + tag.getTagName());
    } else {
      appendToOutput(END_TAG_STRING + tag.getTagName());
    }
  }

  private void appendToOutput(String stringToAppend) {
    output = output + stringToAppend + "\n";
  }

  private void processXMLString(XMLString string) {
    appendToOutput(CHARACTERS_STRING + string.getString());
  }

  private boolean childBeingProcessedHasValidStartTag(XMLElement childBeingProcessed) {
    if (childBeingProcessed == null) {
      return false;
    } else {
      return (!childBeingProcessed.isCompleted() && childBeingProcessed.getChildren().size() != 0);
    }
  }

}
