# -*- mode: ruby -*-
# vi: set ft=ruby :

chembench_home = "/opt/chembench"
chembench_env = <<-ENV
# Chembench environment variables
export CHEMBENCH_HOME=#{chembench_home}
export PATH=$PATH:$CHEMBENCH_HOME/bin:$CHEMBENCH_HOME/jchem/bin
export CHEMAXON_LICENSE_URL=$CHEMBENCH_HOME/licenses/jchem.cxl
export DRGX_LICENSEDATA=$CHEMBENCH_HOME/licenses/dragon.txt
ENV

tomcat_home = "/opt/apache-tomcat-7.0.70"
catalina_opts = <<-OPTS
export CATALINA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
OPTS
tomcat_users_xml = <<-XML
<?xml version="1.0" encoding="utf-8"?>
<tomcat-users>
    <role rolename="manager-gui" />
    <role rolename="manager-script" />
    <user username="admin" password="" roles="manager-gui,manager-script" />
</tomcat-users>
XML

Vagrant.configure(2) do |config|
    config.vm.box = "ubuntu/trusty64"
    config.vm.synced_folder "logs", "#{chembench_home}/logs", create: true
    config.vm.synced_folder "tomcat_logs", "#{tomcat_home}/logs", create: true
    config.vm.synced_folder "users", "#{chembench_home}/users", create: true

    config.vm.network "forwarded_port", guest: 8080, host: 9090
    config.vm.network "forwarded_port", guest: 5005, host: 5005

    config.vm.provider "virtualbox" do |vb|
        vb.memory = "2048"
    end

    config.vm.provision "shell", inline: <<-SHELL
        sudo dpkg --add-architecture i386
        sudo add-apt-repository -y ppa:webupd8team/java
        sudo apt-get update
        echo 'oracle-java8-installer shared/accepted-oracle-license-v1-1 select true' | sudo /usr/bin/debconf-set-selections
        sudo DEBIAN_FRONTEND=noninteractive apt-get install -y \
            mysql-server \
            r-base r-cran-randomforest \
            python2.7 python-pandas python-sklearn \
            gcc-multilib \
            oracle-java8-installer \
            maven \
            dos2unix
        wget -O /tmp/tomcat.tgz 'http://www-us.apache.org/dist/tomcat/tomcat-7/v7.0.70/bin/apache-tomcat-7.0.70.tar.gz'
        sudo tar xzvf /tmp/tomcat.tgz -C /opt
        sudo echo '#{chembench_env}' >> #{tomcat_home}/bin/setenv.sh
        sudo echo '#{catalina_opts}' >> #{tomcat_home}/bin/setenv.sh
        sudo echo '#{tomcat_users_xml}' > #{tomcat_home}/conf/tomcat-users.xml
        sudo tar xzvf /vagrant/basebox.tgz -C /opt
        sudo chown -R vagrant:vagrant /opt/chembench
        mysql -u root -e 'CREATE DATABASE cbprod'
        mysql -u root cbprod < #{chembench_home}/cbprod.sql
    SHELL

    config.vm.provision "shell", run: "always", inline: <<-SHELL
        cp /vagrant/utils/R/* #{chembench_home}/bin
        cp /vagrant/utils/scikit-rf/* #{chembench_home}/bin
        dos2unix #{chembench_home}/bin/*.{R,py}
    SHELL
end

