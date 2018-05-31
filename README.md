# [Jease](https://github.com/jease/jease)
===========================

[![Build Status](https://travis-ci.org/jease/jease.svg?branch=master)](https://travis-ci.org/jease/jease)
[![Apache License](http://img.shields.io/badge/license-apache2-orange.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Coverage Status](https://coveralls.io/repos/github/mmghasemi/nasher/badge.svg?branch=master)](https://coveralls.io/github/mmghasemi/nasher?branch=master)


Jease eases the development of content- & database-driven web-applications with Java.

Admin panel + Home pages

[Demo Admin panel](http://demo.jease.org) => http://demo.jease.org username/password=demo

[Home Page](http://jease.org)  

About
-----

Jease eases the development of content- & database-driven web-applications with Java.
Out of the box Jease provides an Ajax-powered Content-Management-System (CMS) which makes use of drag & drop to organize content.
* A relational database that holds blog posts and users.


This project is just meant to be a demonstration, therefore it is neither well documented nor well tested. Use it to learn about the technologies used, but do not use it for productive applications.

Any feedback is welcome, and I will incorporate useful pull requests.

Technologies
------------

* [DB4O](http://db4o.com)db4o or Perst as object-oriented persistence engines
* [Lucene](http://lucene.apache.org/) Lucene as high performance indexing and search technology.
* [Bootstrap](http://getbootstrap.com/)
* [ZK](https://zkoss.org/)ZK as component- & event-driven Ajax-Web-Framework.

User Levels
------------
* User Level = {View posts, Apply new Comment}
* Editor Level = {Add new Post,Edit posts,Delete posts}
* Admin Level = {Manage Users, Posts, Comments, Files, Categories}

Running
-------

Make sure [Maven](http://maven.apache.org/) >= 2.2.1 is installed on your system. Go into the project dir and type `mvn clean package`, then deploy war file generated in target folder on a java webserver like tomcat and then point your browser to `http://localhost:8080`.


RoadMap
-------
* Add configuration properties file
* Change admin UI framework from zk to jsf based ui
* Update lucene and use solr based search
* Add Shopping service and shopping cart
* Add Email support
* Add seo Setting
* Add new responsive themes

License
-------

[The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)







