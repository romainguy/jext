<?xml version="1.0"?>

<!--
  -
  - Jext documentation style-sheet
  - Based on xtiny-doc DTD
  -
  - DTD:xtiny-doc and XSL:xtiny-doc
  - are (C)1999 Romain Guy
  -
  - Last updated: 12/07/2000 - 22:10:24
  -
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no" />
  <xsl:preserve-space elements="code" />

   <xsl:template match="/">
     <xsl:apply-templates />
   </xsl:template>

   <xsl:template match="xtiny-doc-page">
      <html>
        <head>
          <meta name="generator" value="Ga&#239;a v1.4 - Romain Guy" />
          <title><xsl:value-of select="@title" /></title>
          <link rel ="stylesheet" type="text/css" href="../stylesheet.css" title="Style" />
        </head>
        <body>
          <div align="center">
            <font size="+4" color="#FF0000" face="Verdana, Arial, Helvetica, sans-serif">
              <xsl:value-of select="@name" /><br />
            </font>
            <table width="92%" class="main" align="center">
              <xsl:apply-templates />
              <tr>
                <td align="center">
                  <br />
                  <font color="#AAACBE">[&#160;
                  <xsl:choose>
                    <xsl:when test="@prev > ''">
                      <a><xsl:attribute name="href"><xsl:value-of select="@prev" /></xsl:attribute>
                      Pr&#233;c&#233;dent</a>
                    </xsl:when>
                    <xsl:otherwise>
                      Pr&#233;c&#233;dent
                    </xsl:otherwise>
                  </xsl:choose>
                  &#160;|&#160;
                  <a><xsl:attribute name="href">index.html</xsl:attribute>
                  Sommaire</a>
                  &#160;|&#160;
                  <xsl:choose>
                    <xsl:when test="@next > ''">
                      <a><xsl:attribute name="href"><xsl:value-of select="@next" /></xsl:attribute>
                      Suivant</a>
                    </xsl:when>
                    <xsl:otherwise>
                      Suivant
                    </xsl:otherwise>
                  </xsl:choose>
                  &#160;]</font>
                  <br />
                </td>
              </tr>
              <tr>
                <td align="center">
                  <font size="-3">
                  <br /><br />
                    par&#160;<xsl:value-of select="@author" /><br />
                    Derni&#232;re mise &#224; jour:&#160;<xsl:value-of select="@update" /><br />
                    G&#233;n&#233;r&#233; par Ga&#239;a v1.4
                  </font>
                </td>
              </tr>
            </table>
          </div>
        </body>
      </html>
   </xsl:template>

   <xsl:template match="menu">
     <xsl:choose>
       <xsl:when test="@title > ''">
         <tr>
          <td><br /><br /></td>
         </tr>
         <tr>
           <td class="title">
             <font face="Verdana, Arial, sans-serif" size="-2">
               &#160;&gt;&gt;<xsl:value-of select="@title" />
             </font>
           </td>
         </tr>
         <tr>
           <td>
             <br />
             <ul>
               <xsl:apply-templates />
             </ul>
           </td>
         </tr>
       </xsl:when>
       <xsl:otherwise>
         <tr>
           <td>
             <br />
             <ul>
               <xsl:apply-templates />
             </ul>
           </td>
         </tr>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:template>

   <xsl:template match="submenu">
     <li>
       <b><font color="red"><xsl:value-of select="@title" />:</font></b>&#160;
       <ul>
         <xsl:apply-templates />
       </ul>
     </li>
   </xsl:template>

   <xsl:template match="item">
     <li>
       <b><font color="green"><xsl:value-of select="@name" />:</font></b>&#160;
       <xsl:apply-templates />
     </li>
   </xsl:template>

   <xsl:template match="break">
     <tr>
       <td>
         <hr />
       </td>
     </tr>
   </xsl:template>

   <xsl:template match="code">
     <br /><code><xsl:apply-templates /></code><br />
   </xsl:template>

   <xsl:template match="b">
     <b><xsl:apply-templates /></b>
   </xsl:template>

   <xsl:template match="i">
     <i><xsl:apply-templates /></i>
   </xsl:template>

   <xsl:template match="a">
     <xsl:copy-of select="." />
   </xsl:template>

   <xsl:template match="paragraph">
     <xsl:if test="@title > ''">
       <tr>
         <td><br /><br /></td>
       </tr>
       <tr>
         <td class="title">
           <font face="Verdana, Arial, sans-serif" size="-2">
             &#160;&gt;&gt;<xsl:value-of select="@title" />
           </font>
         </td>
       </tr>
     </xsl:if>
     <tr>
       <td>
         <xsl:if test="@title > ''">
           <br />
         </xsl:if>
         &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
         <xsl:apply-templates />
       </td>
     </tr>
   </xsl:template>

</xsl:stylesheet>

<!-- End of XML Tiny Documentation style-sheet -->
