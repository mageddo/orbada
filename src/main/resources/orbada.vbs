Set objShell = CreateObject("WScript.Shell")
objShell.Run "javaw -Xmx200M -Djava.system.class.loader=pl.mpak.startup.StartupClassLoader -jar startup.jar", 0