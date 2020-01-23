/*
 * PLSQLTokenMarker.java - SQL token marker
 * Copyright (C) 2001 Romain Guy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.gjt.sp.jedit.syntax;

import javax.swing.text.Segment;

/**
 * SQL token marker.
 */
public class PLSQLTokenMarker extends TSQLTokenMarker
{
  public PLSQLTokenMarker()
  {
    super(getKeywords());
  }

  public static KeywordMap getKeywords()
  {
    if (sqlKeywords == null)
    {
      sqlKeywords = new KeywordMap(true);
      sqlKeywords.add("ABORT", Token.KEYWORD1);
      sqlKeywords.add("ACCESS", Token.KEYWORD1);
      sqlKeywords.add("ADD", Token.KEYWORD1);
      sqlKeywords.add("ALTER", Token.KEYWORD1);
      sqlKeywords.add("ARRAY", Token.KEYWORD1);
      sqlKeywords.add("ARRAY_LEN", Token.KEYWORD1);
      sqlKeywords.add("AS", Token.KEYWORD1);
      sqlKeywords.add("ASC", Token.KEYWORD1);
      sqlKeywords.add("ASSERT", Token.KEYWORD1);
      sqlKeywords.add("ASSIGN", Token.KEYWORD1);
      sqlKeywords.add("AT", Token.KEYWORD1);
      sqlKeywords.add("AUDIT", Token.KEYWORD1);
      sqlKeywords.add("AUTHORIZATION", Token.KEYWORD1);
      sqlKeywords.add("AVG", Token.KEYWORD1);
      sqlKeywords.add("BASE_TABLE", Token.KEYWORD1);
      sqlKeywords.add("BEGIN", Token.KEYWORD1);
      sqlKeywords.add("BINARY_INTEGER", Token.KEYWORD1);
      sqlKeywords.add("BODY", Token.KEYWORD1);
      sqlKeywords.add("CASE", Token.KEYWORD1);
      sqlKeywords.add("CHAR", Token.KEYWORD1);
      sqlKeywords.add("CHAR_BASE", Token.KEYWORD1);
      sqlKeywords.add("CHECK", Token.KEYWORD1);
      sqlKeywords.add("CLOSE", Token.KEYWORD1);
      sqlKeywords.add("CLUSTER", Token.KEYWORD1);
      sqlKeywords.add("CLUSTERS", Token.KEYWORD1);
      sqlKeywords.add("COLAUTH", Token.KEYWORD1);
      sqlKeywords.add("COLUMN", Token.KEYWORD1);
      sqlKeywords.add("COMMENT", Token.KEYWORD1);
      sqlKeywords.add("COMMIT", Token.KEYWORD1);
      sqlKeywords.add("COMPRESS", Token.KEYWORD1);
      sqlKeywords.add("CONSTANT", Token.KEYWORD1);
      sqlKeywords.add("CONSTRAINT", Token.KEYWORD1);
      sqlKeywords.add("COUNT", Token.KEYWORD1);
      sqlKeywords.add("CREATE", Token.KEYWORD1);
      sqlKeywords.add("CURRENT", Token.KEYWORD1);
      sqlKeywords.add("CURRVAL", Token.KEYWORD1);
      sqlKeywords.add("CURSOR", Token.KEYWORD1);
      sqlKeywords.add("DATABASE", Token.KEYWORD1);
      sqlKeywords.add("DATA_BASE", Token.KEYWORD1);
      sqlKeywords.add("DATE", Token.KEYWORD1);
      sqlKeywords.add("DBA", Token.KEYWORD1);
      sqlKeywords.add("DEBUGOFF", Token.KEYWORD1);
      sqlKeywords.add("DEBUGON", Token.KEYWORD1);
      sqlKeywords.add("DECLARE", Token.KEYWORD1);
      sqlKeywords.add("DEFAULT", Token.KEYWORD1);
      sqlKeywords.add("DEFINITION", Token.KEYWORD1);
      sqlKeywords.add("DELAY", Token.KEYWORD1);
      sqlKeywords.add("DELETE", Token.KEYWORD1);
      sqlKeywords.add("DESC", Token.KEYWORD1);
      sqlKeywords.add("DIGITS", Token.KEYWORD1);
      sqlKeywords.add("DISPOSE", Token.KEYWORD1);
      sqlKeywords.add("DISTINCT", Token.KEYWORD1);
      sqlKeywords.add("DO", Token.KEYWORD1);
      sqlKeywords.add("DROP", Token.KEYWORD1);
      sqlKeywords.add("DUMP", Token.KEYWORD1);
      sqlKeywords.add("ELSE", Token.KEYWORD1);
      sqlKeywords.add("ELSIF", Token.KEYWORD1);
      sqlKeywords.add("END", Token.KEYWORD1);
      sqlKeywords.add("ENTRY", Token.KEYWORD1);
      sqlKeywords.add("EXCEPTION", Token.KEYWORD1);
      sqlKeywords.add("EXCEPTION_INIT", Token.KEYWORD1);
      sqlKeywords.add("EXCLUSIVE", Token.KEYWORD1);
      sqlKeywords.add("EXIT", Token.KEYWORD1);
      sqlKeywords.add("FALSE", Token.KEYWORD1);
      sqlKeywords.add("FETCH", Token.KEYWORD1);
      sqlKeywords.add("FILE", Token.KEYWORD1);
      sqlKeywords.add("FOR", Token.KEYWORD1);
      sqlKeywords.add("FORM", Token.KEYWORD1);
      sqlKeywords.add("FROM", Token.KEYWORD1);
      sqlKeywords.add("FUNCTION", Token.KEYWORD1);
      sqlKeywords.add("GENERIC", Token.KEYWORD1);
      sqlKeywords.add("GOTO", Token.KEYWORD1);
      sqlKeywords.add("GRANT", Token.KEYWORD1);
      sqlKeywords.add("GREATEST", Token.KEYWORD1);
      sqlKeywords.add("GROUP", Token.KEYWORD1);
      sqlKeywords.add("HAVING", Token.KEYWORD1);
      sqlKeywords.add("IDENTIFIED", Token.KEYWORD1);
      sqlKeywords.add("IDENTITYCOL", Token.KEYWORD1);
      sqlKeywords.add("IF", Token.KEYWORD1);
      sqlKeywords.add("IMMEDIATE", Token.KEYWORD1);
      sqlKeywords.add("INCREMENT", Token.KEYWORD1);
      sqlKeywords.add("INDEX", Token.KEYWORD1);
      sqlKeywords.add("INDEXES", Token.KEYWORD1);
      sqlKeywords.add("INDICATOR", Token.KEYWORD1);
      sqlKeywords.add("INITIAL", Token.KEYWORD1);
      sqlKeywords.add("INSERT", Token.KEYWORD1);
      sqlKeywords.add("INTERFACE", Token.KEYWORD1);
      sqlKeywords.add("INTO", Token.KEYWORD1);
      sqlKeywords.add("IS", Token.KEYWORD1);
      sqlKeywords.add("KEY", Token.KEYWORD1);
      sqlKeywords.add("LEAST", Token.KEYWORD1);
      sqlKeywords.add("LEVEL", Token.KEYWORD1);
      sqlKeywords.add("LIMITED", Token.KEYWORD1);
      sqlKeywords.add("LOCK", Token.KEYWORD1);
      sqlKeywords.add("LONG", Token.KEYWORD1);
      sqlKeywords.add("LOOP", Token.KEYWORD1);
      sqlKeywords.add("MAX", Token.KEYWORD1);
      sqlKeywords.add("MAXEXTENTS", Token.KEYWORD1);
      sqlKeywords.add("MIN", Token.KEYWORD1);
      sqlKeywords.add("MINUS", Token.KEYWORD1);
      sqlKeywords.add("MLSLABEL", Token.KEYWORD1);
      sqlKeywords.add("MOD", Token.KEYWORD1);
      sqlKeywords.add("MORE", Token.KEYWORD1);
      sqlKeywords.add("NATURAL", Token.KEYWORD1);
      sqlKeywords.add("NATURALN", Token.KEYWORD1);
      sqlKeywords.add("NEW", Token.KEYWORD1);
      sqlKeywords.add("NEXTVAL", Token.KEYWORD1);
      sqlKeywords.add("NOAUDIT", Token.KEYWORD1);
      sqlKeywords.add("NOCOMPRESS", Token.KEYWORD1);
      sqlKeywords.add("NOWAIT", Token.KEYWORD1);
      sqlKeywords.add("NULL", Token.KEYWORD1);
      sqlKeywords.add("NUMBER", Token.KEYWORD1);
      sqlKeywords.add("NUMBER_BASE", Token.KEYWORD1);
      sqlKeywords.add("OF", Token.KEYWORD1);
      sqlKeywords.add("OFFLINE", Token.KEYWORD1);
      sqlKeywords.add("ON", Token.KEYWORD1);
      sqlKeywords.add("OFF", Token.KEYWORD1);
      sqlKeywords.add("ONLINE", Token.KEYWORD1);
      sqlKeywords.add("OPEN", Token.KEYWORD1);
      sqlKeywords.add("OPTION", Token.KEYWORD1);
      sqlKeywords.add("ORDER", Token.KEYWORD1);
      sqlKeywords.add("OTHERS", Token.KEYWORD1);
      sqlKeywords.add("OUT", Token.KEYWORD1);
      sqlKeywords.add("PACKAGE", Token.KEYWORD1);
      sqlKeywords.add("PARTITION", Token.KEYWORD1);
      sqlKeywords.add("PCTFREE", Token.KEYWORD1);
      sqlKeywords.add("POSITIVE", Token.KEYWORD1);
      sqlKeywords.add("POSITIVEN", Token.KEYWORD1);
      sqlKeywords.add("PRAGMA", Token.KEYWORD1);
      sqlKeywords.add("PRIVATE", Token.KEYWORD1);
      sqlKeywords.add("PRIMARY", Token.KEYWORD1);
      sqlKeywords.add("PRIVILEGES", Token.KEYWORD1);
      sqlKeywords.add("PROCEDURE", Token.KEYWORD1);
      sqlKeywords.add("PUBLIC", Token.KEYWORD1);
      sqlKeywords.add("QUOTED_IDENTIFIER", Token.KEYWORD1);
      sqlKeywords.add("RAISE", Token.KEYWORD1);
      sqlKeywords.add("RANGE", Token.KEYWORD1);
      sqlKeywords.add("RAW", Token.KEYWORD1);
      sqlKeywords.add("RECORD", Token.KEYWORD1);
      sqlKeywords.add("REF", Token.KEYWORD1);
      sqlKeywords.add("REFERENCES", Token.KEYWORD1);
      sqlKeywords.add("RELEASE", Token.KEYWORD1);
      sqlKeywords.add("REMR", Token.KEYWORD1);
      sqlKeywords.add("RENAME", Token.KEYWORD1);
      sqlKeywords.add("RESOURCE", Token.KEYWORD1);
      sqlKeywords.add("RETURN", Token.KEYWORD1);
      sqlKeywords.add("REVERSE", Token.KEYWORD1);
      sqlKeywords.add("REVOKE", Token.KEYWORD1);
      sqlKeywords.add("ROLLBACK", Token.KEYWORD1);
      sqlKeywords.add("ROW", Token.KEYWORD1);
      sqlKeywords.add("ROWID", Token.KEYWORD1);
      sqlKeywords.add("ROWLABEL", Token.KEYWORD1);
      sqlKeywords.add("ROWNUM", Token.KEYWORD1);
      sqlKeywords.add("ROWS", Token.KEYWORD1);
      sqlKeywords.add("ROWTYPE", Token.KEYWORD1);
      sqlKeywords.add("RUN", Token.KEYWORD1);
      sqlKeywords.add("SAVEPOINT", Token.KEYWORD1);
      sqlKeywords.add("SCHEMA", Token.KEYWORD1);
      sqlKeywords.add("SELECT", Token.KEYWORD1);
      sqlKeywords.add("SEPERATE", Token.KEYWORD1);
      sqlKeywords.add("SESSION", Token.KEYWORD1);
      sqlKeywords.add("SET", Token.KEYWORD1);
      sqlKeywords.add("SHARE", Token.KEYWORD1);
      sqlKeywords.add("SIGNTYPE", Token.KEYWORD1);
      sqlKeywords.add("SPACE", Token.KEYWORD1);
      sqlKeywords.add("SQL", Token.KEYWORD1);
      sqlKeywords.add("SQLCODE", Token.KEYWORD1);
      sqlKeywords.add("SQLERRM", Token.KEYWORD1);
      sqlKeywords.add("STATEMENT", Token.KEYWORD1);
      sqlKeywords.add("STDDEV", Token.KEYWORD1);
      sqlKeywords.add("SUBTYPE", Token.KEYWORD1);
      sqlKeywords.add("SUCCESSFULL", Token.KEYWORD1);
      sqlKeywords.add("SUM", Token.KEYWORD1);
      sqlKeywords.add("SYNONYM", Token.KEYWORD1);
      sqlKeywords.add("SYSDATE", Token.KEYWORD1);
      sqlKeywords.add("TABAUTH", Token.KEYWORD1);
      sqlKeywords.add("TABLE", Token.KEYWORD1);
      sqlKeywords.add("TABLES", Token.KEYWORD1);
      sqlKeywords.add("TASK", Token.KEYWORD1);
      sqlKeywords.add("TERMINATE", Token.KEYWORD1);
      sqlKeywords.add("THEN", Token.KEYWORD1);
      sqlKeywords.add("TO", Token.KEYWORD1);
      sqlKeywords.add("TRIGGER", Token.KEYWORD1);
      sqlKeywords.add("TRUE", Token.KEYWORD1);
      sqlKeywords.add("TYPE", Token.KEYWORD1);
      sqlKeywords.add("UID", Token.KEYWORD1);
      sqlKeywords.add("UNION", Token.KEYWORD1);
      sqlKeywords.add("UNIQUE", Token.KEYWORD1);
      sqlKeywords.add("UPDATE", Token.KEYWORD1);
      sqlKeywords.add("UPDATETEXT", Token.KEYWORD1);
      sqlKeywords.add("USE", Token.KEYWORD1);
      sqlKeywords.add("USER", Token.KEYWORD1);
      sqlKeywords.add("USING", Token.KEYWORD1);
      sqlKeywords.add("VALIDATE", Token.KEYWORD1);
      sqlKeywords.add("VALUES", Token.KEYWORD1);
      sqlKeywords.add("VARIANCE", Token.KEYWORD1);
      sqlKeywords.add("VIEW", Token.KEYWORD1);
      sqlKeywords.add("VIEWS", Token.KEYWORD1);
      sqlKeywords.add("WHEN", Token.KEYWORD1);
      sqlKeywords.add("WHENEVER", Token.KEYWORD1);
      sqlKeywords.add("WHERE", Token.KEYWORD1);
      sqlKeywords.add("WHILE", Token.KEYWORD1);
      sqlKeywords.add("WITH", Token.KEYWORD1);
      sqlKeywords.add("WORK", Token.KEYWORD1);
      sqlKeywords.add("WRITE", Token.KEYWORD1);
      sqlKeywords.add("XOR", Token.KEYWORD1);

      sqlKeywords.add("binary", Token.KEYWORD1);
      sqlKeywords.add("bit", Token.KEYWORD1);
      sqlKeywords.add("blob", Token.KEYWORD1);
      sqlKeywords.add("boolean", Token.KEYWORD1);
      sqlKeywords.add("char", Token.KEYWORD1);
      sqlKeywords.add("character", Token.KEYWORD1);
      sqlKeywords.add("datetime", Token.KEYWORD1);
      sqlKeywords.add("decimal", Token.KEYWORD1);
      sqlKeywords.add("float", Token.KEYWORD1);
      sqlKeywords.add("image", Token.KEYWORD1);
      sqlKeywords.add("int", Token.KEYWORD1);
      sqlKeywords.add("integer", Token.KEYWORD1);
      sqlKeywords.add("money", Token.KEYWORD1);
      sqlKeywords.add("name", Token.KEYWORD1);
      sqlKeywords.add("numeric", Token.KEYWORD1);
      sqlKeywords.add("nchar", Token.KEYWORD1);
      sqlKeywords.add("nvarchar", Token.KEYWORD1);
      sqlKeywords.add("ntext", Token.KEYWORD1);
      sqlKeywords.add("pls_integer", Token.KEYWORD1);
      sqlKeywords.add("real", Token.KEYWORD1);
      sqlKeywords.add("smalldatetime", Token.KEYWORD1);
      sqlKeywords.add("smallint", Token.KEYWORD1);
      sqlKeywords.add("smallmoney", Token.KEYWORD1);
      sqlKeywords.add("text", Token.KEYWORD1);
      sqlKeywords.add("timestamp", Token.KEYWORD1);
      sqlKeywords.add("tinyint", Token.KEYWORD1);
      sqlKeywords.add("uniqueidentifier", Token.KEYWORD1);
      sqlKeywords.add("varbinary", Token.KEYWORD1);
      sqlKeywords.add("varchar", Token.KEYWORD1);
      sqlKeywords.add("varchar2", Token.KEYWORD1);

      sqlKeywords.add("ABS", Token.KEYWORD2);
      sqlKeywords.add("ACOS", Token.KEYWORD2);
      sqlKeywords.add("ADD_MONTHS", Token.KEYWORD2);
      sqlKeywords.add("ASCII", Token.KEYWORD2);
      sqlKeywords.add("ASIN", Token.KEYWORD2);
      sqlKeywords.add("ATAN", Token.KEYWORD2);
      sqlKeywords.add("ATAN2", Token.KEYWORD2);
      sqlKeywords.add("CEIL", Token.KEYWORD2);
      sqlKeywords.add("CHARTOROWID", Token.KEYWORD2);
      sqlKeywords.add("CHR", Token.KEYWORD2);
      sqlKeywords.add("CONCAT", Token.KEYWORD2);
      sqlKeywords.add("CONVERT", Token.KEYWORD2);
      sqlKeywords.add("COS", Token.KEYWORD2);
      sqlKeywords.add("COSH", Token.KEYWORD2);
      sqlKeywords.add("DECODE", Token.KEYWORD2);
      sqlKeywords.add("DEFINE", Token.KEYWORD2);
      sqlKeywords.add("FLOOR", Token.KEYWORD2);
      sqlKeywords.add("HEXTORAW", Token.KEYWORD2);
      sqlKeywords.add("INITCAP", Token.KEYWORD2);
      sqlKeywords.add("INSTR", Token.KEYWORD2);
      sqlKeywords.add("INSTRB", Token.KEYWORD2);
      sqlKeywords.add("LAST_DAY", Token.KEYWORD2);
      sqlKeywords.add("LENGTH", Token.KEYWORD2);
      sqlKeywords.add("LENGTHB", Token.KEYWORD2);
      sqlKeywords.add("LN", Token.KEYWORD2);
      sqlKeywords.add("LOG", Token.KEYWORD2);
      sqlKeywords.add("LOWER", Token.KEYWORD2);
      sqlKeywords.add("LPAD", Token.KEYWORD2);
      sqlKeywords.add("LTRIM", Token.KEYWORD2);
      sqlKeywords.add("MOD", Token.KEYWORD2);
      sqlKeywords.add("MONTHS_BETWEEN", Token.KEYWORD2);
      sqlKeywords.add("NEW_TIME", Token.KEYWORD2);
      sqlKeywords.add("NEXT_DAY", Token.KEYWORD2);
      sqlKeywords.add("NLSSORT", Token.KEYWORD2);
      sqlKeywords.add("NSL_INITCAP", Token.KEYWORD2);
      sqlKeywords.add("NLS_LOWER", Token.KEYWORD2);
      sqlKeywords.add("NLS_UPPER", Token.KEYWORD2);
      sqlKeywords.add("NVL", Token.KEYWORD2);
      sqlKeywords.add("POWER", Token.KEYWORD2);
      sqlKeywords.add("RAWTOHEX", Token.KEYWORD2);
      sqlKeywords.add("REPLACE", Token.KEYWORD2);
      sqlKeywords.add("ROUND", Token.KEYWORD2);
      sqlKeywords.add("ROWIDTOCHAR", Token.KEYWORD2);
      sqlKeywords.add("RPAD", Token.KEYWORD2);
      sqlKeywords.add("RTRIM", Token.KEYWORD2);
      sqlKeywords.add("SIGN", Token.KEYWORD2);
      sqlKeywords.add("SOUNDEX", Token.KEYWORD2);
      sqlKeywords.add("SIN", Token.KEYWORD2);
      sqlKeywords.add("SINH", Token.KEYWORD2);
      sqlKeywords.add("SQRT", Token.KEYWORD2);
      sqlKeywords.add("SUBSTR", Token.KEYWORD2);
      sqlKeywords.add("SUBSTRB", Token.KEYWORD2);
      sqlKeywords.add("TAN", Token.KEYWORD2);
      sqlKeywords.add("TANH", Token.KEYWORD2);
      sqlKeywords.add("TO_CHAR", Token.KEYWORD2);
      sqlKeywords.add("TO_DATE", Token.KEYWORD2);
      sqlKeywords.add("TO_MULTIBYTE", Token.KEYWORD2);
      sqlKeywords.add("TO_NUMBER", Token.KEYWORD2);
      sqlKeywords.add("TO_SINGLE_BYTE", Token.KEYWORD2);
      sqlKeywords.add("TRANSLATE", Token.KEYWORD2);
      sqlKeywords.add("TRUNC", Token.KEYWORD2);
      sqlKeywords.add("UPPER", Token.KEYWORD2);

      sqlKeywords.add("ALL", Token.KEYWORD1);
      sqlKeywords.add("AND", Token.KEYWORD1);
      sqlKeywords.add("ANY", Token.KEYWORD1);
      sqlKeywords.add("BETWEEN", Token.KEYWORD1);
      sqlKeywords.add("BY", Token.KEYWORD1);
      sqlKeywords.add("CONNECT", Token.KEYWORD1);
      sqlKeywords.add("EXISTS", Token.KEYWORD1);
      sqlKeywords.add("IN", Token.KEYWORD1);
      sqlKeywords.add("INTERSECT", Token.KEYWORD1);
      sqlKeywords.add("LIKE", Token.KEYWORD1);
      sqlKeywords.add("NOT", Token.KEYWORD1);
      sqlKeywords.add("NULL", Token.KEYWORD1);
      sqlKeywords.add("OR", Token.KEYWORD1);
      sqlKeywords.add("START", Token.KEYWORD1);
      sqlKeywords.add("UNION", Token.KEYWORD1);
      sqlKeywords.add("WITH", Token.KEYWORD1);
			
      sqlKeywords.add("VERIFY", Token.KEYWORD3);
      sqlKeywords.add("SERVEROUTPUT", Token.KEYWORD3);
      sqlKeywords.add("PAGESIZE", Token.KEYWORD3);
      sqlKeywords.add("LINESIZE", Token.KEYWORD3);
      sqlKeywords.add("ARRAYSIZE", Token.KEYWORD3);
      sqlKeywords.add("DBMS_OUTPUT", Token.KEYWORD3);
      sqlKeywords.add("PUT_LINE", Token.KEYWORD3);
      sqlKeywords.add("ENABLE", Token.KEYWORD3);
    }
    return sqlKeywords;
  }
}
