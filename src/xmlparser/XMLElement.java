package xmlparser;

import java.util.ArrayList;


class XMLElement implements XMLElementComponent{

  private ArrayList<XMLElementComponent> children;
  private boolean isComplete;

  private XMLTag tagBeingProcessed;
  private XMLString stringBeingProcessed;
  private XMLElement childElementBeingProcessed;


  XMLElement() {

    children = new ArrayList<>();
    isComplete = false;
    tagBeingProcessed = new XMLTag();
    stringBeingProcessed = new XMLString();
    childElementBeingProcessed = new XMLElement();
  }

  XMLElement(XMLElement xmlElement) {

    this.children =  copyChildren(xmlElement.children);
    this.isComplete = xmlElement.isComplete;
    this.tagBeingProcessed = new XMLTag(xmlElement.tagBeingProcessed);
    this.stringBeingProcessed = new XMLString(xmlElement.stringBeingProcessed);
    this.childElementBeingProcessed = new XMLElement(xmlElement.childElementBeingProcessed);
  }

  private ArrayList<XMLElementComponent> copyChildren(ArrayList<XMLElementComponent> childrenToCopy) {
    ArrayList<XMLElementComponent> returnArray = new ArrayList<>();
    for (XMLElementComponent xmlChildElement : childrenToCopy){
      returnArray.add(xmlChildElement.createCopy());
    }
    return returnArray;
  }


  @Override
  public void start(char startChar) throws InvalidXMLException {
    tagBeingProcessed.start(startChar);
  }

  @Override
  public boolean isStarted() {
    return false;
  }


  @Override
  public XMLElement createCopy() {
    return new XMLElement(this);
  }

  @Override
  public boolean processChar(char c) throws InvalidXMLException {

    if (childIsBeingProcessed()) {
      childElementBeingProcessed.processChar(c);
    } else {

      if (tagIsBeingProcessed()){
        boolean finished = tagBeingProcessed.processChar(c);
        if (finished){ //TODO: shouldnt check when is finished but on each letter compare with the start tag
          finishProcessingTag();
        }

      } else if (stringIsBeingProcessed()) {
        boolean finished = stringBeingProcessed.processChar(c);
        if (finished) {
          finishProcessingString(c);
        }

      } else {
        throw new InvalidXMLException("XML ended, cannot add char");
      }

    }

    return isComplete;
  }

  private boolean childIsBeingProcessed() {
    return childElementBeingProcessed.isStarted();
  }

  private boolean tagIsBeingProcessed() {
    return tagBeingProcessed.isStarted();
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
      children.add(createChildAndAddTag(tagBeingProcessed));
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
    return stringBeingProcessed.isStarted();
  }

  private void finishProcessingString(char lastProcessedCharacter) throws InvalidXMLException {
    tagBeingProcessed.start(lastProcessedCharacter);

  }

}
