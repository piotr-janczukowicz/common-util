<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:p="http://pmd.sourceforge.net/report/2.0.0">
    <xsl:output method="text"/>
    <xsl:template match="/">
        <xsl:for-each select="p:pmd/p:file/p:violation">
            <xsl:value-of select="../@name"/>
            <xsl:text>:</xsl:text>
            <xsl:value-of select="@beginline"/>
            <xsl:text> [</xsl:text>
            <xsl:value-of select="@rule"/>
            <xsl:text>] </xsl:text>
            <xsl:value-of select="normalize-space(.)"/>
            <xsl:text>&#10;</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
