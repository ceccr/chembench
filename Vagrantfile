# -*- mode: ruby -*-
# vi: set ft=ruby :
Vagrant.configure(2) do |config|
    config.vm.box = "iansjk/chembench"
	config.vm.provider "virtualbox" do |vb|
		vb.memory = "2048"
	end
    config.ssh.insert_key = true
    config.vm.synced_folder ".", "/vagrant"
end

