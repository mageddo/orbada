#! /bin/sh
cd `dirname $0`
java -Xmx200M -Djava.system.class.loader=pl.mpak.startup.StartupClassLoader -jar startup.jar