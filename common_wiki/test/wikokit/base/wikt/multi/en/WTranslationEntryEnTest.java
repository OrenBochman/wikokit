
package wikokit.base.wikt.multi.en;

import wikokit.base.wikt.multi.en.WTranslationEntryEn;
import wikokit.base.wikt.util.WikiText;
import wikokit.base.wikt.util.WikiWord;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import wikokit.base.wikt.word.WTranslationEntry;

public class WTranslationEntryEnTest {

    public WTranslationEntryEnTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // [[Datiwuy]] - wikified_language_name
    @Test
    public void testParse_wikified_language_name() {
        System.out.println("parameters_wikified_language_name");

        String page_title = "butterfly";
        String text = "*[[Datiwuy]]: {{t|duj|buurnba}}";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);

        assertEquals(1, result.getWikiPhrases().length);
        
        WikiText[] phrases = result.getWikiPhrases();
        
        assertTrue(phrases[0].getVisibleText().equalsIgnoreCase( "buurnba" ) );
        WikiWord[] ww0 = phrases[0].getWikiWords();
        assertEquals(1, ww0.length);
        assertTrue(ww0[0].getWordVisible().equalsIgnoreCase( "buurnba" ) );
        assertTrue(ww0[0].getWordLink().   equalsIgnoreCase( "buurnba" ) );
    }

    // Latin - non_wikified_language_name
    @Test
    public void testParse_non_wikified_language_name() {
        System.out.println("parameters_non_wikified_language_name");

        String page_title = "butterfly";
        String text = "*Latin: {{t-|la|papilio|alt=pāpíliō}}";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);

        assertEquals(1, result.getWikiPhrases().length);

        WikiText[] phrases = result.getWikiPhrases();

        assertTrue(phrases[0].getVisibleText().equalsIgnoreCase( "papilio" ) );
        WikiWord[] ww0 = phrases[0].getWikiWords();
        assertEquals(1, ww0.length);
        assertTrue(ww0[0].getWordVisible().equalsIgnoreCase( "papilio" ) );
        assertTrue(ww0[0].getWordLink().   equalsIgnoreCase( "papilio" ) );
    }

    // Empty translation
    @Test
    public void testParse_Empty_translation() {
        System.out.println("parameters_Empty_translation");

        String page_title = "butterfly";

        // 1. ": "
        String text = "*Latin: ";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);
        assertNull(result);

        // 2. ":"
        text = "*Latin:";
        result = WTranslationEntryEn.parse(page_title, text);
        assertNull(result);
    }

    // Error test: one line with several languages, e.g. la and de
    @Test
    public void testParse_one_line_with_several_languages() {
        System.out.println("parameters_one_line_with_several_languages");

        String page_title = "butterfly";
        String text = "*Latin: {{t-|la|papilio|alt=pāpíliō}}, {{t|de|test}}";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);
        assertNull(result);
    }

    // Warning test: two unknown language codes, but only one Warning message
    // should be printed!
    @Test
    public void testParse_two_unknown_language_codes() {
        System.out.println("parameters_two_unknown_language_codes: Attention! Should be only one warning about unknown language code!");

        String page_title = "butterfly";
        String text = "*Latin: {{t-|unkn1|papilio|alt=pāpíliō}}";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);
        assertNull(result);

        text = "*Latin: {{t-|unkn1|papilio|alt=pāpíliō}}";
        result = WTranslationEntryEn.parse(page_title, text);
        assertNull(result);

        // without any warning, since there is no any translations in second parameter
        text = "*Latin: {{t-|unkn1||alt=pāpíliō}}";
        result = WTranslationEntryEn.parse(page_title, text);
        assertEquals(null, result);
    }
    
    // t-SOP, @see http://en.wiktionary.org/wiki/Template:t-SOP
    // livelihood
    // * Russian: {{t-SOP|ru|[[средство|средства]] [[к]] [[существование|существованию]]|tr=srédstva k suščestvovániju|n-p}}
    @Test
    public void testParse_t_SOP() {
        System.out.println("parameters_t_SOP");

        String page_title = "livelihood";
        String text = "* Russian: {{t-SOP|ru|[[средство|средства]] [[к]] [[существование|существованию]]|tr=srédstva k suščestvovániju|n-p}}";
        WTranslationEntry result = WTranslationEntryEn.parse(page_title, text);

        assertNotNull(result);
        if(null == result) {
            assertEquals(1, result.getWikiPhrases().length);

            WikiText[] phrases = result.getWikiPhrases();

            assertTrue(phrases[0].getVisibleText().equalsIgnoreCase( "‎средства к существованию" ) );
            assertTrue(phrases[0].getWikifiedText().equalsIgnoreCase( "[[средство|средства]] [[к]] [[существование|существованию]]" ) );
            WikiWord[] ww0 = phrases[0].getWikiWords();
            assertEquals(3, ww0.length);
            assertTrue(ww0[2].getWordVisible().equalsIgnoreCase( "существованию" ) );
            assertTrue(ww0[2].getWordLink().   equalsIgnoreCase( "существование" ) );
        }
    }

}