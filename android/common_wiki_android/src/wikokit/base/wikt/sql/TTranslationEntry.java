/* TTranslationEntry - SQL operations with the table 'translation_entry'
 * in SQLite Android Wiktionary parsed database.
 *
 * Copyright (c) 2009-2012 Andrew Krizhanovsky <andrew.krizhanovsky at gmail.com>
 * Distributed under EPL/LGPL/GPL/AL/BSD multi-license.
 */

package wikokit.base.wikt.sql;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/** SQL operations with the table 'translation_entry' in Wiktionary parsed database.
 *
 * @see wikt.word.WTranslationEntry
 */
public class TTranslationEntry {

    /** Unique identifier in the table 'translation_entry'. */
    private int id;

    /** Link to the table 'translation', which links to language, POS, and (may be) meaning.
     */
    private TTranslation translation;   // int translation_id;

    /** Translation into 'lang'.
     */
    private TLang lang;                 // int lang_id;
    
    /** Text of translation (Wikified text). */
    private TWikiText wiki_text;

    private final static TTranslationEntry[] NULL_TTRANSLATIONENTRY_ARRAY = new TTranslationEntry[0];

    public TTranslationEntry(int _id,TTranslation _translation,TLang _lang,TWikiText _wiki_text) {
        id          = _id;
        translation = _translation;
        lang        = _lang;
        wiki_text  = _wiki_text;
    }

    /** Gets unique ID from database */
    public int getID() {
        return id;
    }

    /** Gets wikified text from the database' table 'wiki_text'. */
    public TWikiText getWikiText() {
        return wiki_text;
    }

    /** Gets translation from the database' table 'translation'. */
    public TTranslation getTranslation() {
        return translation;
    }

    /** Gets language from database */
    public TLang getLang() {
        return lang;
    }

    /** Inserts record into the table 'meaning'.<br><br>
     * INSERT INTO translation_entry (translation_id,lang_id,wiki_text_id) VALUES (1,2,3);
     * @param lang_pos  ID of language and POS of wiki page which will be added
     * @param meaning_id defines meaning (sense) in table 'meaning', it could be null (todo check)
     * @param meaning_summary
     * @return inserted record, or null if insertion failed
     */
    /*public static TTranslationEntry insert (Connect connect,TTranslation trans,
            TLang lang,TWikiText wiki_text) {

        if(null == trans || null == lang || null == wiki_text) {
            System.err.println("Error (TTranslationEntry.insert()):: null arguments: trans = "+trans+
                    "; lang="+ lang +"; wiki_text=" + wiki_text);
            return null;
        }
        
        StringBuilder str_sql = new StringBuilder();
        TTranslationEntry trans_entry = null;
        try
        {
            Statement s = connect.conn.createStatement ();
            try {
                str_sql.append("INSERT INTO translation_entry (translation_id,lang_id,wiki_text_id) VALUES (");
                str_sql.append(trans.getID());
                str_sql.append(",");
                str_sql.append(lang.getID());
                str_sql.append(",");
                str_sql.append(wiki_text.getID());
                str_sql.append(")");
                if(s.executeUpdate (str_sql.toString()) > 0) {
                    ResultSet rs = connect.conn.prepareStatement( "SELECT LAST_INSERT_ID() AS id" ).executeQuery();
                    try {
                        if (rs.next ()) {
                            trans_entry = new TTranslationEntry(rs.getInt("id"), trans, lang, wiki_text);
                            //System.out.println("TTranslationEntry.insert()):: summary='" + trans.getMeaningSummary() +
                            //        "'; id=" + rs.getInt("id") +
                            //        "'; translation_id=" + trans.getID() +
                            //        "; wiki_text='" + wiki_text.getText() + "; lang='" + lang.getLanguage().getName() + "'");
                        }
                    } finally {
                        rs.close();
                    }
                }
            } finally {
                s.close();
            }
        }catch(SQLException ex) {
            System.err.println("SQLException (TTranslationEntry.insert()):: sql='" + str_sql.toString() + "' " + ex.getMessage());
        }
        return trans_entry;
    }*/

