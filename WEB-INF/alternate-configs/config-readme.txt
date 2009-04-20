This dir contains the config files used by the various machines Chembench
gets installed onto.
The actual config files read by the website when it's running are at:
/build.xml
/WEB-INF/systemConfig.xml
/WEB-INF/web.xml
/WEB-INF/src/hibernate.cfg.xml
/WEB-INF/src/log4j.properties

Typically, there will be a script "build.sh" on each machine, which will copy the config files from 
alternate-configs and replace the above files. This way, each machine can have its own directory
structure, database, etc.


