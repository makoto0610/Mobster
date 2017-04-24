# Mobster Android Application
Mobster is an Android application based on our client’s ([Dr. Nick Jones](https://sites.google.com/a/uah.edu/njones/) from the University of Alabama, Huntsville) idea of ochlosophia, which means mob wisdom, a term used to describe the conception of the application. Essentially, ochlosophia refers to people’s tendency to offer advice to others, often strangers, as well as people’s inclination to take advice from anonymous, online sources. The goal of our application is to accommodate this idea by allowing users to ask and offer decision-making questions and advice anonymously. These questions can range from being relatively small such as "What toothpaste should I buy?" to being something quite impactful such as "What should I be when I grow up?".

## Features
- Creating a Mobster account and logging into and out of the application
- Viewing "New", "Trending", and "Closed" questions
- Creating your own decision making question and giving your own options for others to vote on
- Voting on others' questions and commenting on their decisions
- Viewing your own "Open" and "Closed" questions
- Favoriting your favorite question and flagging inappropriate questions
- Viewing questions that have been posted around your location
- Searching question (in general and in your own questions) based on keywords and tagged words
- Forget Password capability to allow the user the ability to change his/her password if forgotten
- Verifying your email and updating your email if necessary
- Notifications to be notified when one of your questions has closed
- Changing the color theme of the application (light and dark settings)
- Viewing statistics about your time with Mobster
- FAQ
- Admin functionality to view overall statistics, ban users, and delete flagged questions

### Known Bugs
- The list of questions  is not implemented to scale and will crash when there is a large number of questions (note that this number is dependent on the device but it is probably in the hundreds) ; this is because of memory limitations on the phone and we did not code the list to efficiently use memory
- The trending and closed tabs refresh the questions when the tabs are clicked, but the closed tab currently does not 
- In the location activity, closed questions are displayed and can be voted on
- When verifying email, in order for the verified button to appear, the user must logout and log back into the application; this is because the application does not update this information immediately after the user has checked their email and verified it
- Admin statistics are incorrect and do not show the right number of questions
- The application will sometimes crash when the Internet/WiFi connection is lost
- The application does not gracefully handle database connection issues
- The application lets a single user login from multiple devices

## Installation
### Using the Application
To use the application on mobile (Android), please simply go to the Google Play Store and search for "Mobster" and follow the standard application installation procedure. (WILL BE RELEASED SOON)

### Contribute to the Development
This Android application is developed in Android Studio and we highly recommend you to use Android Studio for both development and testing.
To clone the repository please use
'''git clone https://github.com/makoto0610/Mobster''' in the directory you want to clone the repository to.
To download Android Studio, please use the following: [here](https://developer.android.com/studio/index.html)
Once Android Studio and repository are set-up, please open the project using Android Studio to run the application. NOTE: You can easily open our project by selecting '''Import project''' and then selecting the appropriate '''build.gradle''' file. This gradle file will build all of the required dependencies when imported into Android Studio. 

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my_new_feature`
3. Commit your changes: `git commit -m 'Your own commit message'`
4. Push to the branch: `git push origin my_new_feature`
5. Submit a pull request :D

## History
Version 1.0 of the application under development.

## Credits
[Makoto, R](https://github.com/makoto0610), [Ani, R](https://github.com/anir1296), [Christine, S](https://github.com/cshih299), [Esha, S](https://github.com/esingh7), [Natalie, Y](https://github.com/nathyu)

## License
All rights reserved.
