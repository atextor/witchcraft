<?xml version="1.0" encoding="UTF-8"?>

<!-- XSLT Stylesheet zur simplen Konvertierung von XML -> LaTeX -> PDF -->
<!-- A. Textor -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xslt"
                version="1.0">
<xsl:output method="text" indent="no" encoding="ISO-8859-1"/>
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>


	<xsl:template match="document">
		<xsl:text>\documentclass[11pt,a4paper,pdftex]{article}
			\usepackage{a4}
			\usepackage{german}
			\usepackage{eurosym}
			\usepackage{graphicx}
			\usepackage{amssymb}
			\usepackage{amsmath}
			\usepackage{listings}
			\usepackage[T1]{fontenc}
			\usepackage[latin9]{inputenc}
			\usepackage{color}
			\usepackage{hyperref}
			\definecolor{darkblue}{rgb}{0,0,.5}
			\hypersetup{pdftex=true, colorlinks=true, breaklinks=true, linkcolor=darkblue, plainpages=false, menucolor=darkblue, pagecolor=darkblue, urlcolor=darkblue}
			\lstset{numbers=left, numberstyle=\tiny, stepnumber=3, numbersep=5pt, language=Java, basicstyle=\small, stringstyle=\ttfamily}
			\oddsidemargin 0.0cm
			\textwidth 16.0cm
			\textheight 25.0cm
			\topmargin -2.0cm
		</xsl:text>
		<xsl:apply-templates select="head"/>
		<xsl:text>
			\begin{document}
			\maketitle
		</xsl:text>
		<xsl:apply-templates select="body"/>
		<xsl:text>
			\end{document}
		</xsl:text>
	</xsl:template>

	<xsl:template match="head">
		<xsl:text>\author{</xsl:text>
		<xsl:value-of select="./author/name"/>
		<xsl:if test="./author/additional">
			<xsl:text>\\{\footnotesize (</xsl:text>
			<xsl:value-of select="./author/additional"/>
			<xsl:text>)}</xsl:text>
		</xsl:if>
		<xsl:text>}</xsl:text>
		<xsl:text>\title{</xsl:text>
		<xsl:value-of select="./title"/>
		<xsl:text>}</xsl:text>
		<xsl:text>\date{</xsl:text>
		<xsl:value-of select="./date"/>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<xsl:template name="body">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="itemize">
		<xsl:text>\begin{itemize}</xsl:text>
			<xsl:apply-templates />
		<xsl:text>\end{itemize}</xsl:text>
	</xsl:template>

	<xsl:template match="item">
		<xsl:text>\item[</xsl:text>
		<xsl:value-of select="./@name"/>
		<xsl:text>] </xsl:text>
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="br">
		<xsl:text>\\ </xsl:text>
	</xsl:template>

	<!-- non-breakable space -->
	<xsl:template match="nbsp">
		<xsl:text>~</xsl:text>
	</xsl:template>

	<xsl:template match="tableofcontents">
		<xsl:text>\tableofcontents</xsl:text>
	</xsl:template>

	<xsl:template match="pagebreak">
		<xsl:text>\pagebreak</xsl:text>
	</xsl:template>

	<xsl:template match="section">
		<xsl:text>\section{</xsl:text>
		<xsl:value-of select="./@name"/>
		<xsl:text>}</xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="subsection">
		<xsl:text>\subsection{</xsl:text>
		<xsl:value-of select="./@name"/>
		<xsl:text>}</xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="ref">
		<xsl:text>~[\ref{</xsl:text>
		<xsl:copy-of select="."/>
		<xsl:text>}]</xsl:text>
	</xsl:template>

	<xsl:template match="q">
		<xsl:text>"`</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>"'</xsl:text>
	</xsl:template>

	<xsl:template match="v">
		<xsl:text>\verb|</xsl:text>
		<xsl:copy-of select="."/>
		<xsl:text>|</xsl:text>
	</xsl:template>

	<xsl:template match="e">
		<xsl:text>\emph{</xsl:text>
		<xsl:apply-templates/>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<xsl:template match="image">
		<xsl:text>\includegraphics[width=</xsl:text>
		<xsl:value-of select="./width"/>
		<xsl:text>pt]{</xsl:text>
		<xsl:value-of select="./src"/>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<xsl:template match="listing">
		<xsl:text>\addcontentsline{toc}{subsection}{</xsl:text>
		<xsl:value-of select="./caption"/>
		<xsl:text>}</xsl:text>
		<xsl:text>\lstinputlisting[caption=</xsl:text>
		<xsl:value-of select="./caption"/>
		<xsl:text>,label=</xsl:text>
		<xsl:value-of select="./caption"/>
		<xsl:text>,frame=trbl]{</xsl:text>
		<xsl:value-of select="./incfile"/>
		<xsl:text>}</xsl:text>
	</xsl:template>

</xsl:stylesheet>
