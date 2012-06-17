This guide will show some basic stuff you might need to maintain an Ubuntu server. You might not need all of them, but thy are handy enough and I've had to use them at least once.

## Secure a fresh Ubuntu installation (via [Mensk](http://www.mensk.com/webmaster-toolbox/perfect-ubuntu-hardy-nginx-mysql5-php5-wordpress/))

When you install your fresh server, you have a completely unsafe Ubuntu installation. To make it a bit safer, follow these steps (change names and ports accordingly): Login as root (via ssh or using the console, it depends on your physical access to the machine) and change root password:

    passwd

Add new username - yourself:

    adduser jsmith
    visudo

Append this line to end of file (to navigate within 'vi' editor to create next line - use these: L, $, a, <ENTER>):

    jsmith ALL=(ALL) ALL

To save and exit do: <ESC>, :wq, <ENTER>. Now let's set up SSH configuration:

    nano /etc/ssh/sshd_config

Find Port 22 and change number to something different (12345) to make hacking more difficult.Then change the following settings:

    PermitRootLogin no
    X11Forwarding no
    UsePAM no

Append these lines to the very end:

    UseDNS no
    AllowUsers jsmith

After this, we must secure the server with iptables

    iptables-save > /etc/iptables.up.rules
    nano /etc/iptables.test.rules

Copy contents of [this file][1] (content below) and paste it into 'iptables.test.rules'

    *filter


    #  Allows all loopback (lo0) traffic and drop all traffic to 127/8 that doesn't use lo0
    -A INPUT -i lo -j ACCEPT
    -A INPUT -i ! lo -d 127.0.0.0/8 -j REJECT


    #  Accepts all established inbound connections
    -A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT


    #  Allows all outbound traffic
    #  You can modify this to only allow certain traffic
    -A OUTPUT -j ACCEPT


    # Allows HTTP and HTTPS connections from anywhere (the normal ports for websites)
    -A INPUT -p tcp --dport 80 -j ACCEPT
    -A INPUT -p tcp --dport 443 -j ACCEPT

    #  Allows SSH connections
    #
    # THE -dport NUMBER IS THE SAME ONE YOU SET UP IN THE SSHD_CONFIG FILE
    #
    -A INPUT -p tcp -m state --state NEW --dport 30000 -j ACCEPT

    # Allow ping
    -A INPUT -p icmp -m icmp --icmp-type 8 -j ACCEPT


    # log iptables denied calls
    -A INPUT -m limit --limit 5/min -j LOG --log-prefix "iptables denied: " --log-level 7

    # Reject all other inbound - default deny unless explicitly allowed policy
    -A INPUT -j REJECT
    -A FORWARD -j REJECT

    COMMIT

Change port number to your SSH port number on this line:

    -A INPUT -p tcp -m state --state NEW --dport 30000 -j ACCEPT

Save and exit (Ctrl+O, Ctrl+X). To apply new iptables rules:

    iptables-restore < /etc/iptables.test.rules

Then save iptables rules permanently:

    iptables-save > /etc/iptables.up.rules

Make sure iptables rules will apply when server is rebooted as well:

    nano /etc/network/interfaces

Add new line after these 2:

    auto lo

    iface lo inet loopbackpre-up iptables-restore < /etc/iptables.up.rules

Save and exit. Reload SSH to use new ports and configurations:

     /etc/init.d/ssh reload

Keep 'root' session running and open second session. SSH login to your slice to new port, with your new username and password:

    ssh -p 12345 jsmith@123.45.6.78

If you logged on successfully via your new username: 'jsmith' - you may close 'root' session now. If not - you still have 'root' session opened to fix problems. As your user, edit .bashrc file to make terminal window a bit more helpful:

    nano ~/.bashrc

Append these lines to the end of it:

    export PS1="\[\e[32;1m\]\u\[\e[0m\]\[\e[32m\]@\h\[\e[36m\]\w \[\e[33m\]\$ \[\e[0m\]"
    alias ll="ls -la"
    alias a2r="sudo /etc/init.d/apache2 stop && sleep 2 && sudo /etc/init.d/apache2 start"
    alias n2r="sudo /etc/init.d/nginx stop && sleep 2 && sudo /etc/init.d/nginx start"
    alias ver="cat /etc/lsb-release"

Save and exit. Reload .bashrc to make changes active:

    source ~/.bashrc

