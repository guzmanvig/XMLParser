import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Abstract a way for testing the XMLValidator and XMLInfoLogger at the same time.
 * The input method is tested for both in the same way, i.e.: it expects the same results,
 * but the output method is tested differently for each.
 */
public abstract class AbstractXMLParserTest {

  protected abstract XMLParser createXMLParser();

  /**
   * Class that creates an instance of a XMLValidator to be tested.
   */
  public static final class XMLValidatorTest extends AbstractXMLParserTest {
    @Override
    protected XMLParser createXMLParser() {
      return new XMLValidator();
    }
  }

  /**
   * Class that creates an instance of a XMLInfoLogger to be tested.
   */
  public static final class XMLInfoLoggerTest extends AbstractXMLParserTest {
    @Override
    protected XMLParser createXMLParser() {
      return new XMLInfoLogger();
    }
  }
  

  private void enterXMLInput(XMLParser parser, String xml) throws InvalidXMLException {
    for (int i = 0, n = xml.length() ; i < n ; i++) {
      char c = xml.charAt(i);
      parser.input(c);
    }
  }

  @Test(expected = InvalidXMLException.class)
  public void testRootTagStartingWithChar() throws InvalidXMLException {
    enterXMLInput(createXMLParser(), "a");
  }

  @Test(expected = InvalidXMLException.class)
  public void testRootTagStartingWithClosingTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),">");
  }

  @Test(expected = InvalidXMLException.class)
  public void testImproperNesting0() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<root> text <tag>a</r");
  }

  @Test(expected = InvalidXMLException.class)
  public void testClosingWrongTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<root> text </t");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInString() throws InvalidXMLException { 
    enterXMLInput(createXMLParser(),"<root> te>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidClosingOfTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<root> <txt<");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag0() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<*");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag1() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<ro ");
  }

  @Test(expected = InvalidXMLException.class)
  public void testDoubleOpeningOfTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<<");
  }

  @Test(expected = InvalidXMLException.class)
  public void testEmptyTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart0() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<0");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart1() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<-");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartWithClosingTag() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"</");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterAfterSpecialCharacter() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<h/t");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartTagAfterRoot() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<root> txt </root><");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartTextAfterRoot() throws InvalidXMLException {
    enterXMLInput(createXMLParser(),"<root> txt </root>t");
  }

  private void assertOutputValidatorAndLogger(XMLParser parser,String expectedValidatorOutput,
      String expectedLoggerOutput) {
    if (parser instanceof XMLValidator) {
      assertEquals(expectedValidatorOutput, parser.output());
    } else if (parser instanceof XMLInfoLogger) {
      assertEquals(expectedLoggerOutput, parser.output());
    }
  }

  @Test
  public void testOutputNoInput() {
    XMLParser parser = createXMLParser();
    assertOutputValidatorAndLogger(parser,"Status:Empty", "");
  }

  @Test
  public void testValidOutput() throws InvalidXMLException {
    XMLParser parser = createXMLParser();
    enterXMLInput(parser, "<root>txt</root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
        + "\nCharacters:txt"
        + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputWithSpaces() throws InvalidXMLException {
    XMLParser parser = createXMLParser();
    enterXMLInput(parser,  " <html> ");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:html"
            + "\n");
  }

  @Test
  public void testValidOutputWithSpaces2() throws InvalidXMLException {
    XMLParser parser = createXMLParser();
    enterXMLInput(parser,  " <root></root> ");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputNoText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputTextAndTagWithText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>txt<tag>txt2</tag></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nCharacters:txt2"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputTextAndTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>txt<tag></tag></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputDoubleNoText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root><tag></tag></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputDoubleChildTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root><tag></tag><tag2></tag2></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nEnded:tag2"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputDoubleChildTagWithText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root><tag></tag><tag2>txt</tag2></root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nCharacters:txt"
            + "\nEnded:tag2"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testValidOutputDoubleText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>text1<tag></tag>text2</root>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:root"
            + "\nCharacters:text1"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nCharacters:text2"
            + "\nEnded:root"
            + "\n");
  }

  @Test
  public void testIncompleteRootStartTag0() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete", "");
  }

  @Test
  public void testIncompleteRootStartTag1() throws InvalidXMLException {
    XMLParser parser = createXMLParser();
    enterXMLInput(parser, "<r");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete", "");
  }

  @Test
  public void testCompleteRootStartTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test
  public void testIncompleteText() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test
  public void testIncompleteChildStartTag0() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test
  public void testIncompleteChildStartTag1() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test
  public void testCompleteChildStartTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag>");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test
  public void testIncompleteChildClosingTag0() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag><");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test
  public void testIncompleteChildClosingTag1() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test
  public void testIncompleteChildClosingTag2() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></tag");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test
  public void testCompleteChildClosingTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></tag>");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test
  public void testIncompleteClosingRootTag0() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></tag><");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test
  public void testIncompleteClosingRootTag1() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></tag></");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test
  public void testIncompleteClosingRootTag2() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<root>a<tag></tag></root");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test
  public void testIncompleteEndTag() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<html> <head>This is a header </head> <");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:html"
            + "\nCharacters: "
            + "\nStarted:head"
            + "\nCharacters:This is a header "
            + "\nEnded:head"
            + "\n");
  }

  @Test
  public void testIncompleteTripleChild() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<html><div><p>");
    assertOutputValidatorAndLogger(parser,"Status:Incomplete",
        "Started:html"
            + "\nStarted:div"
            + "\nStarted:p"
            + "\n");
  }

  @Test
  public void testValidSameNameChild() throws InvalidXMLException {
    XMLParser parser = createXMLParser(); 
    enterXMLInput(parser, "<html><div><p></p><div></div></div></html>");
    assertOutputValidatorAndLogger(parser,"Status:Valid",
        "Started:html"
            + "\nStarted:div"
            + "\nStarted:p"
            + "\nEnded:p"
            + "\nStarted:div"
            + "\nEnded:div"
            + "\nEnded:div"
            + "\nEnded:html"
            + "\n");
  }
}
