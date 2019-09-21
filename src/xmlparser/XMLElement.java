package xmlparser;

import java.util.ArrayList;

class XMLElement implements XMLElementComponent{

  private ArrayList<XMLElement> children;
  private  boolean isComplete;

  private XMLTag tagBeingProcessed;
  private XMLString stringBeingProcessed;
  private XMLElement elementBeingProcessed;


  XMLElement() {
    children = new ArrayList<>();
    isComplete = false;
    tagBeingProcessed = new XMLTag();
    stringBeingProcessed = new XMLString();
    elementBeingProcessed = new XMLElement();
  }

  XMLElement(XMLElement xmlElement) {
    children = new ArrayList<>();
    for (XMLElement xmlChildElement : xmlElement.children){
      children.add(new XMLElement(xmlChildElement));
    }
    isComplete = xmlElement.isComplete;
    tagBeingProcessed = new XMLTag(xmlElement.tagBeingProcessed);
    stringBeingProcessed = new XMLString(xmlElement.stringBeingProcessed);
    elementBeingProcessed = new XMLElement(xmlElement.elementBeingProcessed);
  }



  private void startTag(char startChar) throws InvalidXMLException {
    tagBeingProcessed.start(startChar);
  }

  @Override
  public void start(char startChar) throws InvalidXMLException {
    startTag(startChar);
  }

  @Override
  public void finish() {

  }

  @Override
  public boolean isStarted() {
    return false;
  }

  @Override
  public boolean processChar(char c) throws InvalidXMLException {
    return false;
  }
}
