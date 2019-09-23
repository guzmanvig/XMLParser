import java.util.ArrayList;


class XMLElement implements XMLElementComponent{

  private ArrayList<XMLElementComponent> children;
  private boolean isComplete;
  private boolean isStarted;

  private XMLTag tagBeingProcessed;
  private XMLString stringBeingProcessed;
  private XMLElement childElementBeingProcessed;


  XMLElement() {
    children = new ArrayList<>();
    isComplete = false;
    isStarted = false;
  }

  ArrayList<XMLElementComponent> getChildren() {
    return children;
  }

  XMLElement getChildBeingProcessed() {
    return childElementBeingProcessed;
  }

  @Override
  public boolean isStarted() {
    return isStarted;
  }

  @Override
  public boolean isCompleted() {
    return isComplete;
  }

  @Override
  public void processChar(char c) throws InvalidXMLException {
    if (childIsBeingProcessed()) {

      childElementBeingProcessed.processChar(c);
      if (childElementBeingProcessed.isCompleted()) {
        children.add(childElementBeingProcessed);
      }

    } else if (tagIsBeingProcessed()){

      tagBeingProcessed.processChar(c);
      if (!tagBeingProcessed.isStartTag()) {
        checkIfValidEndTag();
      }
      if (tagBeingProcessed.isCompleted()){
        finishProcessingTag();
      }

    } else if (stringIsBeingProcessed()) {

      stringBeingProcessed.processChar(c);
      if (stringBeingProcessed.isCompleted()) {
        finishProcessingString(c);
      }

    } else if (!isStarted() && c != ' ') {
      isStarted = true;
      startTag(c);
    } else if (isStarted && !isCompleted()) {
      if (XMLTag.isStartSpecialCharacter(c)) {
        startTag(c);
      } else {
        startString(c);
      }
    } else if (c != ' '){
      throw new InvalidXMLException("Cannot add char. No open element");
    }
  }

  private boolean childIsBeingProcessed() {
    return childElementBeingProcessed != null
        && childElementBeingProcessed.isStarted()
        && !childElementBeingProcessed.isCompleted();
  }

  private boolean tagIsBeingProcessed() {
    return tagBeingProcessed != null
        && tagBeingProcessed.isStarted()
        && !tagBeingProcessed.isCompleted();
  }

  private boolean stringIsBeingProcessed() {
    return stringBeingProcessed != null
        && stringBeingProcessed.isStarted()
        && !stringBeingProcessed.isCompleted();
  }


  private void checkIfValidEndTag() throws InvalidXMLException {
    if (children.isEmpty()) {
      throw new InvalidXMLException("Element cannot start with end tag");
    }
    XMLTag startTag = (XMLTag) children.get(0);
    String startTagName = startTag.getTagName();
    String currentlyEndTagName = tagBeingProcessed.getTagName();
    if (!currentlyEndTagName.equals("")
        && (!currentlyEndTagName.equals(startTagName.substring(0, currentlyEndTagName.length()))
        || currentlyEndTagName.length() > startTagName.length())){
      throw new InvalidXMLException("Invalid end tag: " + currentlyEndTagName);
    }
  }

  private void finishProcessingTag() throws InvalidXMLException {
    if (tagBeingProcessed.isStartTag()) {
      finishProcessingStartTag();
    } else {
      finishProcessingEndTag();
    }
  }

  private void finishProcessingStartTag() throws InvalidXMLException {
    if (children.isEmpty()) {
      children.add(tagBeingProcessed);
    } else {
      childElementBeingProcessed = createChildAndAddTag(tagBeingProcessed);
    }
  }

  private String getStartTagName() {
    return ((XMLTag)children.get(0)).getTagName();
  }

  private XMLElement createChildAndAddTag(XMLTag startTag) {
    XMLElement childElement = new XMLElement();
    childElement.children.add(startTag);
    childElement.isStarted = true;
    return childElement;
  }


  private void finishProcessingEndTag() throws InvalidXMLException {
    if (tagBeingProcessed.getTagName().equals(getStartTagName())) {
      children.add(tagBeingProcessed);
      isComplete = true;
    } else {
      throw new InvalidXMLException("Ending tag should have the same name as starting tag");
    }
  }

  private void finishProcessingString(char lastProcessedCharacter) throws InvalidXMLException {
    children.add(stringBeingProcessed);
    startTag(lastProcessedCharacter);

  }

  private void startTag(char startChar) throws InvalidXMLException {
    tagBeingProcessed = new XMLTag();
    tagBeingProcessed.processChar(startChar);
  }

  private void startString(char startChar) throws InvalidXMLException {
    stringBeingProcessed = new XMLString();
    stringBeingProcessed.processChar(startChar);
  }

}
