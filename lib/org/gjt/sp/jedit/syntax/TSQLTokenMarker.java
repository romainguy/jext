/*
 * TSQLTokenMarker.java - SQL token marker
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
public class TSQLTokenMarker extends TokenMarker
{
  boolean bracket = false;

  public TSQLTokenMarker()
  {
    this(getKeywords());
  }

  public TSQLTokenMarker(KeywordMap keywords)
  {
    this.keywords = keywords;
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    int length = line.count + offset;

loop:   for(int i = offset; i < length; i++)
    {
      int i1 = (i+1);

      char c = array[i];

      switch(token)
      {
      case Token.NULL:
        switch(c)
        {
        case '[':
          bracket = true;
        case '"':
          doKeyword(line,i,c);
          addToken(i - lastOffset,token);
          token = Token.LITERAL1;
          lastOffset = lastKeyword = i;
          break;
        case '\'':
          doKeyword(line,i,c);
          addToken(i - lastOffset,token);
          token = Token.LITERAL2;
          lastOffset = lastKeyword = i;
          break;
        case ':':
          if(lastKeyword == offset)
          {
            if(doKeyword(line,i,c))
              break;
            addToken(i1 - lastOffset,Token.LABEL);
            lastOffset = lastKeyword = i1;
          }
          else if(doKeyword(line,i,c))
            break;
          break;
        case '/':
          doKeyword(line,i,c);
          if(length - i > 1)
          {
            if (array[i1] == '*')
            {
              addToken(i - lastOffset,token);
              lastOffset = lastKeyword = i;
              token = Token.COMMENT2;
            }
          }
          break;
        case '-':
          doKeyword(line, i, c);
          if(length - i > 1)
          {
            switch(array[i1])
            {
              case '-':
                addToken(i - lastOffset, token);
                addToken(length - i, Token.COMMENT1);
                lastOffset = lastKeyword = length;
                break loop;
            }
          }
          break; 
        default:
          if(!Character.isLetterOrDigit(c) && c != '_')
            doKeyword(line,i,c);
          break;
        }
        break;
      case Token.COMMENT2:
        if(c == '*' && length - i > 1)
        {
          if(array[i1] == '/')
          {
            i++;
            addToken((i+1) - lastOffset,token);
            token = Token.NULL;
            lastOffset = lastKeyword = i+1;
          }
        }
        break;
      case Token.LITERAL1:
         if(c == '"' || c == ']')
        {
          addToken(i1 - lastOffset,token);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
          bracket = false;
        }
        break;
      case Token.LITERAL2:
        if(c == '\'')
        {
          addToken(i1 - lastOffset,token);
          token = Token.NULL;
          lastOffset = lastKeyword = i1;
        }
        break;
      default:
        throw new InternalError("Invalid state: "
          + token);
      }
    }

    if(token == Token.NULL)
      doKeyword(line,length,'\0');

    switch(token)
    {
    case Token.LITERAL1:
    case Token.LITERAL2:
      addToken(length - lastOffset, (bracket ? Token.LITERAL1 : Token.INVALID));
      token = (bracket ? Token.LITERAL1 : Token.NULL);
      break;
    case Token.KEYWORD2:
      addToken(length - lastOffset,token);
      token = Token.NULL;
      break;
    default:
      addToken(length - lastOffset,token);
      break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if(sqlKeywords == null)
    {
      sqlKeywords = new KeywordMap(true);
      sqlKeywords.add("ADD", Token.KEYWORD1);
      sqlKeywords.add("ALTER", Token.KEYWORD1);
      sqlKeywords.add("ANSI_NULLS", Token.KEYWORD1);
      sqlKeywords.add("AS", Token.KEYWORD1);
      sqlKeywords.add("ASC", Token.KEYWORD1);
      sqlKeywords.add("AUTHORIZATION", Token.KEYWORD1);
      sqlKeywords.add("BACKUP", Token.KEYWORD1);
      sqlKeywords.add("BEGIN", Token.KEYWORD1);
      sqlKeywords.add("BREAK", Token.KEYWORD1);
      sqlKeywords.add("BROWSE", Token.KEYWORD1);
      sqlKeywords.add("BULK", Token.KEYWORD1);
      sqlKeywords.add("BY", Token.KEYWORD1);
      sqlKeywords.add("CASCADE", Token.KEYWORD1);
      sqlKeywords.add("CHECK", Token.KEYWORD1);
      sqlKeywords.add("CHECKPOINT", Token.KEYWORD1);
      sqlKeywords.add("CLOSE", Token.KEYWORD1);
      sqlKeywords.add("CLUSTERED", Token.KEYWORD1);
      sqlKeywords.add("COLUMN", Token.KEYWORD1);
      sqlKeywords.add("COMMIT", Token.KEYWORD1);
      sqlKeywords.add("COMMITTED", Token.KEYWORD1);
      sqlKeywords.add("COMPUTE", Token.KEYWORD1);
      sqlKeywords.add("CONFIRM", Token.KEYWORD1);
      sqlKeywords.add("CONSTRAINT", Token.KEYWORD1);
      sqlKeywords.add("CONTAINS", Token.KEYWORD1);
      sqlKeywords.add("CONTAINSTABLE", Token.KEYWORD1);
      sqlKeywords.add("CONTINUE", Token.KEYWORD1);
      sqlKeywords.add("CONTROLROW", Token.KEYWORD1);
      sqlKeywords.add("CREATE", Token.KEYWORD1);
      sqlKeywords.add("CURRENT", Token.KEYWORD1);
      sqlKeywords.add("CURRENT_DATE", Token.KEYWORD1);
      sqlKeywords.add("CURRENT_TIME", Token.KEYWORD1);
      sqlKeywords.add("CURSOR", Token.KEYWORD1);
      sqlKeywords.add("DATABASE", Token.KEYWORD1);
      sqlKeywords.add("DBCC", Token.KEYWORD1);
      sqlKeywords.add("DEALLOCATE", Token.KEYWORD1);
      sqlKeywords.add("DECLARE", Token.KEYWORD1);
      sqlKeywords.add("DEFAULT", Token.KEYWORD1);
      sqlKeywords.add("DELETE", Token.KEYWORD1);
      sqlKeywords.add("DENY", Token.KEYWORD1);
      sqlKeywords.add("DESC", Token.KEYWORD1);
      sqlKeywords.add("DISK", Token.KEYWORD1);
      sqlKeywords.add("DISTINCT", Token.KEYWORD1);
      sqlKeywords.add("DISTRIBUTED", Token.KEYWORD1);
      sqlKeywords.add("DOUBLE", Token.KEYWORD1);
      sqlKeywords.add("DROP", Token.KEYWORD1);
      sqlKeywords.add("DUMMY", Token.KEYWORD1);
      sqlKeywords.add("DUMP", Token.KEYWORD1);
      sqlKeywords.add("ELSE", Token.KEYWORD1);
      sqlKeywords.add("END", Token.KEYWORD1);
      sqlKeywords.add("ERRLVL", Token.KEYWORD1);
      sqlKeywords.add("ERROREXIT", Token.KEYWORD1);
      sqlKeywords.add("ESCAPE", Token.KEYWORD1);
      sqlKeywords.add("EXCEPT", Token.KEYWORD1);
      sqlKeywords.add("EXEC", Token.KEYWORD1);
      sqlKeywords.add("EXECUTE", Token.KEYWORD1);
      sqlKeywords.add("EXIT", Token.KEYWORD1);
      sqlKeywords.add("FETCH", Token.KEYWORD1);
      sqlKeywords.add("FILE", Token.KEYWORD1);
      sqlKeywords.add("FILLFACTOR", Token.KEYWORD1);
      sqlKeywords.add("FLOPPY", Token.KEYWORD1);
      sqlKeywords.add("FOR", Token.KEYWORD1);
      sqlKeywords.add("FOREIGN", Token.KEYWORD1);
      sqlKeywords.add("FREETEXT", Token.KEYWORD1);
      sqlKeywords.add("FREETEXTTABLE", Token.KEYWORD1);
      sqlKeywords.add("FROM", Token.KEYWORD1);
      sqlKeywords.add("FULL", Token.KEYWORD1);
      sqlKeywords.add("GOTO", Token.KEYWORD1);
      sqlKeywords.add("GRANT", Token.KEYWORD1);
      sqlKeywords.add("GROUP", Token.KEYWORD1);
      sqlKeywords.add("HAVING", Token.KEYWORD1);
      sqlKeywords.add("HOLDLOCK", Token.KEYWORD1);
      sqlKeywords.add("IDENTITY_INSERT", Token.KEYWORD1);
      sqlKeywords.add("IDENTITYCOL", Token.KEYWORD1);
      sqlKeywords.add("ID", Token.KEYWORD1);
      sqlKeywords.add("IF", Token.KEYWORD1);
      sqlKeywords.add("INDEX", Token.KEYWORD1);
      sqlKeywords.add("INNER", Token.KEYWORD1);
      sqlKeywords.add("INSERT", Token.KEYWORD1);
      sqlKeywords.add("INTO", Token.KEYWORD1);
      sqlKeywords.add("IS", Token.KEYWORD1);
      sqlKeywords.add("ISOLATION", Token.KEYWORD1);
      sqlKeywords.add("KEY", Token.KEYWORD1);
      sqlKeywords.add("KILL", Token.KEYWORD1);
      sqlKeywords.add("LEVEL", Token.KEYWORD1);
      sqlKeywords.add("LINENO", Token.KEYWORD1);
      sqlKeywords.add("LOAD", Token.KEYWORD1);
      sqlKeywords.add("MAX", Token.KEYWORD1);
      sqlKeywords.add("MIN", Token.KEYWORD1);
      sqlKeywords.add("MIRROREXIT", Token.KEYWORD1);
      sqlKeywords.add("NATIONAL", Token.KEYWORD1);
      sqlKeywords.add("NOCHECK", Token.KEYWORD1);
      sqlKeywords.add("NONCLUSTERED", Token.KEYWORD1);
      sqlKeywords.add("OF", Token.KEYWORD1);
      sqlKeywords.add("OFF", Token.KEYWORD1);
      sqlKeywords.add("OFFSETS", Token.KEYWORD1);
      sqlKeywords.add("ON", Token.KEYWORD1);
      sqlKeywords.add("ONCE", Token.KEYWORD1);
      sqlKeywords.add("ONLY", Token.KEYWORD1);
      sqlKeywords.add("OPEN", Token.KEYWORD1);
      sqlKeywords.add("OPENDATASOURCE", Token.KEYWORD1);
      sqlKeywords.add("OPENQUERY", Token.KEYWORD1);
      sqlKeywords.add("OPENROWSET", Token.KEYWORD1);
      sqlKeywords.add("OPTION", Token.KEYWORD1);
      sqlKeywords.add("ORDER", Token.KEYWORD1);
      sqlKeywords.add("OVER", Token.KEYWORD1);
      sqlKeywords.add("PERCENT", Token.KEYWORD1);
      sqlKeywords.add("PERM", Token.KEYWORD1);
      sqlKeywords.add("PERMANENT", Token.KEYWORD1);
      sqlKeywords.add("PIPE", Token.KEYWORD1);
      sqlKeywords.add("PLAN", Token.KEYWORD1);
      sqlKeywords.add("PRECISION", Token.KEYWORD1);
      sqlKeywords.add("PREPARE", Token.KEYWORD1);
      sqlKeywords.add("PRIMARY", Token.KEYWORD1);
      sqlKeywords.add("PRINT", Token.KEYWORD1);
      sqlKeywords.add("PRIVILEGES", Token.KEYWORD1);
      sqlKeywords.add("PROC", Token.KEYWORD1);
      sqlKeywords.add("PROCEDURE", Token.KEYWORD1);
      sqlKeywords.add("PROCESSEXIT", Token.KEYWORD1);
      sqlKeywords.add("PUBLIC", Token.KEYWORD1);
      sqlKeywords.add("QUOTED_IDENTIFIER", Token.KEYWORD1);
      sqlKeywords.add("RAISERROR", Token.KEYWORD1);
      sqlKeywords.add("READ", Token.KEYWORD1);
      sqlKeywords.add("READTEXT", Token.KEYWORD1);
      sqlKeywords.add("RECONFIGURE", Token.KEYWORD1);
      sqlKeywords.add("REFERENCES", Token.KEYWORD1);
      sqlKeywords.add("REPEATABLE", Token.KEYWORD1);
      sqlKeywords.add("REPLICATION", Token.KEYWORD1);
      sqlKeywords.add("RESTORE", Token.KEYWORD1);
      sqlKeywords.add("RESTRICT", Token.KEYWORD1);
      sqlKeywords.add("RETURN", Token.KEYWORD1);
      sqlKeywords.add("REVOKE", Token.KEYWORD1);
      sqlKeywords.add("ROLLBACK", Token.KEYWORD1);
      sqlKeywords.add("ROWGUIDCOL", Token.KEYWORD1);
      sqlKeywords.add("RULE", Token.KEYWORD1);
      sqlKeywords.add("SAVE", Token.KEYWORD1);
      sqlKeywords.add("SCHEMA", Token.KEYWORD1);
      sqlKeywords.add("SELECT", Token.KEYWORD1);
      sqlKeywords.add("SERIALIZABLE", Token.KEYWORD1);
      sqlKeywords.add("SET", Token.KEYWORD1);
      sqlKeywords.add("SETUSER", Token.KEYWORD1);
      sqlKeywords.add("SHUTDOWN", Token.KEYWORD1);
      sqlKeywords.add("STATISTICS", Token.KEYWORD1);
      sqlKeywords.add("TABLE", Token.KEYWORD1);
      sqlKeywords.add("TAPE", Token.KEYWORD1);
      sqlKeywords.add("TEMP", Token.KEYWORD1);
      sqlKeywords.add("TEMPORARY", Token.KEYWORD1);
      sqlKeywords.add("TEXTIMAGE_ON", Token.KEYWORD1);
      sqlKeywords.add("THEN", Token.KEYWORD1);
      sqlKeywords.add("TO", Token.KEYWORD1);
      sqlKeywords.add("TOP", Token.KEYWORD1);
      sqlKeywords.add("TRAN", Token.KEYWORD1);
      sqlKeywords.add("TRANSACTION", Token.KEYWORD1);
      sqlKeywords.add("TRIGGER", Token.KEYWORD1);
      sqlKeywords.add("TRUNCATE", Token.KEYWORD1);
      sqlKeywords.add("TSEQUAL", Token.KEYWORD1);
      sqlKeywords.add("UNCOMMITTED", Token.KEYWORD1);
      sqlKeywords.add("UNION", Token.KEYWORD1);
      sqlKeywords.add("UNIQUE", Token.KEYWORD1);
      sqlKeywords.add("UPDATE", Token.KEYWORD1);
      sqlKeywords.add("UPDATETEXT", Token.KEYWORD1);
      sqlKeywords.add("USE", Token.KEYWORD1);
      sqlKeywords.add("VALUES", Token.KEYWORD1);
      sqlKeywords.add("VARYING", Token.KEYWORD1);
      sqlKeywords.add("VIEW", Token.KEYWORD1);
      sqlKeywords.add("WAITFOR", Token.KEYWORD1);
      sqlKeywords.add("WHEN", Token.KEYWORD1);
      sqlKeywords.add("WHERE", Token.KEYWORD1);
      sqlKeywords.add("WHILE", Token.KEYWORD1);
      sqlKeywords.add("WITH", Token.KEYWORD1);
      sqlKeywords.add("WORK", Token.KEYWORD1);
      sqlKeywords.add("WRITETEXT", Token.KEYWORD1);

      sqlKeywords.add("binary", Token.KEYWORD1);
      sqlKeywords.add("bit", Token.KEYWORD1);
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

      sqlKeywords.add("@@CONNECTIONS", Token.KEYWORD2);
      sqlKeywords.add("@@CPU_BUSY", Token.KEYWORD2);
      sqlKeywords.add("@@CURSOR_ROWS", Token.KEYWORD2);
      sqlKeywords.add("@@DATEFIRST", Token.KEYWORD2);
      sqlKeywords.add("@@DBTS", Token.KEYWORD2);
      sqlKeywords.add("@@ERROR", Token.KEYWORD2);
      sqlKeywords.add("@@FETCH_STATUS", Token.KEYWORD2);
      sqlKeywords.add("@@IDENTITY", Token.KEYWORD2);
      sqlKeywords.add("@@IDLE", Token.KEYWORD2);
      sqlKeywords.add("@@IO_BUSY", Token.KEYWORD2);
      sqlKeywords.add("@@LANGID", Token.KEYWORD2);
      sqlKeywords.add("@@LANGUAGE", Token.KEYWORD2);
      sqlKeywords.add("@@LOCK_TIMEOUT", Token.KEYWORD2);
      sqlKeywords.add("@@MAX_CONNECTIONS", Token.KEYWORD2);
      sqlKeywords.add("@@MAX_PRECISION", Token.KEYWORD2);
      sqlKeywords.add("@@NESTLEVEL", Token.KEYWORD2);
      sqlKeywords.add("@@OPTIONS", Token.KEYWORD2);
      sqlKeywords.add("@@PACK_RECEIVED", Token.KEYWORD2);
      sqlKeywords.add("@@PACK_SENT", Token.KEYWORD2);
      sqlKeywords.add("@@PACKET_ERRORS", Token.KEYWORD2);
      sqlKeywords.add("@@PROCID", Token.KEYWORD2);
      sqlKeywords.add("@@REMSERVER", Token.KEYWORD2);
      sqlKeywords.add("@@ROWCOUNT", Token.KEYWORD2);
      sqlKeywords.add("@@SERVERNAME", Token.KEYWORD2);
      sqlKeywords.add("@@SERVICENAME", Token.KEYWORD2);
      sqlKeywords.add("@@SPID", Token.KEYWORD2);
      sqlKeywords.add("@@TEXTSIZE", Token.KEYWORD2);
      sqlKeywords.add("@@TIMETICKS", Token.KEYWORD2);
      sqlKeywords.add("@@TOTAL_ERRORS", Token.KEYWORD2);
      sqlKeywords.add("@@TOTAL_READ", Token.KEYWORD2);
      sqlKeywords.add("@@TOTAL_WRITE", Token.KEYWORD2);
      sqlKeywords.add("@@TRANCOUNT", Token.KEYWORD2);
      sqlKeywords.add("@@VERSION", Token.KEYWORD2);
      sqlKeywords.add("ABS", Token.KEYWORD2);
      sqlKeywords.add("ACOS", Token.KEYWORD2);
      sqlKeywords.add("APP_NAME", Token.KEYWORD2);
      sqlKeywords.add("ASCII", Token.KEYWORD2);
      sqlKeywords.add("ASIN", Token.KEYWORD2);
      sqlKeywords.add("ATAN", Token.KEYWORD2);
      sqlKeywords.add("ATN2", Token.KEYWORD2);
      sqlKeywords.add("AVG", Token.KEYWORD2);
      sqlKeywords.add("CASE", Token.KEYWORD2);
      sqlKeywords.add("CAST", Token.KEYWORD2);
      sqlKeywords.add("CEILING", Token.KEYWORD2);
      sqlKeywords.add("CHARINDEX", Token.KEYWORD2);
      sqlKeywords.add("COALESCE", Token.KEYWORD2);
      sqlKeywords.add("COL_LENGTH", Token.KEYWORD2);
      sqlKeywords.add("COL_NAME", Token.KEYWORD2);
      sqlKeywords.add("COLUMNPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("CONVERT", Token.KEYWORD2);
      sqlKeywords.add("COS", Token.KEYWORD2);
      sqlKeywords.add("COT", Token.KEYWORD2);
      sqlKeywords.add("COUNT", Token.KEYWORD2);
      sqlKeywords.add("CURRENT_TIME", Token.KEYWORD2);
      sqlKeywords.add("CURRENT_DATE", Token.KEYWORD2);
      sqlKeywords.add("CURRENT_TIMESTAMP", Token.KEYWORD2);
      sqlKeywords.add("CURRENT_USER", Token.KEYWORD2);
      sqlKeywords.add("CURSOR_STATUS", Token.KEYWORD2);
      sqlKeywords.add("DATABASEPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("DATALENGTH", Token.KEYWORD2);
      sqlKeywords.add("DATEADD", Token.KEYWORD2);
      sqlKeywords.add("DATEDIFF", Token.KEYWORD2);
      sqlKeywords.add("DATENAME", Token.KEYWORD2);
      sqlKeywords.add("DATEPART", Token.KEYWORD2);
      sqlKeywords.add("DAY", Token.KEYWORD2);
      sqlKeywords.add("DB_ID", Token.KEYWORD2);
      sqlKeywords.add("DB_NAME", Token.KEYWORD2);
      sqlKeywords.add("DEGREES", Token.KEYWORD2);
      sqlKeywords.add("DIFFERENCE", Token.KEYWORD2);
      sqlKeywords.add("EXP", Token.KEYWORD2);
      sqlKeywords.add("FILE_ID", Token.KEYWORD2);
      sqlKeywords.add("FILE_NAME", Token.KEYWORD2);
      sqlKeywords.add("FILEGROUP_ID", Token.KEYWORD2);
      sqlKeywords.add("FILEGROUP_NAME", Token.KEYWORD2);
      sqlKeywords.add("FILEGROUPPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("FILEPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("FLOOR", Token.KEYWORD2);
      sqlKeywords.add("FORMATMESSAGE", Token.KEYWORD2);
      sqlKeywords.add("FULLTEXTCATALOGPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("FULLTEXTSERVICEPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("GETANSINULL", Token.KEYWORD2);
      sqlKeywords.add("GETDATE", Token.KEYWORD2);
      sqlKeywords.add("HOST_ID", Token.KEYWORD2);
      sqlKeywords.add("HOST_NAME", Token.KEYWORD2);
      sqlKeywords.add("IDENT_INCR", Token.KEYWORD2);
      sqlKeywords.add("IDENT_SEED", Token.KEYWORD2);
      sqlKeywords.add("IDENTITY_INSERT", Token.KEYWORD2);
      sqlKeywords.add("INDEX_COL", Token.KEYWORD2);
      sqlKeywords.add("INDEXPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("IS_MEMBER", Token.KEYWORD2);
      sqlKeywords.add("IS_SRVROLEMEMBER", Token.KEYWORD2);
      sqlKeywords.add("ISDATE", Token.KEYWORD2);
      sqlKeywords.add("ISNULL", Token.KEYWORD2);
      sqlKeywords.add("ISNUMERIC", Token.KEYWORD2);
      sqlKeywords.add("LEFT", Token.KEYWORD2);
      sqlKeywords.add("LEN", Token.KEYWORD2);
      sqlKeywords.add("LOG", Token.KEYWORD2);
      sqlKeywords.add("LOG10", Token.KEYWORD2);
      sqlKeywords.add("LOWER", Token.KEYWORD2);
      sqlKeywords.add("LTRIM", Token.KEYWORD2);
      sqlKeywords.add("MONTH", Token.KEYWORD2);
      sqlKeywords.add("NEWID", Token.KEYWORD2);
      sqlKeywords.add("NULLIF", Token.KEYWORD2);
      sqlKeywords.add("OBJECT_ID", Token.KEYWORD2);
      sqlKeywords.add("OBJECT_NAME", Token.KEYWORD2);
      sqlKeywords.add("OBJECTPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("PARSENAME", Token.KEYWORD2);
      sqlKeywords.add("PATINDEX", Token.KEYWORD2);
      sqlKeywords.add("PERMISSIONS", Token.KEYWORD2);
      sqlKeywords.add("PI", Token.KEYWORD2);
      sqlKeywords.add("POWER", Token.KEYWORD2);
      sqlKeywords.add("QUOTENAME", Token.KEYWORD2);
      sqlKeywords.add("RADIANS", Token.KEYWORD2);
      sqlKeywords.add("RAND", Token.KEYWORD2);
      sqlKeywords.add("REPLACE", Token.KEYWORD2);
      sqlKeywords.add("REPLICATE", Token.KEYWORD2);
      sqlKeywords.add("REVERSE", Token.KEYWORD2);
      sqlKeywords.add("RIGHT", Token.KEYWORD2);
      sqlKeywords.add("ROUND", Token.KEYWORD2);
      sqlKeywords.add("RTRIM", Token.KEYWORD2);
      sqlKeywords.add("SESSION_USER", Token.KEYWORD2);
      sqlKeywords.add("SIGN", Token.KEYWORD2);
      sqlKeywords.add("SIN", Token.KEYWORD2);
      sqlKeywords.add("SOUNDEX", Token.KEYWORD2);
      sqlKeywords.add("SPACE", Token.KEYWORD2);
      sqlKeywords.add("SQRT", Token.KEYWORD2);
      sqlKeywords.add("SQUARE", Token.KEYWORD2);
      sqlKeywords.add("STATS_DATE", Token.KEYWORD2);
      sqlKeywords.add("STR", Token.KEYWORD2);
      sqlKeywords.add("STUFF", Token.KEYWORD2);
      sqlKeywords.add("SUBSTRING", Token.KEYWORD2);
      sqlKeywords.add("SUSER_ID", Token.KEYWORD2);
      sqlKeywords.add("SUSER_NAME", Token.KEYWORD2);
      sqlKeywords.add("SUSER_SID", Token.KEYWORD2);
      sqlKeywords.add("SUSER_SNAME", Token.KEYWORD2);
      sqlKeywords.add("SYSTEM_USER", Token.KEYWORD2);
      sqlKeywords.add("TAN", Token.KEYWORD2);
      sqlKeywords.add("TEXTPTR", Token.KEYWORD2);
      sqlKeywords.add("TEXTVALID", Token.KEYWORD2);
      sqlKeywords.add("TYPEPROPERTY", Token.KEYWORD2);
      sqlKeywords.add("UNICODE", Token.KEYWORD2);
      sqlKeywords.add("UPPER", Token.KEYWORD2);
      sqlKeywords.add("USER_ID", Token.KEYWORD2);
      sqlKeywords.add("USER_NAME", Token.KEYWORD2);
      sqlKeywords.add("USER", Token.KEYWORD2);
      sqlKeywords.add("YEAR", Token.KEYWORD2);

      sqlKeywords.add("ALL", Token.KEYWORD1);
      sqlKeywords.add("AND", Token.KEYWORD1);
      sqlKeywords.add("ANY", Token.KEYWORD1);
      sqlKeywords.add("BETWEEN", Token.KEYWORD1);
      sqlKeywords.add("CROSS", Token.KEYWORD1);
      sqlKeywords.add("EXISTS", Token.KEYWORD1);
      sqlKeywords.add("IN", Token.KEYWORD1);
      sqlKeywords.add("INTERSECT", Token.KEYWORD1);
      sqlKeywords.add("JOIN", Token.KEYWORD1);
      sqlKeywords.add("LIKE", Token.KEYWORD1);
      sqlKeywords.add("NOT", Token.KEYWORD1);
      sqlKeywords.add("NULL", Token.KEYWORD1);
      sqlKeywords.add("OR", Token.KEYWORD1);
      sqlKeywords.add("OUTER", Token.KEYWORD1);
      sqlKeywords.add("SOME", Token.KEYWORD1);

      sqlKeywords.add("sp_add_agent_parameter", Token.KEYWORD3);
      sqlKeywords.add("sp_add_agent_profile", Token.KEYWORD3);
      sqlKeywords.add("sp_add_alert", Token.KEYWORD3);
      sqlKeywords.add("sp_add_category", Token.KEYWORD3);
      sqlKeywords.add("sp_add_data_file_recover_suspect_db", Token.KEYWORD3);
      sqlKeywords.add("sp_add_job", Token.KEYWORD3);
      sqlKeywords.add("sp_add_jobschedule", Token.KEYWORD3);
      sqlKeywords.add("sp_add_jobserver", Token.KEYWORD3);
      sqlKeywords.add("sp_add_jobstep", Token.KEYWORD3);
      sqlKeywords.add("sp_add_log_file_recover_suspect_db", Token.KEYWORD3);
      sqlKeywords.add("sp_add_notification", Token.KEYWORD3);
      sqlKeywords.add("sp_add_operator", Token.KEYWORD3);
      sqlKeywords.add("sp_add_targetservergroup", Token.KEYWORD3);
      sqlKeywords.add("sp_add_targetsvrgrp_member", Token.KEYWORD3);
      sqlKeywords.add("sp_addalias", Token.KEYWORD3);
      sqlKeywords.add("sp_addapprole", Token.KEYWORD3);
      sqlKeywords.add("sp_addarticle", Token.KEYWORD3);
      sqlKeywords.add("sp_adddistpublisher", Token.KEYWORD3);
      sqlKeywords.add("sp_adddistributiondb", Token.KEYWORD3);
      sqlKeywords.add("sp_adddistributor", Token.KEYWORD3);
      sqlKeywords.add("sp_addextendedproc", Token.KEYWORD3);
      sqlKeywords.add("sp_addgroup", Token.KEYWORD3);
      sqlKeywords.add("sp_addlinkedserver", Token.KEYWORD3);
      sqlKeywords.add("sp_addlinkedsrvlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_addlinkedsrvlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_addlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergearticle", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergefilter", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergepublication", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergepullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergepullsubscription_agent", Token.KEYWORD3);
      sqlKeywords.add("sp_addmergesubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_addmessage", Token.KEYWORD3);
      sqlKeywords.add("sp_addpublication", Token.KEYWORD3);
      sqlKeywords.add("sp_addpublication_snapshot", Token.KEYWORD3);
      sqlKeywords.add("sp_addpublisher70", Token.KEYWORD3);
      sqlKeywords.add("sp_addpullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_addpullsubscription_agent", Token.KEYWORD3);
      sqlKeywords.add("sp_addremotelogin", Token.KEYWORD3);
      sqlKeywords.add("sp_addrole", Token.KEYWORD3);
      sqlKeywords.add("sp_addrolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_addserver", Token.KEYWORD3);
      sqlKeywords.add("sp_addsrvrolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_addsubscriber", Token.KEYWORD3);
      sqlKeywords.add("sp_addsubscriber_schedule", Token.KEYWORD3);
      sqlKeywords.add("sp_addsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_addsynctriggers", Token.KEYWORD3);
      sqlKeywords.add("sp_addtabletocontents", Token.KEYWORD3);
      sqlKeywords.add("sp_addtask", Token.KEYWORD3);
      sqlKeywords.add("sp_addtype", Token.KEYWORD3);
      sqlKeywords.add("sp_addumpdevice", Token.KEYWORD3);
      sqlKeywords.add("sp_adduser", Token.KEYWORD3);
      sqlKeywords.add("sp_altermessage", Token.KEYWORD3);
      sqlKeywords.add("sp_apply_job_to_targets", Token.KEYWORD3);
      sqlKeywords.add("sp_approlepassword", Token.KEYWORD3);
      sqlKeywords.add("sp_article_validation", Token.KEYWORD3);
      sqlKeywords.add("sp_articlecolumn", Token.KEYWORD3);
      sqlKeywords.add("sp_articlefilter", Token.KEYWORD3);
      sqlKeywords.add("sp_articlesynctranprocs", Token.KEYWORD3);
      sqlKeywords.add("sp_articleview", Token.KEYWORD3);
      sqlKeywords.add("sp_attach_db", Token.KEYWORD3);
      sqlKeywords.add("sp_attach_single_file_db", Token.KEYWORD3);
      sqlKeywords.add("sp_autostats", Token.KEYWORD3);
      sqlKeywords.add("sp_bindefault", Token.KEYWORD3);
      sqlKeywords.add("sp_bindrule", Token.KEYWORD3);
      sqlKeywords.add("sp_bindsession", Token.KEYWORD3);
      sqlKeywords.add("sp_browsereplcmds", Token.KEYWORD3);
      sqlKeywords.add("sp_catalogs", Token.KEYWORD3);
      sqlKeywords.add("sp_certify_removable", Token.KEYWORD3);
      sqlKeywords.add("sp_change_agent_parameter", Token.KEYWORD3);
      sqlKeywords.add("sp_change_agent_profile", Token.KEYWORD3);
      sqlKeywords.add("sp_change_subscription_properties", Token.KEYWORD3);
      sqlKeywords.add("sp_change_users_login", Token.KEYWORD3);
      sqlKeywords.add("sp_changearticle", Token.KEYWORD3);
      sqlKeywords.add("sp_changedbowner", Token.KEYWORD3);
      sqlKeywords.add("sp_changedistpublisher", Token.KEYWORD3);
      sqlKeywords.add("sp_changedistributiondb", Token.KEYWORD3);
      sqlKeywords.add("sp_changedistributor_password", Token.KEYWORD3);
      sqlKeywords.add("sp_changedistributor_property", Token.KEYWORD3);
      sqlKeywords.add("sp_changegroup", Token.KEYWORD3);
      sqlKeywords.add("sp_changemergearticle", Token.KEYWORD3);
      sqlKeywords.add("sp_changemergefilter", Token.KEYWORD3);
      sqlKeywords.add("sp_changemergepublication", Token.KEYWORD3);
      sqlKeywords.add("sp_changemergepullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_changemergesubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_changeobjectowner", Token.KEYWORD3);
      sqlKeywords.add("sp_changepublication", Token.KEYWORD3);
      sqlKeywords.add("sp_changesubscriber", Token.KEYWORD3);
      sqlKeywords.add("sp_changesubscriber_schedule", Token.KEYWORD3);
      sqlKeywords.add("sp_changesubstatus", Token.KEYWORD3);
      sqlKeywords.add("sp_check_for_sync_trigger", Token.KEYWORD3);
      sqlKeywords.add("sp_column_privileges", Token.KEYWORD3);
      sqlKeywords.add("sp_column_privileges_ex", Token.KEYWORD3);
      sqlKeywords.add("sp_columns", Token.KEYWORD3);
      sqlKeywords.add("sp_columns_ex", Token.KEYWORD3);
      sqlKeywords.add("sp_configure", Token.KEYWORD3);
      sqlKeywords.add("sp_create_removable", Token.KEYWORD3);
      sqlKeywords.add("sp_createorphan", Token.KEYWORD3);
      sqlKeywords.add("sp_createstats", Token.KEYWORD3);
      sqlKeywords.add("sp_cursor", Token.KEYWORD3);
      sqlKeywords.add("sp_cursor_list", Token.KEYWORD3);
      sqlKeywords.add("sp_cursorclose", Token.KEYWORD3);
      sqlKeywords.add("sp_cursorexecute", Token.KEYWORD3);
      sqlKeywords.add("sp_cursorfetch", Token.KEYWORD3);
      sqlKeywords.add("sp_cursoropen", Token.KEYWORD3);
      sqlKeywords.add("sp_cursoroption", Token.KEYWORD3);
      sqlKeywords.add("sp_cursorprepare", Token.KEYWORD3);
      sqlKeywords.add("sp_cursorunprepare", Token.KEYWORD3);
      sqlKeywords.add("sp_cycle_errorlog", Token.KEYWORD3);
      sqlKeywords.add("sp_databases", Token.KEYWORD3);
      sqlKeywords.add("sp_datatype_info", Token.KEYWORD3);
      sqlKeywords.add("sp_dbcmptlevel", Token.KEYWORD3);
      sqlKeywords.add("sp_dbfixedrolepermission", Token.KEYWORD3);
      sqlKeywords.add("sp_dboption", Token.KEYWORD3);
      sqlKeywords.add("sp_defaultdb", Token.KEYWORD3);
      sqlKeywords.add("sp_defaultlanguage", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_alert", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_backuphistory", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_category", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_job", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_jobschedule", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_jobserver", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_jobstep", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_notification", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_operator", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_targetserver", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_targetservergroup", Token.KEYWORD3);
      sqlKeywords.add("sp_delete_targetsvrgrp_member", Token.KEYWORD3);
      sqlKeywords.add("sp_deletemergeconflictrow", Token.KEYWORD3);
      sqlKeywords.add("sp_denylogin", Token.KEYWORD3);
      sqlKeywords.add("sp_depends", Token.KEYWORD3);
      sqlKeywords.add("sp_describe_cursor", Token.KEYWORD3);
      sqlKeywords.add("sp_describe_cursor_columns", Token.KEYWORD3);
      sqlKeywords.add("sp_describe_cursor_tables", Token.KEYWORD3);
      sqlKeywords.add("sp_detach_db", Token.KEYWORD3);
      sqlKeywords.add("sp_drop_agent_parameter", Token.KEYWORD3);
      sqlKeywords.add("sp_drop_agent_profile", Token.KEYWORD3);
      sqlKeywords.add("sp_dropalias", Token.KEYWORD3);
      sqlKeywords.add("sp_dropapprole", Token.KEYWORD3);
      sqlKeywords.add("sp_droparticle", Token.KEYWORD3);
      sqlKeywords.add("sp_dropdevice", Token.KEYWORD3);
      sqlKeywords.add("sp_dropdistpublisher", Token.KEYWORD3);
      sqlKeywords.add("sp_dropdistributiondb", Token.KEYWORD3);
      sqlKeywords.add("sp_dropdistributor", Token.KEYWORD3);
      sqlKeywords.add("sp_dropextendedproc", Token.KEYWORD3);
      sqlKeywords.add("sp_dropgroup", Token.KEYWORD3);
      sqlKeywords.add("sp_droplinkedsrvlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_droplinkedsrvlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_droplogin", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmergearticle", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmergefilter", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmergepublication", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmergepullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmergesubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_dropmessage", Token.KEYWORD3);
      sqlKeywords.add("sp_droporphans", Token.KEYWORD3);
      sqlKeywords.add("sp_droppublication", Token.KEYWORD3);
      sqlKeywords.add("sp_droppullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_dropremotelogin", Token.KEYWORD3);
      sqlKeywords.add("sp_droprole", Token.KEYWORD3);
      sqlKeywords.add("sp_droprolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_dropserver", Token.KEYWORD3);
      sqlKeywords.add("sp_dropsrvrolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_dropsubscriber", Token.KEYWORD3);
      sqlKeywords.add("sp_dropsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_droptask", Token.KEYWORD3);
      sqlKeywords.add("sp_droptype", Token.KEYWORD3);
      sqlKeywords.add("sp_dropuser", Token.KEYWORD3);
      sqlKeywords.add("sp_dropwebtask", Token.KEYWORD3);
      sqlKeywords.add("sp_dsninfo", Token.KEYWORD3);
      sqlKeywords.add("sp_dumpparamcmd", Token.KEYWORD3);
      sqlKeywords.add("sp_enumcodepages", Token.KEYWORD3);
      sqlKeywords.add("sp_enumcustomresolvers", Token.KEYWORD3);
      sqlKeywords.add("sp_enumdsn", Token.KEYWORD3);
      sqlKeywords.add("sp_enumfullsubscribers", Token.KEYWORD3);
      sqlKeywords.add("sp_execute", Token.KEYWORD3);
      sqlKeywords.add("sp_executesql", Token.KEYWORD3);
      sqlKeywords.add("sp_expired_subscription_cleanup", Token.KEYWORD3);
      sqlKeywords.add("sp_fkeys", Token.KEYWORD3);
      sqlKeywords.add("sp_foreignkeys", Token.KEYWORD3);
      sqlKeywords.add("sp_fulltext_catalog", Token.KEYWORD3);
      sqlKeywords.add("sp_fulltext_column", Token.KEYWORD3);
      sqlKeywords.add("sp_fulltext_database", Token.KEYWORD3);
      sqlKeywords.add("sp_fulltext_service", Token.KEYWORD3);
      sqlKeywords.add("sp_fulltext_table", Token.KEYWORD3);
      sqlKeywords.add("sp_generatefilters", Token.KEYWORD3);
      sqlKeywords.add("sp_get_distributor", Token.KEYWORD3);
      sqlKeywords.add("sp_getbindtoken", Token.KEYWORD3);
      sqlKeywords.add("sp_getmergedeletetype", Token.KEYWORD3);
      sqlKeywords.add("sp_grant_publication_access", Token.KEYWORD3);
      sqlKeywords.add("sp_grantdbaccess", Token.KEYWORD3);
      sqlKeywords.add("sp_grantlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_help", Token.KEYWORD3);
      sqlKeywords.add("sp_help_agent_default", Token.KEYWORD3);
      sqlKeywords.add("sp_help_agent_parameter", Token.KEYWORD3);
      sqlKeywords.add("sp_help_agent_profile", Token.KEYWORD3);
      sqlKeywords.add("sp_help_alert", Token.KEYWORD3);
      sqlKeywords.add("sp_help_category", Token.KEYWORD3);
      sqlKeywords.add("sp_help_downloadlist", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_catalogs", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_catalogs_cursor", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_columns", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_columns_cursor", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_tables", Token.KEYWORD3);
      sqlKeywords.add("sp_help_fulltext_tables_cursor", Token.KEYWORD3);
      sqlKeywords.add("sp_help_job", Token.KEYWORD3);
      sqlKeywords.add("sp_help_jobhistory", Token.KEYWORD3);
      sqlKeywords.add("sp_help_jobschedule", Token.KEYWORD3);
      sqlKeywords.add("sp_help_jobserver", Token.KEYWORD3);
      sqlKeywords.add("sp_help_jobstep", Token.KEYWORD3);
      sqlKeywords.add("sp_help_notification", Token.KEYWORD3);
      sqlKeywords.add("sp_help_operator", Token.KEYWORD3);
      sqlKeywords.add("sp_help_publication_access", Token.KEYWORD3);
      sqlKeywords.add("sp_help_targetserver", Token.KEYWORD3);
      sqlKeywords.add("sp_help_targetservergroup", Token.KEYWORD3);
      sqlKeywords.add("sp_helparticle", Token.KEYWORD3);
      sqlKeywords.add("sp_helparticlecolumns", Token.KEYWORD3);
      sqlKeywords.add("sp_helpconstraint", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdb", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdbfixedrole", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdevice", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdistpublisher", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdistributiondb", Token.KEYWORD3);
      sqlKeywords.add("sp_helpdistributor", Token.KEYWORD3);
      sqlKeywords.add("sp_helpextendedproc", Token.KEYWORD3);
      sqlKeywords.add("sp_helpfile", Token.KEYWORD3);
      sqlKeywords.add("sp_helpfilegroup", Token.KEYWORD3);
      sqlKeywords.add("sp_helpgroup", Token.KEYWORD3);
      sqlKeywords.add("sp_helphistory", Token.KEYWORD3);
      sqlKeywords.add("sp_helpindex", Token.KEYWORD3);
      sqlKeywords.add("sp_helplanguage", Token.KEYWORD3);
      sqlKeywords.add("sp_helplinkedsrvlogin", Token.KEYWORD3);
      sqlKeywords.add("sp_helplogins", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergearticle", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergearticleconflicts", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergeconflictrows", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergedeleteconflictrows", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergefilter", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergepublication", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergepullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_helpmergesubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_helpntgroup", Token.KEYWORD3);
      sqlKeywords.add("sp_helppublication", Token.KEYWORD3);
      sqlKeywords.add("sp_helppullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_helpremotelogin", Token.KEYWORD3);
      sqlKeywords.add("sp_helpreplicationdboption", Token.KEYWORD3);
      sqlKeywords.add("sp_helprole", Token.KEYWORD3);
      sqlKeywords.add("sp_helprolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_helprotect", Token.KEYWORD3);
      sqlKeywords.add("sp_helpserver", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsort", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsrvrole", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsrvrolemember", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsubscriberinfo", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_helpsubscription_properties", Token.KEYWORD3);
      sqlKeywords.add("sp_helptask", Token.KEYWORD3);
      sqlKeywords.add("sp_helptext", Token.KEYWORD3);
      sqlKeywords.add("sp_helptrigger", Token.KEYWORD3);
      sqlKeywords.add("sp_helpuser", Token.KEYWORD3);
      sqlKeywords.add("sp_indexes", Token.KEYWORD3);
      sqlKeywords.add("sp_indexoption", Token.KEYWORD3);
      sqlKeywords.add("sp_link_publication", Token.KEYWORD3);
      sqlKeywords.add("sp_linkedservers", Token.KEYWORD3);
      sqlKeywords.add("sp_lock", Token.KEYWORD3);
      sqlKeywords.add("sp_makewebtask", Token.KEYWORD3);
      sqlKeywords.add("sp_manage_jobs_by_login", Token.KEYWORD3);
      sqlKeywords.add("sp_mergedummyupdate", Token.KEYWORD3);
      sqlKeywords.add("sp_mergesubscription_cleanup", Token.KEYWORD3);
      sqlKeywords.add("sp_monitor", Token.KEYWORD3);
      sqlKeywords.add("sp_msx_defect", Token.KEYWORD3);
      sqlKeywords.add("sp_msx_enlist", Token.KEYWORD3);
      sqlKeywords.add("sp_OACreate", Token.KEYWORD3);
      sqlKeywords.add("sp_OADestroy", Token.KEYWORD3);
      sqlKeywords.add("sp_OAGetErrorInfo", Token.KEYWORD3);
      sqlKeywords.add("sp_OAGetProperty", Token.KEYWORD3);
      sqlKeywords.add("sp_OAMethod", Token.KEYWORD3);
      sqlKeywords.add("sp_OASetProperty", Token.KEYWORD3);
      sqlKeywords.add("sp_OAStop", Token.KEYWORD3);
      sqlKeywords.add("sp_password", Token.KEYWORD3);
      sqlKeywords.add("sp_pkeys", Token.KEYWORD3);
      sqlKeywords.add("sp_post_msx_operation", Token.KEYWORD3);
      sqlKeywords.add("sp_prepare", Token.KEYWORD3);
      sqlKeywords.add("sp_primarykeys", Token.KEYWORD3);
      sqlKeywords.add("sp_processmail", Token.KEYWORD3);
      sqlKeywords.add("sp_procoption", Token.KEYWORD3);
      sqlKeywords.add("sp_publication_validation", Token.KEYWORD3);
      sqlKeywords.add("sp_purge_jobhistory", Token.KEYWORD3);
      sqlKeywords.add("sp_purgehistory", Token.KEYWORD3);
      sqlKeywords.add("sp_reassigntask", Token.KEYWORD3);
      sqlKeywords.add("sp_recompile", Token.KEYWORD3);
      sqlKeywords.add("sp_refreshsubscriptions", Token.KEYWORD3);
      sqlKeywords.add("sp_refreshview", Token.KEYWORD3);
      sqlKeywords.add("sp_reinitmergepullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_reinitmergesubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_reinitpullsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_reinitsubscription", Token.KEYWORD3);
      sqlKeywords.add("sp_remoteoption", Token.KEYWORD3);
      sqlKeywords.add("sp_remove_job_from_targets", Token.KEYWORD3);
      sqlKeywords.add("sp_removedbreplication", Token.KEYWORD3);
      sqlKeywords.add("sp_rename", Token.KEYWORD3);
      sqlKeywords.add("sp_renamedb", Token.KEYWORD3);
      sqlKeywords.add("sp_replcmds", Token.KEYWORD3);
      sqlKeywords.add("sp_replcounters", Token.KEYWORD3);
      sqlKeywords.add("sp_repldone", Token.KEYWORD3);
      sqlKeywords.add("sp_replflush", Token.KEYWORD3);
      sqlKeywords.add("sp_replication_agent_checkup", Token.KEYWORD3);
      sqlKeywords.add("sp_replicationdboption", Token.KEYWORD3);
      sqlKeywords.add("sp_replsetoriginator", Token.KEYWORD3);
      sqlKeywords.add("sp_replshowcmds", Token.KEYWORD3);
      sqlKeywords.add("sp_repltrans", Token.KEYWORD3);
      sqlKeywords.add("sp_reset_connection", Token.KEYWORD3);
      sqlKeywords.add("sp_resync_targetserver", Token.KEYWORD3);
      sqlKeywords.add("sp_revoke_publication_access", Token.KEYWORD3);
      sqlKeywords.add("sp_revokedbaccess", Token.KEYWORD3);
      sqlKeywords.add("sp_revokelogin", Token.KEYWORD3);
      sqlKeywords.add("sp_runwebtask", Token.KEYWORD3);
      sqlKeywords.add("sp_script_synctran_commands", Token.KEYWORD3);
      sqlKeywords.add("sp_scriptdelproc", Token.KEYWORD3);
      sqlKeywords.add("sp_scriptinsproc", Token.KEYWORD3);
      sqlKeywords.add("sp_scriptmappedupdproc", Token.KEYWORD3);
      sqlKeywords.add("sp_scriptupdproc", Token.KEYWORD3);
      sqlKeywords.add("sp_sdidebug", Token.KEYWORD3);
      sqlKeywords.add("sp_server_info", Token.KEYWORD3);
      sqlKeywords.add("sp_serveroption", Token.KEYWORD3);
      sqlKeywords.add("sp_serveroption", Token.KEYWORD3);
      sqlKeywords.add("sp_setapprole", Token.KEYWORD3);
      sqlKeywords.add("sp_setnetname", Token.KEYWORD3);
      sqlKeywords.add("sp_spaceused", Token.KEYWORD3);
      sqlKeywords.add("sp_special_columns", Token.KEYWORD3);
      sqlKeywords.add("sp_sproc_columns", Token.KEYWORD3);
      sqlKeywords.add("sp_srvrolepermission", Token.KEYWORD3);
      sqlKeywords.add("sp_start_job", Token.KEYWORD3);
      sqlKeywords.add("sp_statistics", Token.KEYWORD3);
      sqlKeywords.add("sp_stop_job", Token.KEYWORD3);
      sqlKeywords.add("sp_stored_procedures", Token.KEYWORD3);
      sqlKeywords.add("sp_subscription_cleanup", Token.KEYWORD3);
      sqlKeywords.add("sp_table_privileges", Token.KEYWORD3);
      sqlKeywords.add("sp_table_privileges_ex", Token.KEYWORD3);
      sqlKeywords.add("sp_table_validation", Token.KEYWORD3);
      sqlKeywords.add("sp_tableoption", Token.KEYWORD3);
      sqlKeywords.add("sp_tables", Token.KEYWORD3);
      sqlKeywords.add("sp_tables_ex", Token.KEYWORD3);
      sqlKeywords.add("sp_unbindefault", Token.KEYWORD3);
      sqlKeywords.add("sp_unbindrule", Token.KEYWORD3);
      sqlKeywords.add("sp_unprepare", Token.KEYWORD3);
      sqlKeywords.add("sp_update_agent_profile", Token.KEYWORD3);
      sqlKeywords.add("sp_update_alert", Token.KEYWORD3);
      sqlKeywords.add("sp_update_category", Token.KEYWORD3);
      sqlKeywords.add("sp_update_job", Token.KEYWORD3);
      sqlKeywords.add("sp_update_jobschedule", Token.KEYWORD3);
      sqlKeywords.add("sp_update_jobstep", Token.KEYWORD3);
      sqlKeywords.add("sp_update_notification", Token.KEYWORD3);
      sqlKeywords.add("sp_update_operator", Token.KEYWORD3);
      sqlKeywords.add("sp_update_targetservergroup", Token.KEYWORD3);
      sqlKeywords.add("sp_updatestats", Token.KEYWORD3);
      sqlKeywords.add("sp_updatetask", Token.KEYWORD3);
      sqlKeywords.add("sp_validatelogins", Token.KEYWORD3);
      sqlKeywords.add("sp_validname", Token.KEYWORD3);
      sqlKeywords.add("sp_who", Token.KEYWORD3);
      sqlKeywords.add("xp_cmdshell", Token.KEYWORD3);
      sqlKeywords.add("xp_deletemail", Token.KEYWORD3);
      sqlKeywords.add("xp_enumgroups", Token.KEYWORD3);
      sqlKeywords.add("xp_findnextmsg", Token.KEYWORD3);
      sqlKeywords.add("xp_findnextmsg", Token.KEYWORD3);
      sqlKeywords.add("xp_grantlogin", Token.KEYWORD3);
      sqlKeywords.add("xp_logevent", Token.KEYWORD3);
      sqlKeywords.add("xp_loginconfig", Token.KEYWORD3);
      sqlKeywords.add("xp_logininfo", Token.KEYWORD3);
      sqlKeywords.add("xp_msver", Token.KEYWORD3);
      sqlKeywords.add("xp_readmail", Token.KEYWORD3);
      sqlKeywords.add("xp_revokelogin", Token.KEYWORD3);
      sqlKeywords.add("xp_sendmail", Token.KEYWORD3);
      sqlKeywords.add("xp_sprintf", Token.KEYWORD3);
      sqlKeywords.add("xp_sqlinventory", Token.KEYWORD3);
      sqlKeywords.add("xp_sqlmaint", Token.KEYWORD3);
      sqlKeywords.add("xp_sqltrace", Token.KEYWORD3);
      sqlKeywords.add("xp_sscanf", Token.KEYWORD3);
      sqlKeywords.add("xp_startmail", Token.KEYWORD3);
      sqlKeywords.add("xp_stopmail", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_addnewqueue", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_deletequeuedefinition", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_destroyqueue", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_enumqueuedefname", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_enumqueuehandles", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_eventclassrequired", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_flushqueryhistory", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_generate_event", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getappfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getconnectionidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getcpufilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getdbidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getdurationfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_geteventfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_geteventnames", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getevents", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_gethostfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_gethpidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getindidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getntdmfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getntnmfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getobjidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getqueueautostart", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getqueuedestination", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getqueueproperties", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getreadfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getserverfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getseverityfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getspidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getsysobjectsfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_gettextfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getuserfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_getwritefilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_loadqueuedefinition", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_pausequeue", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_restartqueue", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_savequeuedefinition", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setappfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setconnectionidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setcpufilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setdbidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setdurationfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_seteventclassrequired", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_seteventfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_sethostfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_sethpidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setindidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setntdmfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setntnmfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setobjidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setqueryhistory", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setqueueautostart", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setqueuecreateinfo", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setqueuedestination", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setreadfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setserverfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setseverityfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setspidfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setsysobjectsfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_settextfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setuserfilter", Token.KEYWORD3);
      sqlKeywords.add("xp_trace_setwritefilter", Token.KEYWORD3);

      sqlKeywords.add("backupfile", Token.KEYWORD3);
      sqlKeywords.add("backupmediafamily", Token.KEYWORD3);
      sqlKeywords.add("backupmediaset", Token.KEYWORD3);
      sqlKeywords.add("backupset", Token.KEYWORD3);
      sqlKeywords.add("MSagent_parameters", Token.KEYWORD3);
      sqlKeywords.add("MSagent_profiles", Token.KEYWORD3);
      sqlKeywords.add("MSarticles", Token.KEYWORD3);
      sqlKeywords.add("MSdistpublishers", Token.KEYWORD3);
      sqlKeywords.add("MSdistribution_agents", Token.KEYWORD3);
      sqlKeywords.add("MSdistribution_history", Token.KEYWORD3);
      sqlKeywords.add("MSdistributiondbs", Token.KEYWORD3);
      sqlKeywords.add("MSdistributor", Token.KEYWORD3);
      sqlKeywords.add("MSlogreader_agents", Token.KEYWORD3);
      sqlKeywords.add("MSlogreader_history", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_agents", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_contents", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_delete_conflicts", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_genhistory", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_history", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_replinfo", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_subscriptions", Token.KEYWORD3);
      sqlKeywords.add("MSmerge_tombstone", Token.KEYWORD3);
      sqlKeywords.add("MSpublication_access", Token.KEYWORD3);
      sqlKeywords.add("Mspublications", Token.KEYWORD3);
      sqlKeywords.add("Mspublisher_databases", Token.KEYWORD3);
      sqlKeywords.add("MSrepl_commands", Token.KEYWORD3);
      sqlKeywords.add("MSrepl_errors", Token.KEYWORD3);
      sqlKeywords.add("Msrepl_originators", Token.KEYWORD3);
      sqlKeywords.add("MSrepl_transactions", Token.KEYWORD3);
      sqlKeywords.add("MSrepl_version", Token.KEYWORD3);
      sqlKeywords.add("MSreplication_objects", Token.KEYWORD3);
      sqlKeywords.add("MSreplication_subscriptions", Token.KEYWORD3);
      sqlKeywords.add("MSsnapshot_agents", Token.KEYWORD3);
      sqlKeywords.add("MSsnapshot_history", Token.KEYWORD3);
      sqlKeywords.add("MSsubscriber_info", Token.KEYWORD3);
      sqlKeywords.add("MSsubscriber_schedule", Token.KEYWORD3);
      sqlKeywords.add("MSsubscription_properties", Token.KEYWORD3);
      sqlKeywords.add("MSsubscriptions", Token.KEYWORD3);
      sqlKeywords.add("restorefile", Token.KEYWORD3);
      sqlKeywords.add("restorefilegroup", Token.KEYWORD3);
      sqlKeywords.add("restorehistory", Token.KEYWORD3);
      sqlKeywords.add("sysalerts", Token.KEYWORD3);
      sqlKeywords.add("sysallocations", Token.KEYWORD3);
      sqlKeywords.add("sysaltfiles", Token.KEYWORD3);
      sqlKeywords.add("sysarticles", Token.KEYWORD3);
      sqlKeywords.add("sysarticleupdates", Token.KEYWORD3);
      sqlKeywords.add("syscacheobjects", Token.KEYWORD3);
      sqlKeywords.add("syscategories", Token.KEYWORD3);
      sqlKeywords.add("syscharsets", Token.KEYWORD3);
      sqlKeywords.add("syscolumns", Token.KEYWORD3);
      sqlKeywords.add("syscomments", Token.KEYWORD3);
      sqlKeywords.add("sysconfigures", Token.KEYWORD3);
      sqlKeywords.add("sysconstraints", Token.KEYWORD3);
      sqlKeywords.add("syscurconfigs", Token.KEYWORD3);
      sqlKeywords.add("sysdatabases", Token.KEYWORD3);
      sqlKeywords.add("sysdatabases", Token.KEYWORD3);
      sqlKeywords.add("sysdepends", Token.KEYWORD3);
      sqlKeywords.add("sysdevices", Token.KEYWORD3);
      sqlKeywords.add("sysdownloadlist", Token.KEYWORD3);
      sqlKeywords.add("sysfilegroups", Token.KEYWORD3);
      sqlKeywords.add("sysfiles", Token.KEYWORD3);
      sqlKeywords.add("sysforeignkeys", Token.KEYWORD3);
      sqlKeywords.add("sysfulltextcatalogs", Token.KEYWORD3);
      sqlKeywords.add("sysindexes", Token.KEYWORD3);
      sqlKeywords.add("sysindexkeys", Token.KEYWORD3);
      sqlKeywords.add("sysjobhistory", Token.KEYWORD3);
      sqlKeywords.add("sysjobs", Token.KEYWORD3);
      sqlKeywords.add("sysjobschedules", Token.KEYWORD3);
      sqlKeywords.add("sysjobservers", Token.KEYWORD3);
      sqlKeywords.add("sysjobsteps", Token.KEYWORD3);
      sqlKeywords.add("syslanguages", Token.KEYWORD3);
      sqlKeywords.add("syslockinfo", Token.KEYWORD3);
      sqlKeywords.add("syslogins", Token.KEYWORD3);
      sqlKeywords.add("sysmembers", Token.KEYWORD3);
      sqlKeywords.add("sysmergearticles", Token.KEYWORD3);
      sqlKeywords.add("sysmergepublications", Token.KEYWORD3);
      sqlKeywords.add("sysmergeschemachange", Token.KEYWORD3);
      sqlKeywords.add("sysmergesubscriptions", Token.KEYWORD3);
      sqlKeywords.add("sysmergesubsetfilters", Token.KEYWORD3);
      sqlKeywords.add("sysmessages", Token.KEYWORD3);
      sqlKeywords.add("sysnotifications", Token.KEYWORD3);
      sqlKeywords.add("sysobjects", Token.KEYWORD3);
      sqlKeywords.add("sysobjects", Token.KEYWORD3);
      sqlKeywords.add("sysoledbusers", Token.KEYWORD3);
      sqlKeywords.add("sysoperators", Token.KEYWORD3);
      sqlKeywords.add("sysperfinfo", Token.KEYWORD3);
      sqlKeywords.add("syspermissions", Token.KEYWORD3);
      sqlKeywords.add("sysprocesses", Token.KEYWORD3);
      sqlKeywords.add("sysprotects", Token.KEYWORD3);
      sqlKeywords.add("syspublications", Token.KEYWORD3);
      sqlKeywords.add("sysreferences", Token.KEYWORD3);
      sqlKeywords.add("sysremotelogins", Token.KEYWORD3);
      sqlKeywords.add("sysreplicationalerts", Token.KEYWORD3);
      sqlKeywords.add("sysservers", Token.KEYWORD3);
      sqlKeywords.add("sysservers", Token.KEYWORD3);
      sqlKeywords.add("syssubscriptions", Token.KEYWORD3);
      sqlKeywords.add("systargetservergroupmembers", Token.KEYWORD3);
      sqlKeywords.add("systargetservergroups", Token.KEYWORD3);
      sqlKeywords.add("systargetservers", Token.KEYWORD3);
      sqlKeywords.add("systaskids", Token.KEYWORD3);
      sqlKeywords.add("systypes", Token.KEYWORD3);
      sqlKeywords.add("sysusers", Token.KEYWORD3);
    }
    return sqlKeywords;
  }

  // private members
  protected static KeywordMap sqlKeywords;

  private boolean cpp;
  private boolean javadoc;
  protected KeywordMap keywords;
  private int lastOffset;
  private int lastKeyword;

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i+1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line,lastKeyword,len);
    if(id != Token.NULL)
    {
      if(lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset,Token.NULL);
      addToken(len,id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}