    /** Selects rows from the table 'translation_entry' by ID.<br><br>
     * SELECT translation_id,lang_id,wiki_text_id FROM translation_entry WHERE id=1;
     * @return empty array if data is absent
     */
    public static TTranslationEntry getByID (SQLiteDatabase db,int _id) {
        
        if(_id < 0) {
            System.err.println("Error (TTranslationEntry.getByID()):: ID is negative.");
            return null;
        }
        TTranslationEntry trans_entry = null;
        
        // SELECT translation_id,lang_id,wiki_text_id FROM translation_entry WHERE id=1;
        Cursor c = db.query("translation_entry", 
                new String[] { "translation_id", "lang_id", "wiki_text_id"}, 
                "id=" + _id, 
                null, null, null, null);
        
        if (c.moveToFirst()) {
            int i_translation_id = c.getColumnIndexOrThrow("translation_id");
            int i_lang_id = c.getColumnIndexOrThrow("lang_id");
            int i_wiki_text_id = c.getColumnIndexOrThrow("wiki_text_id");
            
            TTranslation trans = TTranslation.getByID(db, c.getInt(i_translation_id));
            TLang        _lang = TLang.getTLangFast(      c.getInt(i_lang_id));
            TWikiText    _wiki_text = TWikiText.getByID(db, c.getInt(i_wiki_text_id));

            if(null != trans && null != _lang && null != _wiki_text)
                trans_entry = new TTranslationEntry(_id, trans, _lang, _wiki_text);
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return trans_entry;
    }
    
    /** Selects rows from the table 'translation_entry' by translation ID and language ID.<br><br>
     * SELECT id,wiki_text_id FROM translation_entry WHERE translation_id=1 AND lang_id=2;
     * @return empty array if data is absent
     */
    public static TTranslationEntry[] getByLanguageAndTranslation (SQLiteDatabase db,
                                        TTranslation trans, TLang _lang) {

        if(null == trans || null == _lang) {
            System.err.println("Error (TTranslationEntry.getByLanguageAndTranslation()):: null arguments, trans="+trans+", lang="+_lang);
            return null;
        }
        List<TTranslationEntry> list_trans = null;
        
        // SELECT id,wiki_text_id FROM translation_entry WHERE translation_id=1 AND lang_id=2;
        Cursor c = db.query("translation_entry", 
                new String[] { "id", "wiki_text_id"}, 
                "translation_id=" + trans.getID() + " AND lang_id=" + _lang.getID(), 
                null, null, null, null);
        
        if (c.moveToFirst()) {
            do {
                int i_id = c.getColumnIndexOrThrow("id");
                int i_wiki_text_id = c.getColumnIndexOrThrow("wiki_text_id");
                
                int  _id            = c.getInt(i_id);
                int  wiki_text_id   = c.getInt(i_wiki_text_id);
                TWikiText _wiki_text = wiki_text_id < 1 ? null : TWikiText.getByID(db, wiki_text_id);
                
                if(null != _wiki_text) {
                    if(null == list_trans)
                               list_trans = new ArrayList<TTranslationEntry>();
                    list_trans.add(new TTranslationEntry(_id, trans, _lang, _wiki_text));
                }    
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if(null == list_trans)
            return NULL_TTRANSLATIONENTRY_ARRAY;
        return ((TTranslationEntry[])list_trans.toArray(NULL_TTRANSLATIONENTRY_ARRAY));
    }

    /** Selects rows from the table 'translation_entry' by wiki text ID and 
     * language ID. Selects one language entry from one translation box.<br><br>
     * 
     * SELECT id,translation_id FROM translation_entry WHERE wiki_text_id=3 AND lang_id=2;
     * @return empty array, if data is absent
     */
    public static TTranslationEntry[] getByWikiTextAndLanguage (SQLiteDatabase db,
                                        TWikiText _wiki_text, TLang _lang) {
                                        
        if(null == _wiki_text || null == _lang) {
            System.err.println("Error (TTranslationEntry.getByWikiTextAndLanguage()):: null arguments, wiki_text="+_wiki_text+", lang="+_lang);
            return null;
        }
        List<TTranslationEntry> list_entry = null;

     // SELECT id,translation_id FROM translation_entry WHERE wiki_text_id=3 AND lang_id=2;
        Cursor c = db.query("translation_entry", 
                new String[] { "id", "translation_id"}, 
                "wiki_text_id=" + _wiki_text.getID() + " AND lang_id=" + _lang.getID(),
                null, null, null, null);
        
        if (c.moveToFirst()) {
            do {
                int i_id = c.getColumnIndexOrThrow("id");
                int  _id = c.getInt(i_id);
                
                int i_translation_id = c.getColumnIndexOrThrow("translation_id");
                TTranslation trans = TTranslation.getByID(db, c.getInt(i_translation_id));
                if(null != trans) {
                    if(null == list_entry)
                        list_entry = new ArrayList<TTranslationEntry>();
                    list_entry.add(new TTranslationEntry(_id, trans, _lang, _wiki_text));
                }    
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if(null == list_entry)
            return NULL_TTRANSLATIONENTRY_ARRAY;
        return (TTranslationEntry [])list_entry.toArray(NULL_TTRANSLATIONENTRY_ARRAY);
    }

    /** Selects rows (all languages and translations entries from one translation box)
     * from the table 'translation_entry' by translation ID.<br><br>
     * SELECT id,lang_id,wiki_text_id FROM translation_entry WHERE translation_id=1;
     * @return empty array if data is absent
     */
    public static TTranslationEntry[] getByTranslation (SQLiteDatabase db,TTranslation trans) {

        if(null == trans) {
            System.err.println("Error (TTranslationEntry.getByTranslation()):: null arguments: translation.");
            return null;
        }
        List<TTranslationEntry> list_trans = null;
        
        // SELECT id,lang_id,wiki_text_id FROM translation_entry WHERE translation_id=1;
        Cursor c = db.query("translation_entry", 
                new String[] { "id", "lang_id", "wiki_text_id"}, 
                "translation_id=" + trans.getID(), 
                null, null, null, null);
        
        if (c.moveToFirst()) {
            do {
                int i_id = c.getColumnIndexOrThrow("id");
                int i_lang_id = c.getColumnIndexOrThrow("lang_id");
                int i_wiki_text_id = c.getColumnIndexOrThrow("wiki_text_id");
                
                int  _id          = c.getInt(i_id);
                TLang tlang = TLang.getTLangFast( c.getInt(i_lang_id) );
                int  wiki_text_id = c.getInt(i_wiki_text_id);
                TWikiText _wiki_text = wiki_text_id < 1 ? null : TWikiText.getByID(db, wiki_text_id);
                
                if(null != tlang && null != _wiki_text) {
                    if(null == list_trans)
                               list_trans = new ArrayList<TTranslationEntry>();
                    list_trans.add(new TTranslationEntry(_id, trans, tlang, _wiki_text));
                }
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if(null == list_trans)
            return NULL_TTRANSLATIONENTRY_ARRAY;
        return ((TTranslationEntry[])list_trans.toArray(NULL_TTRANSLATIONENTRY_ARRAY));
    }

    /** Deletes row from the table 'translation_entry' by a value of ID.<br>
     *  DELETE FROM translation_entry WHERE id=1;
     * @param  id  unique ID in the table `translation_entry`
     */
    /*public static void delete (Connect connect,TTranslationEntry trans_entry) {

        if(null == trans_entry) {
            System.err.println("Error (TTranslationEntry.delete()):: null argument 'translation_entry'");
            return;
        }
        StringBuilder str_sql = new StringBuilder();
        try {
            Statement s = connect.conn.createStatement ();
            try {
                str_sql.append("DELETE FROM translation_entry WHERE id=");
                str_sql.append(trans_entry.getID());
                s.execute (str_sql.toString());
                //System.out.println("TTranslationEntry.delete()):: summary='" + trans_entry.getTranslation().getMeaningSummary() +
                //        "'; id=" + trans_entry.getID() + "; wiki_text='" + trans_entry.getWikiText().getText() + "'");
            } finally {
                s.close();
            }
        } catch(SQLException ex) {
            System.err.println("SQLException (TTranslationEntry.delete()):: sql='" + str_sql.toString() + "' " + ex.getMessage());
        }
    }*/

}
