<?xml version="1.0"?>

<!--
  -
  - Jext documentation style-sheet
  - Based on xtiny-doc DTD
  -
  - DTD:xtiny-doc and XSL:xtiny-doc
  - are (C)1999 Romain Guy
  -
  - Last updated: 12/07/2000 - 22:09:25
  -
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="no" />

   <xsl:template match="/">
     <xsl:apply-templates />
   </xsl:template>

   <xsl:template match="xtiny-doc">
      <html>
        <head>
          <meta name="generator" value="Ga&#239;a v1.4 - Romain Guy" />
          <title><xsl:value-of select="@title" /></title>
          <link rel ="stylesheet" type="text/css" href="../stylesheet.css" title="Style" />
        </head>
        <body>
          <div align="center">
            <xsl:if test="@name > ''">
              <font size="+4" color="#FF0000" face="Verdana, Arial, Helvetica, sans-serif">
                <xsl:value-of select="@name" /><br />
              </font>
            </xsl:if>
            <xsl:if test="@version > ''">
              <font size="-1" color="#FF0000" face="Verdana, Arial, Helvetica, sans-serif">
                v<xsl:value-of select="@version" />
              </font>
              <br /><br />
            </xsl:if>
            <table width="92%" class="main" align="center">
              <xsl:apply-templates />
              <tr>
                <td><br /></td>
              </tr>
              <tr>
                <td class="title">
                  <font face="Verdana, Arial, sans-serif" size="-2">
                    &#160;&gt;&gt;Legals
                  </font>
                </td>
             </tr>
              <tr>
                <td>
                  <br />&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                  <xsl:value-of select="@copyright" />
                  <br /><br />
                  <a>
                    <xsl:attribute name="href">mailto:<xsl:value-of select="@email" /></xsl:attribute>
                    <xsl:value-of select="@email" />
                  </a><br />
                  <a>
                    <xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute>
                    <xsl:value-of select="@url" />
                  </a><br />
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

   <xsl:template match="b">
     <b><xsl:apply-templates /></b>
   </xsl:template>

   <xsl:template match="img">
     <tr>
       <td align="center">
         <xsl:copy-of select="." />
       </td>
     </tr>
   </xsl:template>

   <xsl:template match="sections-list">
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
         <ul>
           <xsl:apply-templates />
         </ul>
       </xsl:otherwise>
     </xsl:choose>
   </xsl:template>

   <xsl:template match="section">
     <li>
       <xsl:choose>
         <xsl:when test="@target > ''">
           <a><xsl:attribute name="href"><xsl:value-of select="@target" /></xsl:attribute>
           <xsl:value-of select="@name" /></a>
         </xsl:when>
         <xsl:otherwise>
           <xsl:value-of select="@name" />
         </xsl:otherwise>
       </xsl:choose>
     </li>
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
