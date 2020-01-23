/*
 * ASMTokenMarker.java - Dawn token marker
 * Copyright (C) 2000 Romain Guy
 * romain.guy@jext.org
 * www.jext.org
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

public class ASMTokenMarker extends TokenMarker
{
  // private members
  private static KeywordMap asmKeywords;
  private KeywordMap keywords;

  private int lastOffset;
  private int lastKeyword;

  public ASMTokenMarker()
  {
    this.keywords = getKeywords();
  }

  public byte markTokensImpl(byte token, Segment line, int lineIndex)
  {
    char[] array = line.array;
    int offset = line.offset;
    lastOffset = offset;
    lastKeyword = offset;
    int length = line.count + offset;

loop: for(int i = offset; i < length; i++)
    {
      int i1 = (i+1);

      char c = array[i];

      switch(token)
      {
        case Token.NULL:
          switch(c)
          {
            case ';':
              addToken(i - lastOffset, token);
              addToken(length - i, Token.COMMENT1);
              token = Token.NULL;
              lastOffset = lastKeyword = length;
              break loop;
            case '"':
              doKeyword(line, i, c);
              addToken(i - lastOffset,token);
              token = Token.LITERAL1;
              lastOffset = lastKeyword = i;
              break;
            case '\'':
              doKeyword(line, i, c);
              addToken(i - lastOffset,token);
              token = Token.LITERAL2;
              lastOffset = lastKeyword = i;
              break;
            case ' ':
              doKeyword(line, i, c);
          }
          break;
        case Token.LITERAL1:
          if(c == '"')
          {
            addToken(i1 - lastOffset, token);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        case Token.LITERAL2:
          if(c == '\'')
          {
            addToken(i1 - lastOffset, Token.LITERAL1);
            token = Token.NULL;
            lastOffset = lastKeyword = i1;
          }
          break;
        default:
          throw new InternalError("Invalid state: " + token);
      }
    }

    if (token == Token.NULL)
      doKeyword(line, length, '\0');

    switch(token)
    {
      case Token.LITERAL1:
      case Token.LITERAL2:
        addToken(length - lastOffset, Token.INVALID);
        token = Token.NULL;
        break;
      default:
        addToken(length - lastOffset, token);
        break;
    }

    return token;
  }

  public static KeywordMap getKeywords()
  {
    if (asmKeywords == null)
    {
      asmKeywords = new KeywordMap(true);
      asmKeywords.add(".186", Token.KEYWORD1);
      asmKeywords.add(".286", Token.KEYWORD1);
      asmKeywords.add(".286P", Token.KEYWORD1);
      asmKeywords.add(".287", Token.KEYWORD1);
      asmKeywords.add(".386", Token.KEYWORD1);
      asmKeywords.add(".386P", Token.KEYWORD1);
      asmKeywords.add(".387", Token.KEYWORD1);
      asmKeywords.add(".486", Token.KEYWORD1);
      asmKeywords.add(".486P", Token.KEYWORD1);
      asmKeywords.add(".586", Token.KEYWORD1);
      asmKeywords.add(".586P", Token.KEYWORD1);
      asmKeywords.add(".686", Token.KEYWORD1);
      asmKeywords.add(".686P", Token.KEYWORD1);
      asmKeywords.add(".8086", Token.KEYWORD1);
      asmKeywords.add(".8087", Token.KEYWORD1);
      asmKeywords.add(".ALPHA", Token.KEYWORD1);
      asmKeywords.add(".BREAK", Token.KEYWORD1);
      asmKeywords.add(".BSS", Token.KEYWORD1);
      asmKeywords.add(".CODE", Token.KEYWORD1);
      asmKeywords.add(".CONST", Token.KEYWORD1);
      asmKeywords.add(".CONTINUE", Token.KEYWORD1);
      asmKeywords.add(".CREF", Token.KEYWORD1);
      asmKeywords.add(".DATA", Token.KEYWORD1);
      asmKeywords.add(".DATA?", Token.KEYWORD1);
      asmKeywords.add(".DOSSEG", Token.KEYWORD1);
      asmKeywords.add(".ELSE", Token.KEYWORD1);
      asmKeywords.add(".ELSEIF", Token.KEYWORD1);
      asmKeywords.add(".ENDIF", Token.KEYWORD1);
      asmKeywords.add(".ENDW", Token.KEYWORD1);
      asmKeywords.add(".ERR", Token.KEYWORD1);
      asmKeywords.add(".ERR1", Token.KEYWORD1);
      asmKeywords.add(".ERR2", Token.KEYWORD1);
      asmKeywords.add(".ERRB", Token.KEYWORD1);
      asmKeywords.add(".ERRDEF", Token.KEYWORD1);
      asmKeywords.add(".ERRDIF", Token.KEYWORD1);
      asmKeywords.add(".ERRDIFI", Token.KEYWORD1);
      asmKeywords.add(".ERRE", Token.KEYWORD1);
      asmKeywords.add(".ERRIDN", Token.KEYWORD1);
      asmKeywords.add(".ERRIDNI", Token.KEYWORD1);
      asmKeywords.add(".ERRNB", Token.KEYWORD1);
      asmKeywords.add(".ERRNDEF", Token.KEYWORD1);
      asmKeywords.add(".ERRNZ", Token.KEYWORD1);
      asmKeywords.add(".EXIT", Token.KEYWORD1);
      asmKeywords.add(".FARDATA", Token.KEYWORD1);
      asmKeywords.add(".FARDATA?", Token.KEYWORD1);
      asmKeywords.add(".IF", Token.KEYWORD1);
      asmKeywords.add(".K3D", Token.KEYWORD1);
      asmKeywords.add(".LALL", Token.KEYWORD1);
      asmKeywords.add(".LFCOND", Token.KEYWORD1);
      asmKeywords.add(".LIST", Token.KEYWORD1);
      asmKeywords.add(".LISTALL", Token.KEYWORD1);
      asmKeywords.add(".LISTIF", Token.KEYWORD1);
      asmKeywords.add(".LISTMACRO", Token.KEYWORD1);
      asmKeywords.add(".LISTMACROALL", Token.KEYWORD1);
      asmKeywords.add(".MMX", Token.KEYWORD1);
      asmKeywords.add(".MODEL", Token.KEYWORD1);
      asmKeywords.add(".MSFLOAT", Token.KEYWORD1);
      asmKeywords.add(".NO87", Token.KEYWORD1);
      asmKeywords.add(".NOCREF", Token.KEYWORD1);
      asmKeywords.add(".NOLIST", Token.KEYWORD1);
      asmKeywords.add(".NOLISTIF", Token.KEYWORD1);
      asmKeywords.add(".NOLISTMACRO", Token.KEYWORD1);
      asmKeywords.add(".RADIX", Token.KEYWORD1);
      asmKeywords.add(".REPEAT", Token.KEYWORD1);
      asmKeywords.add(".SALL", Token.KEYWORD1);
      asmKeywords.add(".SEQ", Token.KEYWORD1);
      asmKeywords.add(".SFCOND", Token.KEYWORD1);
      asmKeywords.add(".STACK", Token.KEYWORD1);
      asmKeywords.add(".STARTUP", Token.KEYWORD1);
      asmKeywords.add(".TEXT", Token.KEYWORD1);
      asmKeywords.add(".TFCOND", Token.KEYWORD1);
      asmKeywords.add(".UNTIL", Token.KEYWORD1);
      asmKeywords.add(".UNTILCXZ", Token.KEYWORD1);
      asmKeywords.add(".WHILE", Token.KEYWORD1);
      asmKeywords.add(".XALL", Token.KEYWORD1);
      asmKeywords.add(".XCREF", Token.KEYWORD1);
      asmKeywords.add(".XLIST", Token.KEYWORD1);
      asmKeywords.add(".XMM", Token.KEYWORD1);
      asmKeywords.add("__FILE__", Token.KEYWORD1);
      asmKeywords.add("__LINE__", Token.KEYWORD1);
      asmKeywords.add("A16", Token.KEYWORD1);
      asmKeywords.add("A32", Token.KEYWORD1);
      asmKeywords.add("ADDR", Token.KEYWORD1);
      asmKeywords.add("ALIGN", Token.KEYWORD1);
      asmKeywords.add("ALIGNB", Token.KEYWORD1);
      asmKeywords.add("ASSUME", Token.KEYWORD1);
      asmKeywords.add("BITS", Token.KEYWORD1);
      asmKeywords.add("CARRY?", Token.KEYWORD1);
      asmKeywords.add("CATSTR", Token.KEYWORD1);
      asmKeywords.add("CODESEG", Token.KEYWORD1);
      asmKeywords.add("COMM", Token.KEYWORD1);
      asmKeywords.add("COMMENT", Token.KEYWORD1);
      asmKeywords.add("COMMON", Token.KEYWORD1);
      asmKeywords.add("DATASEG", Token.KEYWORD1);
      asmKeywords.add("DOSSEG", Token.KEYWORD1);
      asmKeywords.add("ECHO", Token.KEYWORD1);
      asmKeywords.add("ELSE", Token.KEYWORD1);
      asmKeywords.add("ELSEIF", Token.KEYWORD1);
      asmKeywords.add("ELSEIF1", Token.KEYWORD1);
      asmKeywords.add("ELSEIF2", Token.KEYWORD1);
      asmKeywords.add("ELSEIFB", Token.KEYWORD1);
      asmKeywords.add("ELSEIFDEF", Token.KEYWORD1);
      asmKeywords.add("ELSEIFE", Token.KEYWORD1);
      asmKeywords.add("ELSEIFIDN", Token.KEYWORD1);
      asmKeywords.add("ELSEIFNB", Token.KEYWORD1);
      asmKeywords.add("ELSEIFNDEF", Token.KEYWORD1);
      asmKeywords.add("END", Token.KEYWORD1);
      asmKeywords.add("ENDIF", Token.KEYWORD1);
      asmKeywords.add("ENDM", Token.KEYWORD1);
      asmKeywords.add("ENDP", Token.KEYWORD1);
      asmKeywords.add("ENDS", Token.KEYWORD1);
      asmKeywords.add("ENDSTRUC", Token.KEYWORD1);
      asmKeywords.add("EVEN", Token.KEYWORD1);
      asmKeywords.add("EXITM", Token.KEYWORD1);
      asmKeywords.add("EXPORT", Token.KEYWORD1);
      asmKeywords.add("EXTERN", Token.KEYWORD1);
      asmKeywords.add("EXTERNDEF", Token.KEYWORD1);
      asmKeywords.add("EXTRN", Token.KEYWORD1);
      asmKeywords.add("FAR", Token.KEYWORD1);
      asmKeywords.add("FOR", Token.KEYWORD1);
      asmKeywords.add("FORC", Token.KEYWORD1);
      asmKeywords.add("GLOBAL", Token.KEYWORD1);
      asmKeywords.add("GOTO", Token.KEYWORD1);
      asmKeywords.add("GROUP", Token.KEYWORD1);
      asmKeywords.add("HIGH", Token.KEYWORD1);
      asmKeywords.add("HIGHWORD", Token.KEYWORD1);
      asmKeywords.add("IEND", Token.KEYWORD1);
      asmKeywords.add("IF", Token.KEYWORD1);
      asmKeywords.add("IF1", Token.KEYWORD1);
      asmKeywords.add("IF2", Token.KEYWORD1);
      asmKeywords.add("IFB", Token.KEYWORD1);
      asmKeywords.add("IFDEF", Token.KEYWORD1);
      asmKeywords.add("IFDIF", Token.KEYWORD1);
      asmKeywords.add("IFDIFI", Token.KEYWORD1);
      asmKeywords.add("IFE", Token.KEYWORD1);
      asmKeywords.add("IFIDN", Token.KEYWORD1);
      asmKeywords.add("IFIDNI", Token.KEYWORD1);
      asmKeywords.add("IFNB", Token.KEYWORD1);
      asmKeywords.add("IFNDEF", Token.KEYWORD1);
      asmKeywords.add("IMPORT", Token.KEYWORD1);
      asmKeywords.add("INCBIN", Token.KEYWORD1);
      asmKeywords.add("INCLUDE", Token.KEYWORD1);
      asmKeywords.add("INCLUDELIB", Token.KEYWORD1);
      asmKeywords.add("INSTR", Token.KEYWORD1);
      asmKeywords.add("INVOKE", Token.KEYWORD1);
      asmKeywords.add("IRP", Token.KEYWORD1);
      asmKeywords.add("IRPC", Token.KEYWORD1);
      asmKeywords.add("ISTRUC", Token.KEYWORD1);
      asmKeywords.add("LABEL", Token.KEYWORD1);
      asmKeywords.add("LENGTH", Token.KEYWORD1);
      asmKeywords.add("LENGTHOF", Token.KEYWORD1);
      asmKeywords.add("LOCAL", Token.KEYWORD1);
      asmKeywords.add("LOW", Token.KEYWORD1);
      asmKeywords.add("LOWWORD", Token.KEYWORD1);
      asmKeywords.add("LROFFSET", Token.KEYWORD1);
      asmKeywords.add("MACRO", Token.KEYWORD1);
      asmKeywords.add("NAME", Token.KEYWORD1);
      asmKeywords.add("NEAR", Token.KEYWORD1);
      asmKeywords.add("NOSPLIT", Token.KEYWORD1);
      asmKeywords.add("O16", Token.KEYWORD1);
      asmKeywords.add("O32", Token.KEYWORD1);
      asmKeywords.add("OFFSET", Token.KEYWORD1);
      asmKeywords.add("OPATTR", Token.KEYWORD1);
      asmKeywords.add("OPTION", Token.KEYWORD1);
      asmKeywords.add("ORG", Token.KEYWORD1);
      asmKeywords.add("OVERFLOW?", Token.KEYWORD1);
      asmKeywords.add("PAGE", Token.KEYWORD1);
      asmKeywords.add("PARITY?", Token.KEYWORD1);
      asmKeywords.add("POPCONTEXT", Token.KEYWORD1);
      asmKeywords.add("PRIVATE", Token.KEYWORD1);
      asmKeywords.add("PROC", Token.KEYWORD1);
      asmKeywords.add("PROTO", Token.KEYWORD1);
      asmKeywords.add("PTR", Token.KEYWORD1);
      asmKeywords.add("PUBLIC", Token.KEYWORD1);
      asmKeywords.add("PURGE", Token.KEYWORD1);
      asmKeywords.add("PUSHCONTEXT", Token.KEYWORD1);
      asmKeywords.add("RECORD", Token.KEYWORD1);
      asmKeywords.add("REPEAT", Token.KEYWORD1);
      asmKeywords.add("REPT", Token.KEYWORD1);
      asmKeywords.add("SECTION", Token.KEYWORD1);
      asmKeywords.add("SEG", Token.KEYWORD1);
      asmKeywords.add("SEGMENT", Token.KEYWORD1);
      asmKeywords.add("SHORT", Token.KEYWORD1);
      asmKeywords.add("SIGN?", Token.KEYWORD1);
      asmKeywords.add("SIZE", Token.KEYWORD1);
      asmKeywords.add("SIZEOF", Token.KEYWORD1);
      asmKeywords.add("SIZESTR", Token.KEYWORD1);
      asmKeywords.add("STACK", Token.KEYWORD1);
      asmKeywords.add("STRUC", Token.KEYWORD1);
      asmKeywords.add("STRUCT", Token.KEYWORD1);
      asmKeywords.add("SUBSTR", Token.KEYWORD1);
      asmKeywords.add("SUBTITLE", Token.KEYWORD1);
      asmKeywords.add("SUBTTL", Token.KEYWORD1);
      asmKeywords.add("THIS", Token.KEYWORD1);
      asmKeywords.add("TITLE", Token.KEYWORD1);
      asmKeywords.add("TYPE", Token.KEYWORD1);
      asmKeywords.add("TYPEDEF", Token.KEYWORD1);
      asmKeywords.add("UNION", Token.KEYWORD1);
      asmKeywords.add("USE16", Token.KEYWORD1);
      asmKeywords.add("USE32", Token.KEYWORD1);
      asmKeywords.add("USES", Token.KEYWORD1);
      asmKeywords.add("WHILE", Token.KEYWORD1);
      asmKeywords.add("WRT", Token.KEYWORD1);
      asmKeywords.add("ZERO?", Token.KEYWORD1);

      asmKeywords.add("DB", Token.KEYWORD2);
      asmKeywords.add("DW", Token.KEYWORD2);
      asmKeywords.add("DD", Token.KEYWORD2);
      asmKeywords.add("DF", Token.KEYWORD2);
      asmKeywords.add("DQ", Token.KEYWORD2);
      asmKeywords.add("DT", Token.KEYWORD2);
      asmKeywords.add("RESB", Token.KEYWORD2);
      asmKeywords.add("RESW", Token.KEYWORD2);
      asmKeywords.add("RESD", Token.KEYWORD2);
      asmKeywords.add("RESQ", Token.KEYWORD2);
      asmKeywords.add("REST", Token.KEYWORD2);
      asmKeywords.add("EQU", Token.KEYWORD2);
      asmKeywords.add("TEXTEQU", Token.KEYWORD2);
      asmKeywords.add("TIMES", Token.KEYWORD2);
      asmKeywords.add("DUP", Token.KEYWORD2);

      asmKeywords.add("BYTE", Token.KEYWORD2);
      asmKeywords.add("WORD", Token.KEYWORD2);
      asmKeywords.add("DWORD", Token.KEYWORD2);
      asmKeywords.add("FWORD", Token.KEYWORD2);
      asmKeywords.add("QWORD", Token.KEYWORD2);
      asmKeywords.add("TBYTE", Token.KEYWORD2);
      asmKeywords.add("SBYTE", Token.KEYWORD2);
      asmKeywords.add("TWORD", Token.KEYWORD2);
      asmKeywords.add("SWORD", Token.KEYWORD2);
      asmKeywords.add("SDWORD", Token.KEYWORD2);
      asmKeywords.add("REAL4", Token.KEYWORD2);
      asmKeywords.add("REAL8", Token.KEYWORD2);
      asmKeywords.add("REAL10", Token.KEYWORD2);

      asmKeywords.add("AL", Token.KEYWORD3);
      asmKeywords.add("BL", Token.KEYWORD3);
      asmKeywords.add("CL", Token.KEYWORD3);
      asmKeywords.add("DL", Token.KEYWORD3);
      asmKeywords.add("AH", Token.KEYWORD3);
      asmKeywords.add("BH", Token.KEYWORD3);
      asmKeywords.add("CH", Token.KEYWORD3);
      asmKeywords.add("DH", Token.KEYWORD3);
      asmKeywords.add("AX", Token.KEYWORD3);
      asmKeywords.add("BX", Token.KEYWORD3);
      asmKeywords.add("CX", Token.KEYWORD3);
      asmKeywords.add("DX", Token.KEYWORD3);
      asmKeywords.add("SI", Token.KEYWORD3);
      asmKeywords.add("DI", Token.KEYWORD3);
      asmKeywords.add("SP", Token.KEYWORD3);
      asmKeywords.add("BP", Token.KEYWORD3);
      asmKeywords.add("EAX", Token.KEYWORD3);
      asmKeywords.add("EBX", Token.KEYWORD3);
      asmKeywords.add("ECX", Token.KEYWORD3);
      asmKeywords.add("EDX", Token.KEYWORD3);
      asmKeywords.add("ESI", Token.KEYWORD3);
      asmKeywords.add("EDI", Token.KEYWORD3);
      asmKeywords.add("ESP", Token.KEYWORD3);
      asmKeywords.add("EBP", Token.KEYWORD3);
      asmKeywords.add("CS", Token.KEYWORD3);
      asmKeywords.add("DS", Token.KEYWORD3);
      asmKeywords.add("SS", Token.KEYWORD3);
      asmKeywords.add("ES", Token.KEYWORD3);
      asmKeywords.add("FS", Token.KEYWORD3);
      asmKeywords.add("GS", Token.KEYWORD3);
      asmKeywords.add("ST", Token.KEYWORD3);
      asmKeywords.add("ST0", Token.KEYWORD3);
      asmKeywords.add("ST1", Token.KEYWORD3);
      asmKeywords.add("ST2", Token.KEYWORD3);
      asmKeywords.add("ST3", Token.KEYWORD3);
      asmKeywords.add("ST4", Token.KEYWORD3);
      asmKeywords.add("ST5", Token.KEYWORD3);
      asmKeywords.add("ST6", Token.KEYWORD3);
      asmKeywords.add("ST7", Token.KEYWORD3);
      asmKeywords.add("MM0", Token.KEYWORD3);
      asmKeywords.add("MM1", Token.KEYWORD3);
      asmKeywords.add("MM2", Token.KEYWORD3);
      asmKeywords.add("MM3", Token.KEYWORD3);
      asmKeywords.add("MM4", Token.KEYWORD3);
      asmKeywords.add("MM5", Token.KEYWORD3);
      asmKeywords.add("MM6", Token.KEYWORD3);
      asmKeywords.add("MM7", Token.KEYWORD3);
      asmKeywords.add("XMM0", Token.KEYWORD3);
      asmKeywords.add("XMM1", Token.KEYWORD3);
      asmKeywords.add("XMM2", Token.KEYWORD3);
      asmKeywords.add("XMM3", Token.KEYWORD3);
      asmKeywords.add("XMM4", Token.KEYWORD3);
      asmKeywords.add("XMM5", Token.KEYWORD3);
      asmKeywords.add("XMM6", Token.KEYWORD3);
      asmKeywords.add("XMM7", Token.KEYWORD3);
      asmKeywords.add("CR0", Token.KEYWORD3);
      asmKeywords.add("CR2", Token.KEYWORD3);
      asmKeywords.add("CR3", Token.KEYWORD3);
      asmKeywords.add("CR4", Token.KEYWORD3);
      asmKeywords.add("DR0", Token.KEYWORD3);
      asmKeywords.add("DR1", Token.KEYWORD3);
      asmKeywords.add("DR2", Token.KEYWORD3);
      asmKeywords.add("DR3", Token.KEYWORD3);
      asmKeywords.add("DR4", Token.KEYWORD3);
      asmKeywords.add("DR5", Token.KEYWORD3);
      asmKeywords.add("DR6", Token.KEYWORD3);
      asmKeywords.add("DR7", Token.KEYWORD3);
      asmKeywords.add("TR3", Token.KEYWORD3);
      asmKeywords.add("TR4", Token.KEYWORD3);
      asmKeywords.add("TR5", Token.KEYWORD3);
      asmKeywords.add("TR6", Token.KEYWORD3);
      asmKeywords.add("TR7", Token.KEYWORD3);

      asmKeywords.add("AAA", Token.LITERAL2);
      asmKeywords.add("AAD", Token.LITERAL2);
      asmKeywords.add("AAM", Token.LITERAL2);
      asmKeywords.add("AAS", Token.LITERAL2);
      asmKeywords.add("ADC", Token.LITERAL2);
      asmKeywords.add("ADD", Token.LITERAL2);
      asmKeywords.add("ADDPS", Token.LITERAL2);
      asmKeywords.add("ADDSS", Token.LITERAL2);
      asmKeywords.add("AND", Token.LITERAL2);
      asmKeywords.add("ANDNPS", Token.LITERAL2);
      asmKeywords.add("ANDPS", Token.LITERAL2);
      asmKeywords.add("ARPL", Token.LITERAL2);
      asmKeywords.add("BOUND", Token.LITERAL2);
      asmKeywords.add("BSF", Token.LITERAL2);
      asmKeywords.add("BSR", Token.LITERAL2);
      asmKeywords.add("BSWAP", Token.LITERAL2);
      asmKeywords.add("BT", Token.LITERAL2);
      asmKeywords.add("BTC", Token.LITERAL2);
      asmKeywords.add("BTR", Token.LITERAL2);
      asmKeywords.add("BTS", Token.LITERAL2);
      asmKeywords.add("CALL", Token.LITERAL2);
      asmKeywords.add("CBW", Token.LITERAL2);
      asmKeywords.add("CDQ", Token.LITERAL2);
      asmKeywords.add("CLC", Token.LITERAL2);
      asmKeywords.add("CLD", Token.LITERAL2);
      asmKeywords.add("CLI", Token.LITERAL2);
      asmKeywords.add("CLTS", Token.LITERAL2);
      asmKeywords.add("CMC", Token.LITERAL2);
      asmKeywords.add("CMOVA", Token.LITERAL2);
      asmKeywords.add("CMOVAE", Token.LITERAL2);
      asmKeywords.add("CMOVB", Token.LITERAL2);
      asmKeywords.add("CMOVBE", Token.LITERAL2);
      asmKeywords.add("CMOVC", Token.LITERAL2);
      asmKeywords.add("CMOVE", Token.LITERAL2);
      asmKeywords.add("CMOVG", Token.LITERAL2);
      asmKeywords.add("CMOVGE", Token.LITERAL2);
      asmKeywords.add("CMOVL", Token.LITERAL2);
      asmKeywords.add("CMOVLE", Token.LITERAL2);
      asmKeywords.add("CMOVNA", Token.LITERAL2);
      asmKeywords.add("CMOVNAE", Token.LITERAL2);
      asmKeywords.add("CMOVNB", Token.LITERAL2);
      asmKeywords.add("CMOVNBE", Token.LITERAL2);
      asmKeywords.add("CMOVNC", Token.LITERAL2);
      asmKeywords.add("CMOVNE", Token.LITERAL2);
      asmKeywords.add("CMOVNG", Token.LITERAL2);
      asmKeywords.add("CMOVNGE", Token.LITERAL2);
      asmKeywords.add("CMOVNL", Token.LITERAL2);
      asmKeywords.add("CMOVNLE", Token.LITERAL2);
      asmKeywords.add("CMOVNO", Token.LITERAL2);
      asmKeywords.add("CMOVNP", Token.LITERAL2);
      asmKeywords.add("CMOVNS", Token.LITERAL2);
      asmKeywords.add("CMOVNZ", Token.LITERAL2);
      asmKeywords.add("CMOVO", Token.LITERAL2);
      asmKeywords.add("CMOVP", Token.LITERAL2);
      asmKeywords.add("CMOVPE", Token.LITERAL2);
      asmKeywords.add("CMOVPO", Token.LITERAL2);
      asmKeywords.add("CMOVS", Token.LITERAL2);
      asmKeywords.add("CMOVZ", Token.LITERAL2);
      asmKeywords.add("CMP", Token.LITERAL2);
      asmKeywords.add("CMPPS", Token.LITERAL2);
      asmKeywords.add("CMPS", Token.LITERAL2);
      asmKeywords.add("CMPSB", Token.LITERAL2);
      asmKeywords.add("CMPSD", Token.LITERAL2);
      asmKeywords.add("CMPSS", Token.LITERAL2);
      asmKeywords.add("CMPSW", Token.LITERAL2);
      asmKeywords.add("CMPXCHG", Token.LITERAL2);
      asmKeywords.add("CMPXCHGB", Token.LITERAL2);
      asmKeywords.add("COMISS", Token.LITERAL2);
      asmKeywords.add("CPUID", Token.LITERAL2);
      asmKeywords.add("CWD", Token.LITERAL2);
      asmKeywords.add("CWDE", Token.LITERAL2);
      asmKeywords.add("CVTPI2PS", Token.LITERAL2);
      asmKeywords.add("CVTPS2PI", Token.LITERAL2);
      asmKeywords.add("CVTSI2SS", Token.LITERAL2);
      asmKeywords.add("CVTSS2SI", Token.LITERAL2);
      asmKeywords.add("CVTTPS2PI", Token.LITERAL2);
      asmKeywords.add("CVTTSS2SI", Token.LITERAL2);
      asmKeywords.add("DAA", Token.LITERAL2);
      asmKeywords.add("DAS", Token.LITERAL2);
      asmKeywords.add("DEC", Token.LITERAL2);
      asmKeywords.add("DIV", Token.LITERAL2);
      asmKeywords.add("DIVPS", Token.LITERAL2);
      asmKeywords.add("DIVSS", Token.LITERAL2);
      asmKeywords.add("EMMS", Token.LITERAL2);
      asmKeywords.add("ENTER", Token.LITERAL2);
      asmKeywords.add("F2XM1", Token.LITERAL2);
      asmKeywords.add("FABS", Token.LITERAL2);
      asmKeywords.add("FADD", Token.LITERAL2);
      asmKeywords.add("FADDP", Token.LITERAL2);
      asmKeywords.add("FBLD", Token.LITERAL2);
      asmKeywords.add("FBSTP", Token.LITERAL2);
      asmKeywords.add("FCHS", Token.LITERAL2);
      asmKeywords.add("FCLEX", Token.LITERAL2);
      asmKeywords.add("FCMOVB", Token.LITERAL2);
      asmKeywords.add("FCMOVBE", Token.LITERAL2);
      asmKeywords.add("FCMOVE", Token.LITERAL2);
      asmKeywords.add("FCMOVNB", Token.LITERAL2);
      asmKeywords.add("FCMOVNBE", Token.LITERAL2);
      asmKeywords.add("FCMOVNE", Token.LITERAL2);
      asmKeywords.add("FCMOVNU", Token.LITERAL2);
      asmKeywords.add("FCMOVU", Token.LITERAL2);
      asmKeywords.add("FCOM", Token.LITERAL2);
      asmKeywords.add("FCOMI", Token.LITERAL2);
      asmKeywords.add("FCOMIP", Token.LITERAL2);
      asmKeywords.add("FCOMP", Token.LITERAL2);
      asmKeywords.add("FCOMPP", Token.LITERAL2);
      asmKeywords.add("FCOS", Token.LITERAL2);
      asmKeywords.add("FDECSTP", Token.LITERAL2);
      asmKeywords.add("FDIV", Token.LITERAL2);
      asmKeywords.add("FDIVP", Token.LITERAL2);
      asmKeywords.add("FDIVR", Token.LITERAL2);
      asmKeywords.add("FDIVRP", Token.LITERAL2);
      asmKeywords.add("FFREE", Token.LITERAL2);
      asmKeywords.add("FIADD", Token.LITERAL2);
      asmKeywords.add("FICOM", Token.LITERAL2);
      asmKeywords.add("FICOMP", Token.LITERAL2);
      asmKeywords.add("FIDIV", Token.LITERAL2);
      asmKeywords.add("FIDIVR", Token.LITERAL2);
      asmKeywords.add("FILD", Token.LITERAL2);
      asmKeywords.add("FIMUL", Token.LITERAL2);
      asmKeywords.add("FINCSTP", Token.LITERAL2);
      asmKeywords.add("FINIT", Token.LITERAL2);
      asmKeywords.add("FIST", Token.LITERAL2);
      asmKeywords.add("FISTP", Token.LITERAL2);
      asmKeywords.add("FISUB", Token.LITERAL2);
      asmKeywords.add("FISUBR", Token.LITERAL2);
      asmKeywords.add("FLD1", Token.LITERAL2);
      asmKeywords.add("FLDCW", Token.LITERAL2);
      asmKeywords.add("FLDENV", Token.LITERAL2);
      asmKeywords.add("FLDL2E", Token.LITERAL2);
      asmKeywords.add("FLDL2T", Token.LITERAL2);
      asmKeywords.add("FLDLG2", Token.LITERAL2);
      asmKeywords.add("FLDLN2", Token.LITERAL2);
      asmKeywords.add("FLDPI", Token.LITERAL2);
      asmKeywords.add("FLDZ", Token.LITERAL2);
      asmKeywords.add("FMUL", Token.LITERAL2);
      asmKeywords.add("FMULP", Token.LITERAL2);
      asmKeywords.add("FNCLEX", Token.LITERAL2);
      asmKeywords.add("FNINIT", Token.LITERAL2);
      asmKeywords.add("FNOP", Token.LITERAL2);
      asmKeywords.add("FNSAVE", Token.LITERAL2);
      asmKeywords.add("FNSTCW", Token.LITERAL2);
      asmKeywords.add("FNSTENV", Token.LITERAL2);
      asmKeywords.add("FNSTSW", Token.LITERAL2);
      asmKeywords.add("FPATAN", Token.LITERAL2);
      asmKeywords.add("FPREM", Token.LITERAL2);
      asmKeywords.add("FPREMI", Token.LITERAL2);
      asmKeywords.add("FPTAN", Token.LITERAL2);
      asmKeywords.add("FRNDINT", Token.LITERAL2);
      asmKeywords.add("FRSTOR", Token.LITERAL2);
      asmKeywords.add("FSAVE", Token.LITERAL2);
      asmKeywords.add("FSCALE", Token.LITERAL2);
      asmKeywords.add("FSIN", Token.LITERAL2);
      asmKeywords.add("FSINCOS", Token.LITERAL2);
      asmKeywords.add("FSQRT", Token.LITERAL2);
      asmKeywords.add("FST", Token.LITERAL2);
      asmKeywords.add("FSTCW", Token.LITERAL2);
      asmKeywords.add("FSTENV", Token.LITERAL2);
      asmKeywords.add("FSTP", Token.LITERAL2);
      asmKeywords.add("FSTSW", Token.LITERAL2);
      asmKeywords.add("FSUB", Token.LITERAL2);
      asmKeywords.add("FSUBP", Token.LITERAL2);
      asmKeywords.add("FSUBR", Token.LITERAL2);
      asmKeywords.add("FSUBRP", Token.LITERAL2);
      asmKeywords.add("FTST", Token.LITERAL2);
      asmKeywords.add("FUCOM", Token.LITERAL2);
      asmKeywords.add("FUCOMI", Token.LITERAL2);
      asmKeywords.add("FUCOMIP", Token.LITERAL2);
      asmKeywords.add("FUCOMP", Token.LITERAL2);
      asmKeywords.add("FUCOMPP", Token.LITERAL2);
      asmKeywords.add("FWAIT", Token.LITERAL2);
      asmKeywords.add("FXAM", Token.LITERAL2);
      asmKeywords.add("FXCH", Token.LITERAL2);
      asmKeywords.add("FXRSTOR", Token.LITERAL2);
      asmKeywords.add("FXSAVE", Token.LITERAL2);
      asmKeywords.add("FXTRACT", Token.LITERAL2);
      asmKeywords.add("FYL2X", Token.LITERAL2);
      asmKeywords.add("FYL2XP1", Token.LITERAL2);
      asmKeywords.add("HLT", Token.LITERAL2);
      asmKeywords.add("IDIV", Token.LITERAL2);
      asmKeywords.add("IMUL", Token.LITERAL2);
      asmKeywords.add("IN", Token.LITERAL2);
      asmKeywords.add("INC", Token.LITERAL2);
      asmKeywords.add("INS", Token.LITERAL2);
      asmKeywords.add("INSB", Token.LITERAL2);
      asmKeywords.add("INSD", Token.LITERAL2);
      asmKeywords.add("INSW", Token.LITERAL2);
      asmKeywords.add("INT", Token.LITERAL2);
      asmKeywords.add("INTO", Token.LITERAL2);
      asmKeywords.add("INVD", Token.LITERAL2);
      asmKeywords.add("INVLPG", Token.LITERAL2);
      asmKeywords.add("IRET", Token.LITERAL2);
      asmKeywords.add("JA", Token.LITERAL2);
      asmKeywords.add("JAE", Token.LITERAL2);
      asmKeywords.add("JB", Token.LITERAL2);
      asmKeywords.add("JBE", Token.LITERAL2);
      asmKeywords.add("JC", Token.LITERAL2);
      asmKeywords.add("JCXZ", Token.LITERAL2);
      asmKeywords.add("JE", Token.LITERAL2);
      asmKeywords.add("JECXZ", Token.LITERAL2);
      asmKeywords.add("JG", Token.LITERAL2);
      asmKeywords.add("JGE", Token.LITERAL2);
      asmKeywords.add("JL", Token.LITERAL2);
      asmKeywords.add("JLE", Token.LITERAL2);
      asmKeywords.add("JMP", Token.LITERAL2);
      asmKeywords.add("JNA", Token.LITERAL2);
      asmKeywords.add("JNAE", Token.LITERAL2);
      asmKeywords.add("JNB", Token.LITERAL2);
      asmKeywords.add("JNBE", Token.LITERAL2);
      asmKeywords.add("JNC", Token.LITERAL2);
      asmKeywords.add("JNE", Token.LITERAL2);
      asmKeywords.add("JNG", Token.LITERAL2);
      asmKeywords.add("JNGE", Token.LITERAL2);
      asmKeywords.add("JNL", Token.LITERAL2);
      asmKeywords.add("JNLE", Token.LITERAL2);
      asmKeywords.add("JNO", Token.LITERAL2);
      asmKeywords.add("JNP", Token.LITERAL2);
      asmKeywords.add("JNS", Token.LITERAL2);
      asmKeywords.add("JNZ", Token.LITERAL2);
      asmKeywords.add("JO", Token.LITERAL2);
      asmKeywords.add("JP", Token.LITERAL2);
      asmKeywords.add("JPE", Token.LITERAL2);
      asmKeywords.add("JPO", Token.LITERAL2);
      asmKeywords.add("JS", Token.LITERAL2);
      asmKeywords.add("JZ", Token.LITERAL2);
      asmKeywords.add("LAHF", Token.LITERAL2);
      asmKeywords.add("LAR", Token.LITERAL2);
      asmKeywords.add("LDMXCSR", Token.LITERAL2);
      asmKeywords.add("LDS", Token.LITERAL2);
      asmKeywords.add("LEA", Token.LITERAL2);
      asmKeywords.add("LEAVE", Token.LITERAL2);
      asmKeywords.add("LES", Token.LITERAL2);
      asmKeywords.add("LFS", Token.LITERAL2);
      asmKeywords.add("LGDT", Token.LITERAL2);
      asmKeywords.add("LGS", Token.LITERAL2);
      asmKeywords.add("LIDT", Token.LITERAL2);
      asmKeywords.add("LLDT", Token.LITERAL2);
      asmKeywords.add("LMSW", Token.LITERAL2);
      asmKeywords.add("LOCK", Token.LITERAL2);
      asmKeywords.add("LODS", Token.LITERAL2);
      asmKeywords.add("LODSB", Token.LITERAL2);
      asmKeywords.add("LODSD", Token.LITERAL2);
      asmKeywords.add("LODSW", Token.LITERAL2);
      asmKeywords.add("LOOP", Token.LITERAL2);
      asmKeywords.add("LOOPE", Token.LITERAL2);
      asmKeywords.add("LOOPNE", Token.LITERAL2);
      asmKeywords.add("LOOPNZ", Token.LITERAL2);
      asmKeywords.add("LOOPZ", Token.LITERAL2);
      asmKeywords.add("LSL", Token.LITERAL2);
      asmKeywords.add("LSS", Token.LITERAL2);
      asmKeywords.add("LTR", Token.LITERAL2);
      asmKeywords.add("MASKMOVQ", Token.LITERAL2);
      asmKeywords.add("MAXPS", Token.LITERAL2);
      asmKeywords.add("MAXSS", Token.LITERAL2);
      asmKeywords.add("MINPS", Token.LITERAL2);
      asmKeywords.add("MINSS", Token.LITERAL2);
      asmKeywords.add("MOV", Token.LITERAL2);
      asmKeywords.add("MOVAPS", Token.LITERAL2);
      asmKeywords.add("MOVD", Token.LITERAL2);
      asmKeywords.add("MOVHLPS", Token.LITERAL2);
      asmKeywords.add("MOVHPS", Token.LITERAL2);
      asmKeywords.add("MOVLHPS", Token.LITERAL2);
      asmKeywords.add("MOVLPS", Token.LITERAL2);
      asmKeywords.add("MOVMSKPS", Token.LITERAL2);
      asmKeywords.add("MOVNTPS", Token.LITERAL2);
      asmKeywords.add("MOVNTQ", Token.LITERAL2);
      asmKeywords.add("MOVQ", Token.LITERAL2);
      asmKeywords.add("MOVS", Token.LITERAL2);
      asmKeywords.add("MOVSB", Token.LITERAL2);
      asmKeywords.add("MOVSD", Token.LITERAL2);
      asmKeywords.add("MOVSS", Token.LITERAL2);
      asmKeywords.add("MOVSW", Token.LITERAL2);
      asmKeywords.add("MOVSX", Token.LITERAL2);
      asmKeywords.add("MOVUPS", Token.LITERAL2);
      asmKeywords.add("MOVZX", Token.LITERAL2);
      asmKeywords.add("MUL", Token.LITERAL2);
      asmKeywords.add("MULPS", Token.LITERAL2);
      asmKeywords.add("MULSS", Token.LITERAL2);
      asmKeywords.add("NEG", Token.LITERAL2);
      asmKeywords.add("NOP", Token.LITERAL2);
      asmKeywords.add("NOT", Token.LITERAL2);
      asmKeywords.add("OR", Token.LITERAL2);
      asmKeywords.add("ORPS", Token.LITERAL2);
      asmKeywords.add("OUT", Token.LITERAL2);
      asmKeywords.add("OUTS", Token.LITERAL2);
      asmKeywords.add("OUTSB", Token.LITERAL2);
      asmKeywords.add("OUTSD", Token.LITERAL2);
      asmKeywords.add("OUTSW", Token.LITERAL2);
      asmKeywords.add("PACKSSDW", Token.LITERAL2);
      asmKeywords.add("PACKSSWB", Token.LITERAL2);
      asmKeywords.add("PACKUSWB", Token.LITERAL2);
      asmKeywords.add("PADDB", Token.LITERAL2);
      asmKeywords.add("PADDD", Token.LITERAL2);
      asmKeywords.add("PADDSB", Token.LITERAL2);
      asmKeywords.add("PADDSW", Token.LITERAL2);
      asmKeywords.add("PADDUSB", Token.LITERAL2);
      asmKeywords.add("PADDUSW", Token.LITERAL2);
      asmKeywords.add("PADDW", Token.LITERAL2);
      asmKeywords.add("PAND", Token.LITERAL2);
      asmKeywords.add("PANDN", Token.LITERAL2);
      asmKeywords.add("PAVGB", Token.LITERAL2);
      asmKeywords.add("PAVGW", Token.LITERAL2);
      asmKeywords.add("PCMPEQB", Token.LITERAL2);
      asmKeywords.add("PCMPEQD", Token.LITERAL2);
      asmKeywords.add("PCMPEQW", Token.LITERAL2);
      asmKeywords.add("PCMPGTB", Token.LITERAL2);
      asmKeywords.add("PCMPGTD", Token.LITERAL2);
      asmKeywords.add("PCMPGTW", Token.LITERAL2);
      asmKeywords.add("PEXTRW", Token.LITERAL2);
      asmKeywords.add("PINSRW", Token.LITERAL2);
      asmKeywords.add("PMADDWD", Token.LITERAL2);
      asmKeywords.add("PMAXSW", Token.LITERAL2);
      asmKeywords.add("PMAXUB", Token.LITERAL2);
      asmKeywords.add("PMINSW", Token.LITERAL2);
      asmKeywords.add("PMINUB", Token.LITERAL2);
      asmKeywords.add("PMOVMSKB", Token.LITERAL2);
      asmKeywords.add("PMULHUW", Token.LITERAL2);
      asmKeywords.add("PMULHW", Token.LITERAL2);
      asmKeywords.add("PMULLW", Token.LITERAL2);
      asmKeywords.add("POP", Token.LITERAL2);
      asmKeywords.add("POPA", Token.LITERAL2);
      asmKeywords.add("POPAD", Token.LITERAL2);
      asmKeywords.add("POPAW", Token.LITERAL2);
      asmKeywords.add("POPF", Token.LITERAL2);
      asmKeywords.add("POPFD", Token.LITERAL2);
      asmKeywords.add("POPFW", Token.LITERAL2);
      asmKeywords.add("POR", Token.LITERAL2);
      asmKeywords.add("PREFETCH", Token.LITERAL2);
      asmKeywords.add("PSADBW", Token.LITERAL2);
      asmKeywords.add("PSHUFW", Token.LITERAL2);
      asmKeywords.add("PSLLD", Token.LITERAL2);
      asmKeywords.add("PSLLQ", Token.LITERAL2);
      asmKeywords.add("PSLLW", Token.LITERAL2);
      asmKeywords.add("PSRAD", Token.LITERAL2);
      asmKeywords.add("PSRAW", Token.LITERAL2);
      asmKeywords.add("PSRLD", Token.LITERAL2);
      asmKeywords.add("PSRLQ", Token.LITERAL2);
      asmKeywords.add("PSRLW", Token.LITERAL2);
      asmKeywords.add("PSUBB", Token.LITERAL2);
      asmKeywords.add("PSUBD", Token.LITERAL2);
      asmKeywords.add("PSUBSB", Token.LITERAL2);
      asmKeywords.add("PSUBSW", Token.LITERAL2);
      asmKeywords.add("PSUBUSB", Token.LITERAL2);
      asmKeywords.add("PSUBUSW", Token.LITERAL2);
      asmKeywords.add("PSUBW", Token.LITERAL2);
      asmKeywords.add("PUNPCKHBW", Token.LITERAL2);
      asmKeywords.add("PUNPCKHDQ", Token.LITERAL2);
      asmKeywords.add("PUNPCKHWD", Token.LITERAL2);
      asmKeywords.add("PUNPCKLBW", Token.LITERAL2);
      asmKeywords.add("PUNPCKLDQ", Token.LITERAL2);
      asmKeywords.add("PUNPCKLWD", Token.LITERAL2);
      asmKeywords.add("PUSH", Token.LITERAL2);
      asmKeywords.add("PUSHA", Token.LITERAL2);
      asmKeywords.add("PUSHAD", Token.LITERAL2);
      asmKeywords.add("PUSHAW", Token.LITERAL2);
      asmKeywords.add("PUSHF", Token.LITERAL2);
      asmKeywords.add("PUSHFD", Token.LITERAL2);
      asmKeywords.add("PUSHFW", Token.LITERAL2);
      asmKeywords.add("PXOR", Token.LITERAL2);
      asmKeywords.add("RCL", Token.LITERAL2);
      asmKeywords.add("RCR", Token.LITERAL2);
      asmKeywords.add("RDMSR", Token.LITERAL2);
      asmKeywords.add("RDPMC", Token.LITERAL2);
      asmKeywords.add("RDTSC", Token.LITERAL2);
      asmKeywords.add("REP", Token.LITERAL2);
      asmKeywords.add("REPE", Token.LITERAL2);
      asmKeywords.add("REPNE", Token.LITERAL2);
      asmKeywords.add("REPNZ", Token.LITERAL2);
      asmKeywords.add("REPZ", Token.LITERAL2);
      asmKeywords.add("RET", Token.LITERAL2);
      asmKeywords.add("RETF", Token.LITERAL2);
      asmKeywords.add("RETN", Token.LITERAL2);
      asmKeywords.add("ROL", Token.LITERAL2);
      asmKeywords.add("ROR", Token.LITERAL2);
      asmKeywords.add("RSM", Token.LITERAL2);
      asmKeywords.add("SAHF", Token.LITERAL2);
      asmKeywords.add("SAL", Token.LITERAL2);
      asmKeywords.add("SAR", Token.LITERAL2);
      asmKeywords.add("SBB", Token.LITERAL2);
      asmKeywords.add("SCAS", Token.LITERAL2);
      asmKeywords.add("SCASB", Token.LITERAL2);
      asmKeywords.add("SCASD", Token.LITERAL2);
      asmKeywords.add("SCASW", Token.LITERAL2);
      asmKeywords.add("SETA", Token.LITERAL2);
      asmKeywords.add("SETAE", Token.LITERAL2);
      asmKeywords.add("SETB", Token.LITERAL2);
      asmKeywords.add("SETBE", Token.LITERAL2);
      asmKeywords.add("SETC", Token.LITERAL2);
      asmKeywords.add("SETE", Token.LITERAL2);
      asmKeywords.add("SETG", Token.LITERAL2);
      asmKeywords.add("SETGE", Token.LITERAL2);
      asmKeywords.add("SETL", Token.LITERAL2);
      asmKeywords.add("SETLE", Token.LITERAL2);
      asmKeywords.add("SETNA", Token.LITERAL2);
      asmKeywords.add("SETNAE", Token.LITERAL2);
      asmKeywords.add("SETNB", Token.LITERAL2);
      asmKeywords.add("SETNBE", Token.LITERAL2);
      asmKeywords.add("SETNC", Token.LITERAL2);
      asmKeywords.add("SETNE", Token.LITERAL2);
      asmKeywords.add("SETNG", Token.LITERAL2);
      asmKeywords.add("SETNGE", Token.LITERAL2);
      asmKeywords.add("SETNL", Token.LITERAL2);
      asmKeywords.add("SETNLE", Token.LITERAL2);
      asmKeywords.add("SETNO", Token.LITERAL2);
      asmKeywords.add("SETNP", Token.LITERAL2);
      asmKeywords.add("SETNS", Token.LITERAL2);
      asmKeywords.add("SETNZ", Token.LITERAL2);
      asmKeywords.add("SETO", Token.LITERAL2);
      asmKeywords.add("SETP", Token.LITERAL2);
      asmKeywords.add("SETPE", Token.LITERAL2);
      asmKeywords.add("SETPO", Token.LITERAL2);
      asmKeywords.add("SETS", Token.LITERAL2);
      asmKeywords.add("SETZ", Token.LITERAL2);
      asmKeywords.add("SFENCE", Token.LITERAL2);
      asmKeywords.add("SGDT", Token.LITERAL2);
      asmKeywords.add("SHL", Token.LITERAL2);
      asmKeywords.add("SHLD", Token.LITERAL2);
      asmKeywords.add("SHR", Token.LITERAL2);
      asmKeywords.add("SHRD", Token.LITERAL2);
      asmKeywords.add("SHUFPS", Token.LITERAL2);
      asmKeywords.add("SIDT", Token.LITERAL2);
      asmKeywords.add("SLDT", Token.LITERAL2);
      asmKeywords.add("SMSW", Token.LITERAL2);
      asmKeywords.add("SQRTPS", Token.LITERAL2);
      asmKeywords.add("SQRTSS", Token.LITERAL2);
      asmKeywords.add("STC", Token.LITERAL2);
      asmKeywords.add("STD", Token.LITERAL2);
      asmKeywords.add("STI", Token.LITERAL2);
      asmKeywords.add("STMXCSR", Token.LITERAL2);
      asmKeywords.add("STOS", Token.LITERAL2);
      asmKeywords.add("STOSB", Token.LITERAL2);
      asmKeywords.add("STOSD", Token.LITERAL2);
      asmKeywords.add("STOSW", Token.LITERAL2);
      asmKeywords.add("STR", Token.LITERAL2);
      asmKeywords.add("SUB", Token.LITERAL2);
      asmKeywords.add("SUBPS", Token.LITERAL2);
      asmKeywords.add("SUBSS", Token.LITERAL2);
      asmKeywords.add("SYSENTER", Token.LITERAL2);
      asmKeywords.add("SYSEXIT", Token.LITERAL2);
      asmKeywords.add("TEST", Token.LITERAL2);
      asmKeywords.add("UB2", Token.LITERAL2);
      asmKeywords.add("UCOMISS", Token.LITERAL2);
      asmKeywords.add("UNPCKHPS", Token.LITERAL2);
      asmKeywords.add("UNPCKLPS", Token.LITERAL2);
      asmKeywords.add("WAIT", Token.LITERAL2);
      asmKeywords.add("WBINVD", Token.LITERAL2);
      asmKeywords.add("VERR", Token.LITERAL2);
      asmKeywords.add("VERW", Token.LITERAL2);
      asmKeywords.add("WRMSR", Token.LITERAL2);
      asmKeywords.add("XADD", Token.LITERAL2);
      asmKeywords.add("XCHG", Token.LITERAL2);
      asmKeywords.add("XLAT", Token.LITERAL2);
      asmKeywords.add("XLATB", Token.LITERAL2);
      asmKeywords.add("XOR", Token.LITERAL2);
      asmKeywords.add("XORPS", Token.LITERAL2);

      asmKeywords.add("FEMMS", Token.LITERAL2);
      asmKeywords.add("PAVGUSB", Token.LITERAL2);
      asmKeywords.add("PF2ID", Token.LITERAL2);
      asmKeywords.add("PFACC", Token.LITERAL2);
      asmKeywords.add("PFADD", Token.LITERAL2);
      asmKeywords.add("PFCMPEQ", Token.LITERAL2);
      asmKeywords.add("PFCMPGE", Token.LITERAL2);
      asmKeywords.add("PFCMPGT", Token.LITERAL2);
      asmKeywords.add("PFMAX", Token.LITERAL2);
      asmKeywords.add("PFMIN", Token.LITERAL2);
      asmKeywords.add("PFMUL", Token.LITERAL2);
      asmKeywords.add("PFRCP", Token.LITERAL2);
      asmKeywords.add("PFRCPIT1", Token.LITERAL2);
      asmKeywords.add("PFRCPIT2", Token.LITERAL2);
      asmKeywords.add("PFRSQIT1", Token.LITERAL2);
      asmKeywords.add("PFRSQRT", Token.LITERAL2);
      asmKeywords.add("PFSUB", Token.LITERAL2);
      asmKeywords.add("PFSUBR", Token.LITERAL2);
      asmKeywords.add("PI2FD", Token.LITERAL2);
      asmKeywords.add("PMULHRW", Token.LITERAL2);
      asmKeywords.add("PREFETCHW", Token.LITERAL2);

      asmKeywords.add("PF2IW", Token.LITERAL2);
      asmKeywords.add("PFNACC", Token.LITERAL2);
      asmKeywords.add("PFPNACC", Token.LITERAL2);
      asmKeywords.add("PI2FW", Token.LITERAL2);
      asmKeywords.add("PSWAPD", Token.LITERAL2);

      asmKeywords.add("PREFETCHNTA", Token.LITERAL2);
      asmKeywords.add("PREFETCHT0", Token.LITERAL2);
      asmKeywords.add("PREFETCHT1", Token.LITERAL2);
      asmKeywords.add("PREFETCHT2", Token.LITERAL2);
    }

    return asmKeywords;
  }

  private boolean doKeyword(Segment line, int i, char c)
  {
    int i1 = i + 1;

    int len = i - lastKeyword;
    byte id = keywords.lookup(line, lastKeyword, len);
    if (id != Token.NULL)
    {
      if (lastKeyword != lastOffset)
        addToken(lastKeyword - lastOffset, Token.NULL);
      addToken(len, id);
      lastOffset = i;
    }
    lastKeyword = i1;
    return false;
  }
}

// End of ASMTokenMarker.java
