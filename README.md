
# OAK Persistence
This is a project is Created with the goal to implement an OAK persistence layer (Data Store).


## Description
This is a project is Created with the goal to implement an OAK persistence layer (Data Store). that persists the data in a
local directory using the git object database. Because the Oak architecture does not provide a way to configure a new Data Store
implementation, the solution has to be implemented as a JDBC driver.


## Structure
As of now this project has only one Module. There is no Parent Project or other modules.

## Installation requirements
	- JDK
	- Maven
	- Eclipse or IntelliJ
	- Eclipse plugins:
		* Maven Integration for Eclipse
		* Lombok
	- IntelliJ plugins:
		* Lombok

## Build
  1. Using Maven
      * Use Maven Plugins and maven commands to build the project.


## How to build your code locally in Command prompt

 To build this project in local Command prompt, we need to follow the below steps

    1. Make sure that maven is installed in your local.
    2. Go to the root folder of the project in local file system.
    3. open Command prompt at the root folder level
    4. Run the maven commands (mvn clean install).


## How to import your code locally using Eclipse

	1.	Go to "File"/ "Import" in the following window choose "Maven"/ "Existing Maven Projects" click "Next"
	2.	Choose the root directory, select all Projects and Finish the import
	3.	Wait until Eclipse loads all dependencies

## How to build your code locally using Eclipse

    1. In Eclipse IDE Tool bar Click on Run option. It will show lot of options in the dropdown menu.
    2. Click on "Run Configurations", it will open a popup window.
    3. Right click "Maven Build", select "New Configuration"
    4.  Add the values to below options.
               a. Put in the Name of your configuration
               b. Base directory (Project Root Path)
               c. Goals (Clean install)
               d. Profiles ( If there is any profile specific goals)
    6. Apply all the settings and click on Run to execute your configuration and run the project.

## How to build your code locally using IntelliJ

 To build the project in IntelliJ, we need to configure maven commands(mvn clean install) in IntelliJ

    1. In IntelliJ IDE Tool bar Click on Run option. It will show lot of options in the dropdown menu.
    2. Click on Edit Configurations, it will open a popup window.
    3. Click on + symbol to create a new configuration
    4. Select maven option for creating maven goal.
    5.  Add the values to below options.
               a. Working directory (Project Root Path)
               b. Command line (Clean install)
               c. Profiles ( If there is any profile specific goals)
    6. Give the Goal name and Apply all the settings and click on OK.


 Now we can run the build with the Given goal name.


## Testing

This project is developed with Test Driven Development Approach.

* unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
