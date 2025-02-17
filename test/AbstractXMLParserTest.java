import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public abstract class AbstractXMLParserTest {

  private XMLParser xmlParser;
  protected abstract XMLParser createXMLParser();


  public static final class XMLValidatorTest extends AbstractXMLParserTest {
    @Override
    protected XMLParser createXMLParser() {
      return new XMLValidator();
    }
  }

  public static final class XMLInfoLoggerTest extends AbstractXMLParserTest {
    @Override
    protected XMLParser createXMLParser() {
      return new XMLInfoLogger();
    }
  }

  @Before
  public void setup() {
    xmlParser = createXMLParser();
  }

  private XMLParser enterXMLInput(String xml) throws InvalidXMLException {
    XMLParser returnXMLParser = xmlParser;
    for(int i = 0, n = xml.length() ; i < n ; i++) {
      char c = xml.charAt(i);
      returnXMLParser = returnXMLParser.input(c);
    }
    return returnXMLParser;
  }

  @Test(expected = InvalidXMLException.class)
  public void testRootTagStartingWithChar() throws InvalidXMLException {
    enterXMLInput("a");
  }

  @Test(expected = InvalidXMLException.class)
  public void testRootTagStartingWithClosingTag() throws InvalidXMLException {
    enterXMLInput(">");
  }

  @Test(expected = InvalidXMLException.class)
  public void testImproperNesting0() throws InvalidXMLException {
    enterXMLInput("<root> text <tag>a</r");
  }

  @Test(expected = InvalidXMLException.class)
  public void testClosingWrongTag() throws InvalidXMLException {
   enterXMLInput("<root> text </t");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInString() throws InvalidXMLException { 
    enterXMLInput("<root> te>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidClosingOfTag() throws InvalidXMLException {
   enterXMLInput("<root> <txt<");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag0() throws InvalidXMLException {
    enterXMLInput("<*");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag1() throws InvalidXMLException {
    enterXMLInput("<ro ");
  }

  @Test(expected = InvalidXMLException.class)
  public void testDoubleOpeningOfTag() throws InvalidXMLException {
   enterXMLInput("<<");
  }

  @Test(expected = InvalidXMLException.class)
  public void testEmptyTag() throws InvalidXMLException {
   enterXMLInput("<>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart0() throws InvalidXMLException {
   enterXMLInput("<0");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart1() throws InvalidXMLException {
    enterXMLInput("<-");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartWithClosingTag() throws InvalidXMLException {
   enterXMLInput("</");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterAfterSpecialCharacter() throws InvalidXMLException {
    enterXMLInput("<h/t");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartTagAfterRoot() throws InvalidXMLException {
    enterXMLInput("<root> txt </root><");
  }

  @Test(expected = InvalidXMLException.class)
  public void testStartTextAfterRoot() throws InvalidXMLException {
   enterXMLInput("<root> txt </root>t");
  }

  private void assertOutputValidatorAndLogger(XMLParser parser,String expectedValidatorOutput,
      String expectedLoggerOutput) {
    if (parser instanceof XMLValidator)
      assertEquals(expectedValidatorOutput, parser.output());
    else if (parser instanceof XMLInfoLogger) {
      assertEquals(expectedLoggerOutput, parser.output());
    }
  }

  @Test()
  public void testOutputNoInput() {
    assertOutputValidatorAndLogger(xmlParser,"Status:Empty", "");
  }

  @Test()
  public void testValidOutput() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>txt</root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
        + "\nCharacters:txt"
        + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputNoText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputTextAndTagWithText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>txt<tag>txt2</tag></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nCharacters:txt2"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputTextAndTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>txt<tag></tag></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputDoubleNoText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root><tag></tag></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputDoubleChildTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root><tag></tag><tag2></tag2></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nEnded:tag2"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputDoubleChildTagWithText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root><tag></tag><tag2>txt</tag2></root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nCharacters:txt"
            + "\nEnded:tag2"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testValidOutputDoubleText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>text1<tag></tag>text2</root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
        "Started:root"
            + "\nCharacters:text1"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nCharacters:text2"
            + "\nEnded:root"
            + "\n");
  }

  @Test()
  public void testIncompleteRootStartTag0() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete", "");
  }

  @Test()
  public void testIncompleteRootStartTag1() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<r");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete", "");
  }

  @Test()
  public void testCompleteRootStartTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test()
  public void testIncompleteText() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test()
  public void testIncompleteChildStartTag0() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test()
  public void testIncompleteChildStartTag1() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root\n");
  }

  @Test()
  public void testCompleteChildStartTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag>");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteChildClosingTag0() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag><");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteChildClosingTag1() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteChildClosingTag2() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></tag");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\n");
  }

  @Test()
  public void testCompleteChildClosingTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></tag>");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteClosingRootTag0() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></tag><");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteClosingRootTag1() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></tag></");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteClosingRootTag2() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<root>a<tag></tag></root");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\n");
  }

  @Test()
  public void testIncompleteEndTag() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<html> <head>This is a header </head> <");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:html"
            + "\nCharacters: "
            + "\nStarted:head"
            + "\nCharacters:This is a header "
            + "\nEnded:head"
            + "\n");
  }

  @Test()
  public void testIncompleteTripleChild() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<html><div><p>");
    assertOutputValidatorAndLogger(resultParser,"Status:Incomplete",
        "Started:html"
            + "\nStarted:div"
            + "\nStarted:p"
            + "\n");
  }

  @Test()
  public void testValidSameNameChild() throws InvalidXMLException {
    XMLParser resultParser = enterXMLInput("<html><div><p></p><div></div></div></html>");
    assertOutputValidatorAndLogger(resultParser,"Status:Valid",
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

  @Test
  public void TestLongInput() throws InvalidXMLException {
    enterXMLInput("<a><a><a><a><a><a><a><a><a><a><a><a><a><a><a><a><a><a>"+
        "</a></a></a></a></a></a></a></a></a></a></a></a></a></a></a></a></a></a>");
  }




}
