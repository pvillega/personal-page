
**TL;DR:** Clone [Ansible for Play Framework](https://github.com/pvillega/ansible-ec2-play) to automate the task of deploying Play 2.x projects from a git repository into an EC2 instance.

As many developers I'm not a good sysadmin. That's why I was happy to see the raise of PaaS like [Heroku](http://www.heroku.com/), which facilitated deploying a webapp without having to spend time in sysadmin tasks which I didn't know how to do correctly and which took me too much time. Unfortunately, as with most abstractions, the PaaS environments are simple to use but also quite limited in options. Heroku offering of 512Mb per dyno plus limitations on what you can do can be a problem, and the third party addons get expensive quickly. Then you have Amazon EC2, cheaper and quite flexible, but in exchange you need a bit more of "sysadmin knowledge". Not a big deal for apps like this blog, running in a Micro instance, but if your app is bigger and spans several servers, it may become time consuming.

I wanted to automate the task of managing the server so I did not have to worry too much about it. I looked at both [Chef](http://www.opscode.com/chef/) and [Puppet](https://puppetlabs.com/), but somehow I couldn't bring myself to spend enough time with them. The release of [Amazon OpsWorks](http://aws.amazon.com/opsworks/) raised the issue again as they don't have a Play Framework 2 script ready out of the box, but it was hard to try to focus on that task.

That was it until this last weekend when I discovered, somehow, [Ansible](http://ansible.cc/). Ansible is a tool in the same category as Chef and Puppet (purists will start pointing at relevant differences here, but I only care about the fact that they automate my sysadmin tasks). For some reason Ansible seemed much simpler to understand and use, and I got hooked. Ansible Playbooks consist on a series of yaml files that declare the steps to execute, in order. Ansible itself manages the execution of the steps across servers, so you can run one script on as many servers as you need all at the same time. I won't enter into details as [Ansible](http://ansible.cc/) has good enough documentation, but to put an example let's see a possible Ansible Playbook to install `Authbind` into an EC2 instance:

    # Run with: ansible-playbook -i hosts.ini playenv.yaml -u ubuntu 
    ---
    - hosts: ec2Instance
      vars:
      - ubuntu_release: quantal
    
    tasks:
      - name: Install Authbind
        action: apt pkg=authbind state=latest install_recommends=yes  


Having the tool the task ahead was clear: my main 3 sysadmin issues with EC2 (given my current usage level) are securing the Ubuntu AMI, deploying a Play environment and deploying the app itself. With Ansible, I should be able to automate these steps so I won't do mistakes like forgetting one step as well as reducing the time I have to spend on these tasks.

It wasn't easy (as I said I'm not a sysadmin) and the scripts could be better, but I managed to create something that works: [Ansible for Play Framework in EC2](https://github.com/pvillega/ansible-ec2-play). Instructions for execution are included in the Github repository.

There are 3 relevant scripts:

* **bootstrap.yaml:** runs a series of commands to enhance security of an EC2 Ubuntu AMI. I'm not a sysadmin so some step may be wrong or uncomplete, but what's in there seems to work fine. Requires sudo privilege in the target machine to run.

* **playenv.yaml:** sets some Play Framework dependencies (basically pvm and java 7) as well as Authbind so Play can use port 80 without root privileges. Requires sudo privilege in the target machine to run.

* **deploy.yaml:** clones a Play project from a given Git repository and deploys it on the target machine. The script assumes the repository contains a `start` file in the format provided with the script (see `exec` folder) to be able to successfully launch the app. Customise the `start` file as required. No sudo required, the script uses authbind to link Play to port 80. Be aware that in EC2 Micro instances this script will fail due to low memory as Linux memory manager kills the JVM when building the distributable zip for the app.


If you have a project in Github (or any publicly available Git repository) and an EC2 instance, run the scripts through Ansible and there you go, app deployed and running!

So, that's all. If you see something that can be improved, please contribute with a pull request. I hope with this you can save some time on your next deployment.



