#!/bin/bash

BASE=$PWD;
RESOURCES_FOLDER=src/main/resources
JAVA_FOLDER=src/main/java
for i in `ls`;
	
	# entrando na pasta do modulo
	cd $BASE/$i
	echo "working on $PWD ..."

	# criando pastas base
	mkdir -p $RESOURCES_FOLDER
	mkdir -p $JAVA_FOLDER

	# movendo o resources 
	cp -r `find -name "res"` $RESOUCES_FOLDER
	cp -r `find -name "*.properties"` $RESOUCES_FOLDER

	# movendo o java
	mv pl $JAVA_FOLDER

	echo "finished module"	


;done

