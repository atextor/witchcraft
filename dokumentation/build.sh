#!/bin/bash
xalanpath=.
filebase=doku

for i in ../src/*.java; do
	iconv -f UTF-8 -t ISO-8859-1 $i | sed -e 's/	/ /g' > ./`basename $i`
done

# pdflatex muss 2 mal ausgefuehrt werden damit die referenzen aufgeloest werden!
java -cp $xalanpath/xalan.jar:$xalanpath/xml-apis.jar:$xalanpath/xercesImpl.jar org.apache.xalan.xslt.Process \
	-in $filebase.xml \
	-xsl pdf.xsl \
	-out $filebase.tex && pdflatex $filebase.tex && pdflatex $filebase.tex
rm *.java 2>&1 &>/dev/null
