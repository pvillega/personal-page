Originally I wanted to install Launchpad on my server to manage my projects. As of 17th of August of 2009, Launchpad is not yet ready for production. The only way to install it is using an script, only available for Ubuntu 9.04, which doesn't work too well on a server. I'll keep an eye on it as I like the way Canonical does things (and applications), but in the meantime I decided to use [Redmine](http://www.redmine.org/) as I found Trac lacking on some areas.

## Redmine

Redmine is an open source, web-based project management and bug-tracking tool, written in Ruby on Rails and heavily influenced by Trac. It improves on several areas, like providing multiple project support, integrating with several source control systems (from popular SVN to less popular Bazaar or Darcs) and allowing extension of its functionalities through plugins. This guide describes how to install Redmine in an Ubuntu 9.04 server using Mongrel as web server, Nginx as proxy and MySQL as database. To achieve this we have to follow 3 steps:

+ install ruby and rails and MySQL
+ install Redmine
+ install Mongrel and Nginx

This guide can be adapted to older versions of Ubuntu and most Linux distributions, just replacing the tools/packages by the specific ones available in that distribution. The guide is a quick overview of the main steps, for  more information on configuration options check the documentation of the corresponding tool.

## Install Ruby and Rails and MySQL

To install the components first ensure the system is up to date:

    sudo apt-get update
    sudo apt-get dist-upgrade

Then run the following command:

    sudo apt-get install build-essential ruby ri rdoc mysql-server libmysql-ruby ruby1.8-dev irb1.8 libdbd-mysql-perl libdbi-perl
          libmysql-ruby1.8 libmysqlclient15off libnet-daemon-perl libplrpc-perl libreadline-ruby1.8 libruby1.8 rdoc1.8 ri1.8 ruby1.8
          irb libopenssl-ruby libopenssl-ruby1.8 libhtml-template-perl mysql-client-5.0 mysql-common mysql-server-5.0 mysql-server-core-5.0

Now that we have Ruby installed, we use Gems to install Rails:

    sudo gem update
    sudo gem install rails --no-rdoc –no-ri

The flags –no-rdoc and –no-ri are included to reduce the RAM footprint of the component, feel free to remove them if your machine has more than enough RAM. To finish this part, open the MySQL client and create the Redmine database:

    create database redmine character set utf8;

## Install Redmine

Get the code from the [download][1] page. Although the stable release is recommended, if you are going to use Bazaar the svn contains some fixes you want to use, so do an export from the trunk and use that. (Note: if you use the trunk, you'll have to install a newer Rails using Gems due to version requirements). Select a folder in your server (from now on, APP) and copy the downloaded code there. Move to the APP folder and run:

    sudo cp config/database.yml.example config/database.yml
    sudo cp config/email.yml.example config/email.yml

Edit the new yml files and change the configuration as required (MySQL details, mail server, etc).  Now run these commands to create the database and populate it:

    rake db:migrate RAILS_ENV="production"
    rake redmine:load_default_data RAILS_ENV="production"

We also need ImageMagick  (although is optional):

    sudo apt-get install imagemagick libmagickwand-dev
    sudo gem install rmagick

## Install Mongrel and Nginx

To run Redmine we will use a standard setup for Rails applications, consistent on Mongrel as web server and Nginx as proxy that interacts with the network. We will run Mongrel as Mongrel Cluster, to improve the performance as Mongrel is single threaded and we will need multiple instances to server multiple clients. First we need to install Mongrel and Mongrel Cluster:

    sudo gem install mongrel
    sudo gem install mongrel_cluster

Now move to APP folder and run:

    mongrel_rails cluster::configure -e production -p 8000 -N 2 -c APP -a 127.0.0.1

The flag -p indicates the initial port to use by Mongrel. Flag -N tells how many instances you want running in the cluster. Flag -a shows the bind address of the cluster. Once created, we need to set it up so it starts automatically with the server:

    sudo mkdir /etc/mongrel_cluster
    sudo ln -s APP/config/mongrel_cluster.yml /etc/mongrel_cluster/your-app-name.yml
    sudo cp /var/lib/gems/1.8/gems/mongrel_cluster-1.0.5/resources/mongrel_cluster /etc/init.d/
    sudo chmod +x /etc/init.d/mongrel_cluster
    sudo /usr/sbin/update-rc.d mongrel_cluster defaults

Now you can control the cluster with the commands:

    mongrel_cluster_ctl start
    mongrel_cluster_ctl stop
    mongrel_cluster_ctl restart

Now install nginx (if you don't have it):

    sudo apt-get install nginx

We need to create a virtual host for Nginx, that will receive the requests and forward them to Redmine. Assuming your public domain will be “domain.com”:

    sudo nano /usr/local/nginx/sites-available/domain.com

And copy inside:

    upstream domain1 {

     server 127.0.0.1:5000;
     server 127.0.0.1:5001;
     server 127.0.0.1:5002;

    }

    server {

     listen   80;
     server_name  www.domain.com;
     rewrite ^/(.*) http://domain.com/$1 permanent;

    }

    server {

     listen   80;
     server_name domain.com;

     access_log /home/demo/public_html/railsapp/log/access.log;
     error_log /home/demo/public_html/railsapp/log/error.log;

     root   /home/demo/public_html/railsapp/public/;

     index  index.html;

     location / {

          proxy_set_header  X-Real-IP  $remote_addr;
          proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header Host $http_host;
          proxy_redirect false;

          if (-f $request_filename/index.html) {
            rewrite (.*) $1/index.html break;
          }

          if (-f $request_filename.html) {
            rewrite (.*) $1.html break;
          }

          if (!-f $request_filename) {
            proxy_pass http://domain1;
            break;
          }

     }

    }

Be careful to change the port numbers and domain name to the ones you'll use on your server. Now enable the site:

    sudo ln -s /usr/local/nginx/sites-available/domain.com /usr/local/nginx/sites-enabled/domain.com

and once you restart Nginx your Redmine installation is ready to go. Configure Bazaar First we need to edit the file config/environment.rb to add new environment variables related to Python, to avoid problems while accessing Bazaar:

    ENV['PYTHONPATH'] = '/usr/lib/python2.6'
    ENV['PATH'] = "#{ENV['PATH']}:/usr/bin"

When you create a project you just need to point Redmine to your Bazaar main folder, this will allow you to browse the repository. Be aware it still has some limitations when displaying contents, but it works quite well. To work with Bazaar, let's set up a repository on  our server. This will be a dumb repository, it means is nothing more than a  folder that can be accessed via sftp that stores the data of the repository. First of all, in Ubuntu 9.04 (our server) let's install the needed tools (sftp) and enable them as accepted shell:

    $apt-get install ssh openssh-server
    $echo '/usr/lib/sftp-server' >> /etc/shells

Then create a user to connect via sftp to the server:

    $sudo useradd --create-home --home-dir /home/bzr --shell /usr/lib/sftp-server bzr

And that's it, we have a folder (/home/bzr/) ready to receive our Bazaar projects. Is a good practice to give write rights to that folder to members of group “bzr”, and to add a password to the 'bzr' user. On your client machine, install Bazaar (using apt-get or similar). Then download the Uploader plugin which eases the task to commit changes to “dumb servers”:

    $ mkdir /home/pvillega/.bazaar/plugins
    $ bzr co lp:bzr-upload ~/.bazaar/plugins/upload

or if you use Ubuntu/Debian with:

    $sudo apt-get install bzr-upload

Then create the base repository. There are two ways of doing this, you can create a shared repository which stores every child branch revision, or you can create standalone repositories, which are good if you don’t want to share revision information with other branches. Here we create a shared repository:

    bzr init-repository some_directory
    bzr init some_directory/trunk
    bzr init some_directory/branches

Now, to interact with the server we created before, first run:

    bzr push --create-prefix sftp://bzr@server:port/~/test

This pushes the project to the server and creates the folder 'test' if needed. After that, all the interaction with the server can be (usually) reduced to:

     bzr pull sftp://bzr@server:port/~/test   #update changes from the server to our client
     bzr co sftp://bzr@server:port/~/test      #checkout the project
     bzr push sftp://bzr@server:port/~/test  #push changes to the server

Ok, is not the nicest way to do that, but remember this is a distributed repository, you won't update or push changes too often, usually only when you are done with a piece of work.  There's an alternative method, using what is called a “smart server”, which is faster than the previous way (but not always possible to use for several reasons). In this case we will use an ssh connection to communicate with the repository, but this requires that our user belongs to the group “bzr” to be able to write in that folder. The command for the first commit is:

    bzr push --create-prefix bzr+ssh://server:port/home/bzr/test #requires full path, from root

In a similar way we interact with the server using:

     bzr pull bzr+ssh://bzr@server:port/~/test   #update changes from the server to our client
     bzr co bzr+ssh://bzr@server:port/~/test      #checkout the project
     bzr push bzr+ssh://bzr@server:port/~/test  #push changes to the server

## Install Redmine Themes

You can install some new themes for Redmine, available [here][2]. Although is mainly a cosmetic change, some themes provide enhancements like colouring tickets according to priority and they are really easy to manage. For example, to install the [Basecamp][3] theme:

+ Download application.css
+ Create required directories: mkdir -p redmine/public/themes/basecamp/stylesheets
+ Copy downloaded file:  cp application.css redmine/public/themes/basecamp/stylesheets

That's it .. now restart your instance running Redmine and select the new basecamp theme from /settings

## Install Redmine Plugins

Redmine provides several plugins that enhance the capabilities of the tool. Amongst them you can find some nice ones like:

+ Bots Filter: avoids bots from accessing certain areas of the application
+ Charts: adds some project based charts
+ Exception Handler: notifies admins when an exception occurs
+ Hudson: integrates Redmine and Hudson, allowing you to see Hudson reports
+ Status updates: allows users to say what are they doing, kind of Twitter per project.
+ Vote: allows users to vote on issues so you see which ones are important to them

You can find these and many more in this [page][4] . To install a plugin, follow the instructions on the “Installation” section of its page. Then restart Redmine and you are good to go.

## Connect Mylyn to Redmine

If you use Mylyn (and you should!) use these [instructions][5] to connect it to your Redmine repository.


  [1]: http://www.redmine.org/projects/redmine/wiki/Download
  [2]: http://www.redmine.org/projects/redmine/wiki/Theme_List
  [3]: http://theill.com/stuff/redmine/
  [4]: http://www.redmine.org/projects/redmine/wiki/Plugin_List
  [5]: http://www.redmine.org/projects/redmine/wiki/HowTo_Mylyn
