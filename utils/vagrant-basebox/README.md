vagrant-basebox
===============
This directory contains a `Vagrantfile` that creates the `chembench` Vagrant
base box, which is referenced by Chembench's top-level `Vagrantfile`.

To create the base box:

1. Run `vagrant up --no-provision` to start up the VM.
2. Use `vboxmanage list vms` to find the name of the VM within VirtualBox.
3. Run the following commands:

        $ vagrant package --base <name-of-VM> \
            --include basebox.tar.xz,chembench_env.sh \
            --vagrantfile Vagrantfile
        $ vagrant box add chembench package.box

**Note**: The file `basebox.tar.xz` is required to build the base box and
contains essential binaries, licenses, and configuration. Due to software
licensing issues, it is not included. If you need the file and you are a
Chembench developer, contact another team member to acquire it.

