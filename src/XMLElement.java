import java.util.ArrayList;


/**
 * Class that represents an XML element implementing the XMLElementComponents interface.
 * This is element has children. They can be tags (own start and end tag), strings,
 * or more XML elements.
 * The children are complete elements, meaning that the input is such, that they can be completely
 * determined.
 * The XMLElement can also have children being processed,
 * these are stored in separate variables, and are added to the children once the input
 * completes them.
 */
class XMLElement implements XMLElementComponent {

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

  /**
   * The element has started then is processing one element.
   * @return the element has started
   */
  @Override
  public boolean isStarted() {
    return isStarted;
  }

  /**
   * An element is completed when is has successfully processed a start tag and its correspondent
   * end tag.
   * @return the element is completed.
   */
  @Override
  public boolean isCompleted() {
    return isComplete;
  }

  /**
   * Processes a char. This will either create a component to be processed, or
   * it will pass it to the current component being processed.
   * If there was a component being processed, and the char makes it complete, it will process it
   * accordingly.
   * @param c the char to be processed,
   * @throws InvalidXMLException if the character cannot be processed since it will not form a valid
   *                              xml element.
   */
  @Override
  public void processChar(char c) throws InvalidXMLException {
    if (childIsBeingProcessed()) {

      childBeingProcessingProcessChar(c);

    } else if (tagIsBeingProcessed()) {

      tagBeingProcessedProcessChar(c);

    } else if (stringIsBeingProcessed()) {

      stringBeingProcessedProcessChar(c);

    } else if (isStarted && !isComplete) {

      startProcessingChildTagOrString(c);

    } else if (!isStarted && isNotSpaceChar(c)) {

      startProcessingOwnStartTag(c);

    } else if (isNotSpaceChar(c)) {
      throw new InvalidXMLException("Cannot add char to a closed XML");
    }
  }

  private boolean childIsBeingProcessed() {
    return childElementBeingProcessed != null
        && childElementBeingProcessed.isStarted()
        && !childElementBeingProcessed.isCompleted();
  }

  private void childBeingProcessingProcessChar(char c) throws InvalidXMLException {
    childElementBeingProcessed.processChar(c);
    if (childElementBeingProcessed.isCompleted()) {
      children.add(childElementBeingProcessed);
    }
  }

  private boolean tagIsBeingProcessed() {
    return tagBeingProcessed != null
        && tagBeingProcessed.isStarted()
        && !tagBeingProcessed.isCompleted();
  }

  private void tagBeingProcessedProcessChar(char c) throws InvalidXMLException {
    tagBeingProcessed.processChar(c);
    if (!tagBeingProcessed.isStartTag()) {
      checkIfValidCandidateToOwnEndTag();
    }
    if (tagBeingProcessed.isCompleted()) {
      finishProcessingTag();
    }
  }

  private void checkIfValidCandidateToOwnEndTag() throws InvalidXMLException {
    if (children.isEmpty()) {
      throw new InvalidXMLException("Element cannot start with end tag");
    }
    String thisElementStartTagName = getStartTagName();
    String currentlyEndTagName = tagBeingProcessed.getName();
    if (!currentlyEndTagName.equals("")
        && !thisElementStartTagName.startsWith(currentlyEndTagName)) {
      throw new InvalidXMLException("Ending tag should have the same name as starting tag");
    }
  }

  private String getStartTagName() {
    return ((XMLTag)children.get(0)).getName();
  }

  private void finishProcessingTag() throws InvalidXMLException {
    if (tagBeingProcessed.isStartTag()) {
      // Can be this element own start tag or a children's start tag
      finishProcessingStartTag();
    } else {
      // Can only be this element own end tag
      finishProcessingOwnEndTag();
    }
  }

  private void finishProcessingStartTag() throws InvalidXMLException {
    // If the element had no children, it's this element own start tag
    if (children.isEmpty()) {
      children.add(tagBeingProcessed);
    } else {
      // If not, is the start tag of a new element
      childElementBeingProcessed = createChildElementAndAddTagToIt(tagBeingProcessed);
    }
  }

  private XMLElement createChildElementAndAddTagToIt(XMLTag startTag) {
    XMLElement childElement = new XMLElement();
    childElement.children.add(startTag);
    childElement.isStarted = true;
    return childElement;
  }

  private void finishProcessingOwnEndTag() throws InvalidXMLException {
    if (tagBeingProcessed.getName().equals(getStartTagName())) {
      children.add(tagBeingProcessed);
      isComplete = true;
    } else {
      throw new InvalidXMLException("Ending tag should have the same name as starting tag");
    }
  }

  private boolean stringIsBeingProcessed() {
    return stringBeingProcessed != null
        && stringBeingProcessed.isStarted()
        && !stringBeingProcessed.isCompleted();
  }

  private void stringBeingProcessedProcessChar(char c) throws InvalidXMLException {
    stringBeingProcessed.processChar(c);
    if (stringBeingProcessed.isCompleted()) {
      finishProcessingString(c);
    }
  }

  private void finishProcessingString(char lastProcessedCharacter) throws InvalidXMLException {
    children.add(stringBeingProcessed);
    // If the string finished it's because a < was processed, meaning the start of a tag.
    startTag(lastProcessedCharacter);
  }

  private void startTag(char startChar) throws InvalidXMLException {
    tagBeingProcessed = new XMLTag();
    tagBeingProcessed.processChar(startChar);
  }

  private static boolean isNotSpaceChar(char c) {
    return c != ' ';
  }

  private void startProcessingChildTagOrString(char startChar) throws InvalidXMLException {
    if (XMLTag.isStartSpecialCharacter(startChar)) {
      startTag(startChar);
    } else {
      startString(startChar);
    }
  }

  private void startString(char startChar) throws InvalidXMLException {
    stringBeingProcessed = new XMLString();
    stringBeingProcessed.processChar(startChar);
  }

  private void startProcessingOwnStartTag(char startChar) throws InvalidXMLException {
    isStarted = true;
    startTag(startChar);
  }

}
