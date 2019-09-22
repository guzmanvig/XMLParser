package xmlparser;

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

  XMLElement(XMLElement xmlElement) {

    this.children =  copyChildren(xmlElement.children);
    this.isComplete = xmlElement.isComplete;
    this.isStarted = xmlElement.isStarted;

    if (xmlElement.tagBeingProcessed != null && (xmlElement.tagBeingProcessed.isStarted())) {
      this.tagBeingProcessed = new XMLTag(xmlElement.tagBeingProcessed);
    } else {
      this.tagBeingProcessed = new XMLTag();
    }

    if(xmlElement.stringBeingProcessed != null && (xmlElement.stringBeingProcessed.isStarted())) {
      this.stringBeingProcessed = new XMLString(xmlElement.stringBeingProcessed);
    }  else {
      stringBeingProcessed = new XMLString();
    }

    if(xmlElement.childElementBeingProcessed != null && (xmlElement.childElementBeingProcessed.isStarted())) {
      this.childElementBeingProcessed = (XMLElement) children.get(xmlElement.children.indexOf(xmlElement.childElementBeingProcessed));
    }  else {
      childElementBeingProcessed = new XMLElement();
    }
  }

  private ArrayList<XMLElementComponent> copyChildren(ArrayList<XMLElementComponent> childrenToCopy) {
    ArrayList<XMLElementComponent> returnArray = new ArrayList<>();
    for (XMLElementComponent xmlChildElement : childrenToCopy){
      returnArray.add(xmlChildElement.createCopy());
    }
    return returnArray;
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
  public XMLElement createCopy() {
    return new XMLElement(this);
  }

  @Override
  public void processChar(char c) throws InvalidXMLException {
    if (childIsBeingProcessed()) {

      childElementBeingProcessed.processChar(c);

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

    } else if (!isStarted()) {
      isStarted = true;
      startTag(c);
    } else if (!isCompleted()) {
      startString(c);
    } else {
      throw new InvalidXMLException("Cannot add char. No open element");
    }
  }

  private boolean childIsBeingProcessed() {
    return childElementBeingProcessed.isStarted() && !childElementBeingProcessed.isCompleted();
  }

  private boolean tagIsBeingProcessed() {
    return tagBeingProcessed.isStarted() && !tagBeingProcessed.isCompleted();
  }

  private void checkIfValidEndTag() throws InvalidXMLException {
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
    } else if (!tagBeingProcessed.getTagName().equals(getStartTagName())) {
      childElementBeingProcessed = createChildAndAddTag(tagBeingProcessed);
      children.add(childElementBeingProcessed);
    } else {
      throw new InvalidXMLException("Cannot add child with same tag as parent");
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

  private boolean stringIsBeingProcessed() {
    return stringBeingProcessed.isStarted() && !stringBeingProcessed.isCompleted();
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
