# DESIGN DOCUMENT
Final Project App for Minor Programming at the Universtiy of Amsterdam

## App functionality
The app will do mainly four things:
1) login user
2) ask user if he ate vegetarian
3) scoreboard of user and community as a whole
4) give a daily recipe

## User Interface
The user interface will consist of a login screen and one activity with three tabs to fullfull the four functions (see sketch). 

<img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Pictures/Design.jpg' width="400" height="200">

The login screen will be simple and clean, just asking for a username and password, with an OK-button. However, it would be nice to add also other login options like FaceBook-login. The activity will have three tabs (home, user and community). The home screen will display the question (i.e. 'did you eat vegetarian?') and a large picture of the daily recipe featured. Something like the Yummly app (see picture 1), but then with the question above. The User and Community tab will look mostly the same. They will display a kind of scoreboard, with one value in a more prominent place drawing attention to it. For the user-tab this will be most likely the runstreak of total animals saved, and for the CommunityTab most likely the users today or total days. The user and community tab design will look something like the user tab on the Headspace app (see picture 2). The idea for the overall design with three tabs is also based on the Headspace app.

  <img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Pictures/Yummly.jpg' width="200" height="400"> <img    src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Pictures/Headspace.jpg' width="200" height="400"> 

## Utility modules, classes and functions
At the time i started the app I didn't know it was for the final project and therefor I have not made a nice diagram of the utility modules, clases and functions. In the sketch of the UI paragraph, you can see some hints of the ideas I had when I was designing the app. 
* I wanted to make a sign-in activity that with a button onClickListener sent the user to the mainActivity. 
* In this mainActivity there would be three tabs (bottomnavigation):
  * Hometab: 
     * A recipe would be displayed with data from the Yummly API. I was thinking to use AsyncTask for that in a seperate class with a HttpRequestHelper class doing the actually API request in the doInBackground() funtion. Something like the to-listen-list (pset3) from Native App studio.
     * The question ("did you eat vegetarian) would be displayed with a YES and NO button. onButtonClick() these data would be saved in the Firebase database.
  * UserTab:
     * Reads from the user-data from the firebase database and performs the calculations to see how many animals are saved and how much CO2 emission is avoided. I was thinking to put the onDataChangeListener from Firebase in this tab. 
     * Includes a settings-button (as also in the Headspace app), where a user can change the account settings. (like dispalyName and password. I was thinking to make seperate functions for all settings.
  * CommunityTab:
     * Basically the same as the userTab, so reads also from the database with an onDataChangeListener from Firebase.

## API, plugins and frameworks
* I need recipe data. You can find several APIs for that but the Yummly Documentation looks very clear and easy to use. If I get the student-account I planning to use the Yummly API: https://developer.yummly.com/documentation
* I will use firebase for logins (authentication) and the realtime database.

## Database
I will use the FireBase realtime database to save user-data. The structure will look like this:

<img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Pictures/Database.jpg' width="400" height="200">
     
 
