
# $Id: Makefile,v 1.3 2001/09/21 16:42:13 cc Exp $

all: prod jar

devel: clean
	jikes -depend +P -deprecation -O jiv/Main.java

devel-jdk: clean
	javac -depend -deprecation jiv/Main.java

debug: clean
	jikes -depend +P -deprecation -g jiv/Main.java

prod: clean
	javac -depend -deprecation -O jiv/Main.java

jar: 
	jar cf jiv/jiv.jar COPYING jiv/*.class
	mv jiv/jiv.jar ../../../www/jiv/devel

clean:
	rm -f jiv/*.class jiv/*.jar

