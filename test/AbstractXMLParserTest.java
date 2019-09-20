
import org.junit.Before;
import org.junit.Test;
import xmlparser.InvalidXMLException;
import xmlparser.XMLInfoLogger;
import xmlparser.XMLParser;
import xmlparser.XMLValidator;

import static org.junit.Assert.assertEquals;


public abstract class AbstractXMLParserTest {

  private XMLParser xmlParser;
  protected abstract XMLParser createXMLParser();


  public static final class XMLValidatorTest extends AbstractXMLParserTest {
    @Override
    protected XMLParser createXMLParser() {
      System.out.print("Create XMLValidator");
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
  public void testNotStartingWithTag0() throws InvalidXMLException {
    enterXMLInput("a");
  }

  @Test(expected = InvalidXMLException.class)
  public void testNotStartingWithTag1() throws InvalidXMLException {
    enterXMLInput(">");
  }

  @Test(expected = InvalidXMLException.class)
  public void testNotStartingWithTag2() throws InvalidXMLException {
    enterXMLInput("/");
  }

  @Test(expected = InvalidXMLException.class)
  public void testImproperNesting0() throws InvalidXMLException {
    enterXMLInput("<root> text <tag> text  </root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testImproperNesting1() throws InvalidXMLException {
    enterXMLInput("<root> text </tag>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInString0() throws InvalidXMLException {
    enterXMLInput("<root> te<xt </root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInString1() throws InvalidXMLException {
    enterXMLInput("<root> <txt </root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag0() throws InvalidXMLException {
    enterXMLInput("<*root> txt </*root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTag1() throws InvalidXMLException {
    enterXMLInput("<ro ot> txt </ro ot>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart0() throws InvalidXMLException {
    enterXMLInput("<0root> txt </0root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testInvalidCharacterInTagStart1() throws InvalidXMLException {
    enterXMLInput("<-root> txt </-root>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testMoreThanOneRoot() throws InvalidXMLException {
    enterXMLInput("<root> txt </root> <tag> txt </tag>");
  }

  @Test(expected = InvalidXMLException.class)
  public void testTextPassedRoot() throws InvalidXMLException {
    enterXMLInput("<root> txt </root> txt");
  }

  private void assertOutputValidatorAndLogger(String expectedValidatorOutput, String expectedLoggerOutput) {
    if (xmlParser instanceof XMLValidator)
      assertEquals(expectedValidatorOutput, xmlParser.output());
    else if (xmlParser instanceof  XMLInfoLogger) {
      assertEquals(expectedLoggerOutput, xmlParser.output());
    }
  }

  @Test()
  public void testOutputNoInput() {
    assertOutputValidatorAndLogger("Status:Empty", "");
  }

  @Test()
  public void testValidOutput0() throws InvalidXMLException {
    enterXMLInput("<root>txt</root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
        + "\nCharacters:txt"
        + "\nEnded:root");
  }

  @Test()
  public void testValidOutput1() throws InvalidXMLException {
    enterXMLInput("<root></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nEnded:root");
  }

  @Test()
  public void testValidOutput2() throws InvalidXMLException {
    enterXMLInput("<root>txt<tag>txt2</tag></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nCharacters:txt2"
            + "\nEnded:tag"
            + "\nEnded:root");
  }

  @Test()
  public void testValidOutput3() throws InvalidXMLException {
    enterXMLInput("<root>txt<tag></tag></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nCharacters:txt"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root");
  }

  @Test()
  public void testValidOutput4() throws InvalidXMLException {
    enterXMLInput("<root><tag></tag></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nEnded:root");
  }

  @Test()
  public void testValidOutput5() throws InvalidXMLException {
    enterXMLInput("<root><tag></tag><tag2></tag2></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nEnded:tag2"
            + "\nEnded:root");
  }

  @Test()
  public void testValidOutput6() throws InvalidXMLException {
    enterXMLInput("<root><tag></tag><tag2>txt</tag2></root>");
    assertOutputValidatorAndLogger("Status:Valid",
        "Started:root"
            + "\nStarted:tag"
            + "\nEnded:tag"
            + "\nStarted:tag2"
            + "\nCharacters:txt"
            + "\nEnded:tag2"
            + "\nEnded:root");
  }

  @Test()
  public void testIncompleteOutput0() throws InvalidXMLException {
    enterXMLInput("<");
    assertOutputValidatorAndLogger("Status:Incomplete", "");
  }

  @Test()
  public void testIncompleteOutput1() throws InvalidXMLException {
    enterXMLInput("<r");
    assertOutputValidatorAndLogger("Status:Incomplete", "");
  }

  @Test()
  public void testIncompleteOutput2() throws InvalidXMLException {
    enterXMLInput("<root>");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root");
  }

  @Test()
  public void testIncompleteOutput3() throws InvalidXMLException {
    enterXMLInput("<root>a");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root");
  }

  @Test()
  public void testIncompleteOutput4() throws InvalidXMLException {
    enterXMLInput("<root>a<");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root");
  }

  @Test()
  public void testIncompleteOutput5() throws InvalidXMLException {
    enterXMLInput("<root>a<");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root");
  }

  @Test()
  public void testIncompleteOutput6() throws InvalidXMLException {
    enterXMLInput("<root>a<tag");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root");
  }

  @Test()
  public void testIncompleteOutput7() throws InvalidXMLException {
    enterXMLInput("<root>a<tag>");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2");
  }

  @Test()
  public void testIncompleteOutput8() throws InvalidXMLException {
    enterXMLInput("<root>a<tag><");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2");
  }

  @Test()
  public void testIncompleteOutput9() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2");
  }

  @Test()
  public void testIncompleteOutput10() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></tag");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2");
  }

  @Test()
  public void testIncompleteOutput11() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></tag>");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2"
            + "\nEnded:tag2");
  }

  @Test()
  public void testIncompleteOutput12() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></tag><");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2"
            + "\nEnded:tag2");
  }

  @Test()
  public void testIncompleteOutput13() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></tag></");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2"
            + "\nEnded:tag2");
  }

  @Test()
  public void testIncompleteOutput14() throws InvalidXMLException {
    enterXMLInput("<root>a<tag></tag></root");
    assertOutputValidatorAndLogger("Status:Incomplete",
        "Started:root"
            + "\nCharacters:a"
            + "\nStarted:tag2"
            + "\nEnded:tag2");
  }

}
