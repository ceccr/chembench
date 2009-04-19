rm -rf ROOT
/usr/local/tomcat/bin/shutdown.sh
rm -r /usr/local/tomcat/webapps/ROOT
svn checkout http://source.ibiblio.org/svn/ceccr/CECCR-QSAR/trunk ROOT --revision HEAD
rm build.xml
cp ROOT/build.xml . 
ant
/usr/local/tomcat/bin/startup.sh