Update sources:

    sudo aptitude update

Set system locale:

    sudo locale-gen en_US.UTF-8
    sudo /usr/sbin/update-locale LANG=en_US.UTF-8

Upgrade system now:

    sudo aptitude -y safe-upgrade
    sudo aptitude -y full-upgrade

## Clean the server (via [UbuntuGeek][2])

I have a special fixation on cleaning my servers. I don't want any extra file (package, log, whatever) to be there if it is not needed. That's why when I discovered UbuntuCleaner I got quite happy. This tools does this for you:

+ Cleans apt cache

+ Removes config files left from uninstalled .deb packages(it happens if you don’t use the --purge switch with apt-get)

+ Removes every kernel except the one you are using

+ Empties the trashes of every user(including root)

It uses apt and the kernel removing thing searches for ubuntu-only packages, so it can’t work on non-debian system and the result is undetermined for other debian-based system, but you can still use the other features of the script(you’ll just have to comment the parts you don’t want).

The script assumes that you are using the text-based Aptitude application, rather than apt-get and dpkg. If you are not using Aptitude, you should also replace the reference to aptitude clean with apt-get clean and the reference to aptitude purge to dpkg –purge.This can be done done by editing the following script. First you need to download the script from [here][3] or using the following command

    wget http://www.opendesktop.org/CONTENT/content-files/71529-ubucleaner.sh

Now you should have 71529-ubucleaner.sh file you need to give execute permissions using the following command

    sudo chmod +x 71529-ubucleaner.sh

Run the script using the following command

    ./71529-ubucleaner.sh

## Upgrading to a new release (via [HowToForge][4])

For an Ubuntu server, the main advice is to stick to LTS releases, due to their stability. That said, sometimes you might need to upgrade to a non-LTS release, as it happened to me when Launchpad was released and I wanted to install it (it required 9.04). So here I will describe the steps needed to update your distribution. It assumes you are running a server (no X11 installed) and this is your first upgrade:

First become root:

    sudo su

Then run

    apt-get update

and install the package update-manager-core:

    apt-get install update-manager-core

If you are running a LTS release, open the file /etc/update-manager/release-upgrades

    vi /etc/update-manager/release-upgrades

and change Prompt=lts to Prompt=normal. Then run

    do-release-upgrade

to start the distribution upgrade.

## Enabling PHP-FastCGI (via [HowToForge][5])

If you need to run fastcgi scripts on your Ubuntu 9.04 machine, you are lucky as this version provides a FastCGI-enabled PHP5 package. To activate it: Install PHP5 on Ubuntu:

    aptitude install php5-cgi php5-mysql php5-curl
    php5-gd php5-idn php-pear php5-imagick php5-imap php5-mcrypt
    php5-memcache php5-mhash php5-ming php5-pspell php5-recode php5-snmp
    php5-sqlite php5-tidy php5-xmlrpc php5-xsl

Then open /etc/php5/cgi/php.ini and add the line

     cgi.fix_pathinfo = 1

right at the end of the file. This enables the FastCGI package. But there's no standalone FastCGI daemon package for Ubuntu 9.04, therefore we use the spawn-fcgi program from lighttpd. We install lighttpd as follows:

    aptitude install lighttpd
    update-rc.d -f lighttpd remove

so that lighttpd will not start at boot time, as we've installed lighttpd because we need just one program that comes with the package, /usr/bin/spawn-fcgi, which we can use to start FastCGI processes. Of course, you don't want to type in that command manually whenever you boot the system, so to have the system execute the command automatically at boot time, open /etc/rc.local

    vi /etc/rc.local

and add the command at the end of the file (before the exit line):

    /usr/bin/spawn-fcgi -a 127.0.0.1 -p 9000 -u www-data -g www-data -f /usr/bin/php5-cgi -P /var/run/fastcgi-php.pid


  [1]: http://articles.slicehost.com/assets/2007/9/4/iptables.txt
  [2]: http://www.ubuntugeek.com/ubucleaner-simple-bash-script-to-keep-your-ubuntu-system-clean.html
  [3]: http://opendesktop.org/content/show.php/Ubucleaner?content=71529
  [4]: http://www.howtoforge.com/how-to-upgrade-ubuntu-8.04-to-ubuntu-8.10-desktop-and-server
  [5]: http://www.howtoforge.com/installing-nginx-with-php5-and-mysql-support-on-ubuntu-9.04