#!/bin/bash

BASE=$PWD;
RESOURCES_FOLDER=src/main/resources
JAVA_FOLDER=src/main/java
for i in `ls -d */`; do
	
	# entrando na pasta do modulo
	cd $BASE/$i
	echo "working on '$PWD'"

	# criando pastas base
	mkdir -p $RESOURCES_FOLDER
	mkdir -p $JAVA_FOLDER

	# movendo o resources 
	mv -f `find -name "res"` $RESOURCES_FOLDER
	mv -f `find -name "*.properties"` $RESOURCES_FOLDER

	# movendo o java
	mv -f pl $JAVA_FOLDER

	echo "finished module"	


done

