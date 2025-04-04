# [Jease](https://github.com/jease/jease)
===========================

[![Build Status](https://travis-ci.org/jease/jease.svg?branch=master)](https://travis-ci.org/jease/jease)
 [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


Jease eases the development of content- & database-driven web-applications with Java.

New branch of Jesae with Relational Database released with name [JeasePlus](https://github.com/jease/jeasePlus)

[Home Page](http://jease.javahost.org)

[Demo->TBA](#)

[Demo Admin panel->TBA]()
<!--http://demo.jease.org/login?file&login=demo&password=Demo123$-->


# Getting Started

## 1) Prerequisites


*   Download and install a recent [Java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (Java SE JDK) or [OpenJDK](http://openjdk.java.net/).  **Please note:** You'll need a full Java Development Kit (JDK), a simple Java Runtime Environment (JRE) won't work.
*   Check your [environment-variables](http://en.wikipedia.org/wiki/Environment_variable): _JAVA_HOME_ needs to point to the root-directory of your JDK.

## 2) Install


*   Download the [latest binary release bundle](http://jease..javahost.org/download/latest).
*   Unzip the downloaded package.

## 3) Start


*   **Linux:** Open a terminal and enter:  
    _jease/bin/catalina.sh run_
*   **Windows:** Navigate to "jease -> bin" and double-click:  
    _startup (= startup.bat)_

## 4) Setup


*   Open your browser and call:  
    [http://localhost:8080/cms/setup](http://localhost:8080/cms/setup)
*   Choose a name, login and password for the administration account. The password must contain at minimum 8 characters with mixed case, digits and symbols.
*   Now you're redirected to the login-page. Enter the login and password for the newly created administrator.
*   Have fun...

## 5) Login


*   To login later on, simply call:  
    [http://localhost:8080/cms](http://localhost:8080/cms)
*   To view the public page:  
    [http://localhost:8080/](http://localhost:8080/)


## 6) (Optional) Config Solr
   
   For use solr in your environment please read this tutorial: https://github.com/jease/solr-core



About
-----

Jease eases the development of content- & database-driven web-applications with Java.
Out of the box Jease provides an Ajax-powered Content-Management-System (CMS) which makes use of drag & drop to organize content.
* A relational database that holds blog posts and users.


This project is just meant to be a demonstration, therefore it is neither well documented nor well tested. Use it to learn about the technologies used, but do not use it for productive applications.

Any feedback is welcome, and I will incorporate useful pull requests.

Technologies
------------

* [DB4O](http://db4o.com) db4o or Perst or ZooDB as object-oriented persistence engines
* [Lucene](http://lucene.apache.org/) Lucene as high performance indexing and search technology.
* [Bootstrap](http://getbootstrap.com/)
* [ZK](https://zkoss.org/) ZK as component- & event-driven Ajax-Web-Framework.
* [Solr](http://lucene.apache.org/solr/) Apache  Solr is the popular, blazing-fast, open source enterprise search platform built on Apache Lucene™. 


Running
-------

Make sure [Maven](http://maven.apache.org/) >= 2.2.1 is installed on your system. Go into the project dir and type `mvn clean package`, then deploy war file generated in target folder on a java webserver like tomcat and then point your browser to `http://localhost:8080`.


## Contribution
Please Join us Jease User Group at GoogleGroup
https://groups.google.com/forum/#!forum/jease

RoadMap
-------
* Add configuration properties file
* Change admin UI framework from zk to jsf based ui
* Update lucene and use solr based search (Completed [#141](https://github.com/jease/jease/issues/141))
* Add Shopping service and shopping cart
* Add Email support
* Add seo Setting (Completed [#140](https://github.com/jease/jease/issues/140) )
* Add new responsive themes (Completed [#133](https://github.com/jease/jease/issues/133))


## Twitter
@jease18

## Facebook
[Jease on facebook](https://www.facebook.com/java.ease.7)



License
-------

[GPL 3](http://jease.org/gpl3)







