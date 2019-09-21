package xmlparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class XMLElement implements XMLElementComponent{

  private final List<XMLElementComponent> children;
  private final boolean isComplete;

  private final XMLTag tagBeingProcessed;
  private final XMLString stringBeingProcessed;
  private final XMLElement childElementBeingProcessed;


  XMLElement() {

    children = Collections.unmodifiableList(new ArrayList<>());
    isComplete = false;
    tagBeingProcessed = new XMLTag();
    stringBeingProcessed = new XMLString();
    childElementBeingProcessed = new XMLElement();
  }

  XMLElement(List<XMLElementComponent> children, boolean isComplete, XMLTag tagBeingProcessed,
      XMLString stringBeingProcessed, XMLElement elementBeingProcessed) {

    this.children =  Collections.unmodifiableList(copyChildren(children));
    this.isComplete = isComplete;
    this.tagBeingProcessed = new XMLTag(tagBeingProcessed);
    this.stringBeingProcessed = new XMLString(stringBeingProcessed);
    this.childElementBeingProcessed = new XMLElement(elementBeingProcessed);
  }

  XMLElement(XMLElement xmlElement) {
    this(xmlElement.children, xmlElement.isComplete, xmlElement.tagBeingProcessed,
        xmlElement.stringBeingProcessed, xmlElement.childElementBeingProcessed);
  }

  private ArrayList<XMLElementComponent> copyChildren(List<XMLElementComponent> childrenToCopy) {
    ArrayList<XMLElementComponent> returnArray = new ArrayList<>();
    for (XMLElementComponent xmlChildElement : childrenToCopy){
      returnArray.add(xmlChildElement.createCopy());
    }
    return returnArray;
  }



  private XMLElement startTag(char startChar) throws InvalidXMLException {
    XMLTag resultingTagBeingProcessed = tagBeingProcessed.start(startChar);
    return new XMLElement(this.children, this.isComplete, resultingTagBeingProcessed,
        this.stringBeingProcessed, this.childElementBeingProcessed);
  }

  @Override
  public XMLElement start(char startChar) throws InvalidXMLException {
    return startTag(startChar);
  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public XMLElement createCopy() {
    return new XMLElement(this);
  }

  @Override
  public XMLElement processChar(char c) throws InvalidXMLException {
    XMLElement returnElement;

    if (childIsBeingProcessed()) {
      returnElement = childElementBeingProcessed.processChar(c);
    } else {

      if (tagIsBeingProcessed()){
        XMLTag processedTag = tagBeingProcessed.processChar(c);
        if (processedTag.isFinished()){ //TODO: shouldnt check when is finished but on each letter compare with the start tag
          ArrayList<XMLElementComponent> newChildren = finishProcessingTag(processedTag);
          returnElement = new XMLElement(newChildren, isComplete, processedTag,
              stringBeingProcessed, childElementBeingProcessed);
        } else {
          returnElement = new XMLElement(children, isComplete, processedTag,
              stringBeingProcessed, childElementBeingProcessed);
        }

      } else if (stringIsBeingProcessed()) {
        XMLString processedString = stringBeingProcessed.processChar(c);
        if (processedString.isFinished()) {
          finishPorcessingString(processedString);
        }
      } else if (!isComplete)  {
        startTag(c)
      } else {
        throw ex
      }

    }
  }

  boolean tagIsBeingProcessed() {
    return tagBeingProcessed.isStarted() && !tagBeingProcessed.isFinished();
  }

  ArrayList<XMLElementComponent> finishProcessingTag(XMLTag tag) throws InvalidXMLException {
    if (tag.isStartTag()) {
      return finishProcessingStartTag(tag);
    } else {
      return finishProcessingEndTag(tag);
    }
  }

  ArrayList<XMLElementComponent> finishProcessingStartTag(XMLTag tag) throws InvalidXMLException {
    ArrayList<XMLElementComponent> childrenCopy = copyChildren(children);
    if (childrenCopy.isEmpty()) {
      childrenCopy.add(tag);
    } else if (!tag.getTagName().equals(getStartTagName())) {
      childrenCopy.add(createChild(tag));
    } else {
      throw new InvalidXMLException("Cannot add child with same tag as parent");
    }
    return childrenCopy;
  }

  private String getStartTagName() {
    return ((XMLTag)children.get(0)).getTagName();
  }

  private XMLElement createChild(XMLTag startTag) {
    ArrayList<XMLElementComponent> children = new ArrayList<>();
    children.add(startTag);
    boolean isComplete = false;
    XMLTag tagBeingProcessed = new XMLTag();
    XMLString stringBeingProcessed = new XMLString();
    XMLElement childElementBeingProcessed = new XMLElement();
    return new XMLElement(children, isComplete, tagBeingProcessed,
        stringBeingProcessed, childElementBeingProcessed);
  }


  ArrayList<XMLElementComponent> finishProcessingEndTag(XMLTag tag) throws InvalidXMLException {
    ArrayList<XMLElementComponent> childrenCopy = copyChildren(children);
    if (tag.getTagName().equals(getStartTagName())) {
      childrenCopy.add(tag);
    } else {
      throw new InvalidXMLException("Ending tag should have the same name as starting tag");
    }
    return childrenCopy;
  }

  boolean stringIsBeingProcessed() {
    return stringBeingProcessed.isStarted() && !stringBeingProcessed.isFinished();
  }

  ArrayList<XMLElementComponent> finishProcessingString(XMLString string) {
    ArrayList<XMLElementComponent> childrenCopy = copyChildren(children);
    childrenCopy.add(string)


  }



  boolean childIsBeingProcessed() {
    return childElementBeingProcessed.isStarted() && !childElementBeingProcessed.isFinished();
  }





}
