# Michigan Hackers Android App
This project is the Android application for the University of Michigan, Ann Arbor student organization called Michigan Hackers.
The goal of this project is to make a user-friendly application that will provide information about and tools for Michigan Hackers.
This project is also used to help the members of the Michigan Hackers Android App Team learn the skills related to Android application development.

# Getting Started

## Prerequisites

### Java SDK
Install the latest version of the Java Software Development Kit (SDK).  
Java is the programming language our team uses to develop the app.  
[Download](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

### Integrated Development Environment (IDE)
Install an IDE to use to work on the project. Android Studio is recommended.  
An IDE makes programming the application faster and easier.  
[Download](https://developer.android.com/studio/)

### Git
Install git, the software our team uses for version control.  
[Download](https://git-scm.com/downloads)

#### Github Account
Create a Github account to interact with the remote repository.

## Installing
1. Open Android Studio
2. Select "Check out project from Version Control"
3. Select "Github"
4. Paste the link to this Github repository (https://github.com/michiganhackers/androidapp) and select "Clone"
5. Download the google-services.json file from the Google drive in Michigan Hackers > Core > Core 2018-2019 > Android and place it in the androidapp/app directory
6. Download the MichiganHackersKey.jks file from the Google drive in Michigan Hackers > Core > Core 2018-2019 > Android and place it anywhere you'd like
7. Open to the AndroidStudioProjects\androidapp\app\build.gradle file and change the storeFile location to the location you used in step 6.
```
    debug {
        keyAlias 'android-debug'
        keyPassword 'ASK ANDROID EXEC'
        storeFile file('THE LOCATION YOU PLACED THE FILE/MichiganHackersKey.jks')
        storePassword 'ASK ANDROID EXEC'
    }
```
# Deployment
The app currently has a bug that makes it so that you can't see the calendar events in the debug version. The current workaround is to install the app from the playstore and sign in with your Google account. This allows fixes the bug in the debug version if you sign in with the same google account.

# Contributing
Join the team

# Authors
* Vincent Nagel - Team Leader
* Owain Kert - Team Leader

# License
This project is licensed under the MIT License - see the [LICENSE](/LICENSE) file for details
