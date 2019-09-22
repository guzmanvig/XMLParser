package xmlparser;

import java.util.ArrayList;

public final class XMLInfoLogger extends AbstractXMLParser{

  private static final String START_TAG_STRING = "Started:";
  private static final String END_TAG_STRING = "Ended:";
  private static final String CHARACTERS_STRING = "Characters:";

  private String output = "";


  XMLInfoLogger() {
    super(new XMLElement());
  }

  XMLInfoLogger(XMLElement rootElement) {
    super(rootElement);
  }

  @Override
  XMLParser createXMLParser(XMLElement rootElement) {
    return new XMLInfoLogger(rootElement);
  }

  @Override
  public String output() {
    processXMLElement(rootElement);
    removeLastCR();
    return output;
  }

  private void processXMLElement(XMLElement element) {
    processChildren(element);
    XMLElement childBeingProcessed = element.getChildBeingProcessed();
    if (childBeingProcessed != null && !childBeingProcessed.isCompleted()) {
      processChildren(element.getChildBeingProcessed());
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

  private void removeLastCR(){
    if (output.length() != 0) {
      output = output.substring(0, output.length() -1);
    }
  }

  private void processXMLString(XMLString string) {
    appendToOutput(CHARACTERS_STRING + string.getString());
  }

  private boolean childBeingProcessedHasValidStartTag(XMLElement childBeingProcessed) {
    if (childBeingProcessed == null) {
      return false;
    } else {
      return (childBeingProcessed.getChildren().size() != 0);
    }
  }

}
