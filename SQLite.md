# Introduction #

Dictionary with GUI, i.e. **wiwordik**, can work with MySQL and SQLite.
But...
The user will be happy do not install MySQL,
so we need to dump Wiktionary parsed database (MySQL) and to convert into SQLite format.

The wiwordik application is distributed with SQLite file.

# Conversion #
## Tables' structure preparations ##

Prepare only once the SQLite file with table structures of the the Wiktionary parsed database (automatic mode):
  * `cd ./wiwordik/mysql2sqlite`
  * `mysqldump -u root -p --no-data --compatible=ansi --default-character-set=binary ruwikt20100405_parsed > wikt_parsed_structure_source.sql`
  * `mysql2sqlite.bat`

The result file with SQLite tables' structure is `wikt_parsed_structure.sql`.

Or in manual mode (Vim regular expressions):
```
%s/COMMENT.*/,/g    // remove COMMENT
%s/unsigned\s*//g   // remove "unsigned"
%s/\n\?.*\PRIMARY KEY.*//g      // remove lines with text "PRIMARY KEY"
%s/AUTO_INCREMENT/PRIMARY KEY/g // replace AUTO_INCREMENT by PRIMARY KEY
%s/CHARACTER SET latin1 COLLATE latin1_bin\s*//g    // remove "CHAR... "
```

Update this file if the stucture of the Wiktionary parsed database is changed, if new language codes were added.

## Data conversion ##
Dump data of the the Wiktionary parsed database:
  * `mysqldump -u root -p --quick --no-create-info --skip-extended-insert --compatible=ansi --default-character-set=binary ruwikt20100405_parsed > ruwikt20100405_parsed_data.sql`

Vim regular expressions in order to change data of INSERT commands into SQLite format:
```
                                // remove two types of lines:
%s/UNLOCK TABLES;\n//g          // UNLOCK TABLES;
%s/LOCK TABLES \".\+\n//g       // LOCK TABLES "page" WRITE;
%s/\\'/''/g                     // replace \' by ''
%s/\([,(]\)'\\''\([,)]\)/\1"'"\2/g // replace '\'' by "'"
```

## Data and tables merging ##

Merge two files (with tables structure and data) into one:
  * `ruwikt20100405_parsed_data.sql += wikt_parsed_structure.sql`

But... split `wikt_parsed_structure.sql` so, that:
  * `CREATE TABLE...` at the top of the file
  * `CREATE INDEX...` at the end of the file

## SQLite shell ##

Read the note "[Let's speed up SQLite](http://componavt.livejournal.com/3393.html)".

Run [SQLite](http://sqlite.org), load and backup the SQLite database:
```
sqlite3
.read "C:/w/bin/ruwikt20100405_parsed_data.sql"
PRAGMA integrity_check;
VACUUM;
.backup "C:/w/bin/ruwikt20100405.sqlite"
```

## SQLite file location ##

Copy SQLite file `ruwikt20100405.sqlite` to the folder `./wikokit/wiwordik/sqlite/`.

## Java ##

Check that the file `wiwordik\src\wiwordik\Main.fx` contains the line:
```
wikt_parsed_conn.OpenSQLite(Connect.RUWIKT_HOST, Connect.RUWIKT_PARSED_DB, Connect.RUWIKT_USER, Connect.RUWIKT_PASS, LanguageType.ru);
```

Check that the file `wiwordik\src\wiwordik\WConstants.fx` contains the line:
```
/** true (SQLite), false (MySQL) */
public def IS_SQLITE : Boolean = true;
```

Check that the file `common_wiki\src\wikipedia\sql\Connect.java` contains the line:
```
public final static String RUWIKT_SQLITE = "ruwikt20110521.sqlite";
```

### Java libraries ###

Check that you have the latest versions of the libs:
  * [SQLiteJDBC](http://www.xerial.org/trac/Xerial/wiki/SQLiteJDBC)
  * [mysql-connector-java](http://dev.mysql.com/downloads/connector/j/5.1.html#downloads) (Connector/J 5.1.13 or later)

# Links #
  * [Java MySQL to SQLite migration](http://componavt.livejournal.com/630.html)

# Previous step #
  * [Setup NetBeans environment for parsing](SetupNetBeansForParsing.md)

# Next step #
  * [JNLPandJavaWebStart](JNLPandJavaWebStart.md) How to create wiwordik-XX.jar and wiwordik-XX.jnlp files.