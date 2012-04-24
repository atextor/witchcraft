PKG=de.rccc.java.witchcraft
CP=de
OPTS=-d .
JAVA=java
JAVAC=javac
JAR=jar
JAVADOC=javadoc
JARNAME=Witchcraft.jar
DOCDIR=dokumentation/doc/

.PHONY: doc jar all

default: build

%: src/%.java
	$(JAVAC) $(OPTS) $<

ex:
	$(JAVA) -cp . $(PKG).Main

aex:
	appletviewer applettest.html

doc:
	rm -rf $(DOCDIR)
	$(JAVADOC) -d $(DOCDIR) -private -charset UTF-8 src/*.java
	(cd dokumentation; ./build.sh)

build:
	$(JAVAC) $(OPTS) -encoding UTF-8 src/*.java

jar: build
	@rm -f $(JARNAME)
# Explizit Dateien *.* packen anstatt der Verzeichnisse, damit der ganze
# SVN-Kram nicht mit reinkommt
	$(JAR) -cfm $(JARNAME) MANIFEST name/panitz/java/ws0607/*.class de/rccc/java/witchcraft/*.class bilder/*.* level/*.* sounds/*.* config/*.* html/*.*

jarex: jar
	$(JAVA) -jar $(JARNAME)

clean:
	rm -rf $(CP) 
	rm -f $(JARNAME)
	rm -f dokumentation/doku.{aux,log,out,pdf,tex,toc}

web: jar
	(cd web; ./send)

all: clean jar doc

abgabe: all
	tar cfvj ../witchcraft.tar.bz2 ../witchcraft/*.* ../witchcraft/MANIFEST ../witchcraft/name/panitz/java/ws0607/*.class ../witchcraft/de/rccc/java/witchcraft/*.class ../witchcraft/bilder/*.* ../witchcraft/level/*.* ../witchcraft/sounds/*.* ../witchcraft/config/*.* ../witchcraft/html/*.* ../witchcraft/Makefile ../witchcraft/src/*.java ../witchcraft/dokumentation/{build.sh,doku.xml,pdf.xsl,doc,doku.pdf} ../witchcraft/gfxsrc/*.* ../witchcraft/web/{index.vorlage.xhtml,send,.htaccess}

help:
	@echo "Make-Targets:"
	@echo "  clean          - loescht Verzeichnis mit Klassen"
	@echo "  build          - Default-target, kompiliert alle Klassen"
	@echo "  <klassenname>  - erzeugt die entsprechende .class-Datei im"
	@echo "                   entsprechenden Verzeichnisbaum"
	@echo "  ex             - startet das Spiel"
	@echo "  aex            - startet die Appletversion im Appletviewer"
	@echo "  doc            - generiert javadoc und startet im dokumentation/-"
	@echo "                   Verzeichnis den PDF-build-Prozess (benoetigt LaTeX"
	@echo "                   und Xalan (Pfad-Konfiguration in dokumentation/build.sh))"
	@echo "  jar            - baut das Jar-File"
	@echo "  jarex          - fuehrt die Jar-Datei aus"
	@echo "  web            - laedt Webseite und Applet auf den Webspace - nur fuer"
	@echo "                   Entwickler!"
	@echo "  all            - clean, build, jar, doc"
	@echo "  abgabe         - baut ../witchcraft.tar.bz2, das zur Abgabe dient"
