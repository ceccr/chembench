vagrant-basebox
===============
This directory contains a `Vagrantfile` that creates the `chembench` Vagrant
base box, which is referenced by Chembench's top-level `Vagrantfile`.

To create the base box:

1. Run `vagrant up --no-provision` to start up the VM.
2. Use `vboxmanage list vms` to find the name of the VM within VirtualBox.
3. Run the following commands:

        $ vagrant package --base <name-of-VM> \
            --include basebox.tgz,jchem.cookie,chembench_env.sh \
            --vagrantfile Vagrantfile
        $ vagrant box add chembench package.box

**Note**: The files `basebox.tgz` and `jchem.cookie` are required to build
the base box. The file `basebox.tgz` contains essential binaries, licenses,
and configuration. Due to software licensing issues, it is not included. The
file `jchem.cookie` is a login cookie that is necessary to download the JChem
software which cannot be included for security reasons.

If you need these files and you are a Chembench developer, contact another team
member.

