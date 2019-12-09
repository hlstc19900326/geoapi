# NetBeans project configuration

This is the root directory of NetBeans project configuration for GeoAPI.
*THIS CONFIGURATION MAY BE DELETED IN A FUTURE VERSION.* We keep it for
now because it allows flexibility that are hard to achieve with a Maven
configuration, but we may revisit that approach after we migrated the
Java project to Jigsaw modules.


## Installation

As of December 2019, GeoAPI upgraded test suite from JUnit 4 to JUnit 5
but NetBeans does not yet support JUnit 5 in Ant projects. More details
[here](https://blogs.apache.org/netbeans/entry/junit-5-apache-ant-and).
We do not yet have a workaround.


## Editing the configuration

There is 3 important files that must be edited BY HAND for preserving user-
neutral configuration:


### build.xml

This Ant file contains tasks executed in addition to the default NetBeans
tasks during the build process, for:

* Copying the resources.


### nbproject/project.properties

Contains most of the project configuration. The content of this file is
modified by the NetBeans "Project properties" panel. PLEASE REVIEW MANUALLY
BEFORE COMMITTING ANY CHANGE. Please preserve the formatting for easier
reading. Ensure that all directories are relative to a variable.


### nbproject/project.xml

Edited together with `nbproject/project.properties` for declaring the source
directories.
