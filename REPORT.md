# FINAL REPORT
Final Project App for Minor Programming at the Universtiy of Amsterdam

## Introduction
Vegetariano! is a mobile application helping users in the early stage of vegetarianism. It supplies them daily with new recipes. As well it helps keep users commited to their new diet by keeping track of the user's runstreak and total vegetarian days. With this is calculates how many animals are saved and how many CO2-emision is avoided by the user. Users can furthermore relate to the Vegetariano! community and check how many people ate vegetarian at a day, as well as how many animal kills and CO2-emission is avoided by the community as a whole.

## Screenshot
<img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Screenshots/Home.jpg' width="200" height="400">

## Technical Design
The app supports four main functionalities:
1) Mangage user account
2) Keep track whether user ate vegetarian at given day
3) Scoreboard of user and community as a whole
4) Supply user with daily recipes

All four functionalities will now be described seperately, together with the related code.

### Manage user account
To be able to save userdata (e.g. how many days he followed the vegetarian diet), every user needs to have an account on the Vegetariano! app. Managing of the user account is done using FireBase Authentication. Supported actions are: registration, sign in, forgot password, change email adress, change display name, change password, log out and unsubscribe. Screenshots of these actions can be found below.

<img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Screenshots/Signin.jpg' width="200" height="400"> <img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Screenshots/Register.jpg' width="200" height="400"> <img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Screenshots/Reset.jpg' width="200" height="400"> <img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Screenshots/Settings.jpg' width="200" height="400">

The following diagram describes the architecture of the user management functionality:

<img src='https://github.com/MarritL/MarritLeenstrapset62/blob/master/Pictures/ManageUsers2.JPG' width="700" height="350">

#### SignInActivity
 The user first arrives at the SignInActivity, which is the launch screen. There he can choose several options: 1) sign in via de sign in button, 2) register by clicking at the 'I don't have an account yet' text, or 3) reset the password by clicking at the 'Oops... I forgot my password' text. All have a their own onClickListeners. When choosing the second or the third option, the user will go to the respective activities, which are childActivities of the SignInActivity. This is handy, because the user can now use up-navigation if he decides that he wants to go back to the sign in screen. When the user choses the first option his email and password will be checked by the firebase authentication service. If the login fails the user will get a focus-text 'the password or email is incorrect'. If the login succeeds the onLaunch value in the database will be set to true, to make sure the homedisplay is displayed well. Furthermore instances of the recipeLab singleton and communityLab singleton will be initiated. The reason behind this will be discribed in more detail in the paragraphs about the functionalities 3 (Scoreboard of user and community as a whole) and 4 (Supply user with daily recipes). If all this is done, the user will be taken through the SpashActivity to the MainActivity. The SplashActivity is needed to make sure all data needed in the MainActivity is actually loaded.

#### RegisterActivity
The RegistrerActivity is fairly basic form, where the user has to fill in his emailadress, displayname and password. The display name is optional. When the user does not choose a display name the emailadress before the '@' will be used as displayname. This can later be changed in the SettingsFragment. When the user has filled the whole form he will click the register-button which triggers the registerOnClick class, which in turn calls the attemptRegister() function. In this funcion the emailadress and password are validated by three functions: isEmailValid(), isPasswordValid() and isPasswordSame(). These functions make sure that the email contains a '@', the password is at least 6 characters long and the password and the repeated password are the same (i.e. the user did not make typos). If everything is alright, the FireBase authentication service is used to create a useraccount. Furthermore a User object will be made, with the FireBase UID, the email and the displayname and all other variables will be set to 0. This User Object will be added to the FireBase Database. Lastly recipes will be downloaded from the server through the RecipesHelper Callback and these will be saved in the database as well. Since this is more relevant to the 4th functionality 'Supply user with daily recipes', this will be described in more detail in that section. If the user is registrated he will go back to SignInActivity

#### ForgotPasswordActivity
The ForgetPasswordActivity is another simple form. The user can provide his emailadress and when clicked on the button the sendEmailOnClick listener will be triggered. This in turn triggers the sendEmailResetPassword() function. In this function the emailadress is again validated with RegisterActivty's isEmailValid() function. If the user gave an emailadress with an '@', a passwordreset email is send to the emailadress. By following the instructions in the email the user can reset his password. The user will be taken back to the SignInActivity.

#### MainActivity
After the user signed in, he will go through the SplashActivity to the MainActivity, as described in the paragraph 'SignInActivity'. The MainActvity contains a bottomNavigation, through which the user can directly navigate to 4 fragments. The HomeFragment, the UserFragment, the CommunityFragment and the SettingsFragment. For the 'user account managment' functionality only the SettingsFragment is important. The MainActivity and other Fragments will therefor be discussed later.

#### SettingsFragment
The SettingsFragment displays 5 clickable TextViews, representing the following actiosn: change email address, change display name, change password, logout and unsubscribe. Listeners are attached to the TextViews. The change email address, change password and logout TextViews share a listener, in which a if-else statement directs the change email adress and change password to respectively the ChangeEmailFragment and ChangePasswordFragment. These fragments are discussed later. When logout is clicked the Firebase signout() method is called and the SignInActivity is started. For the change display name and unsubscribe actions dialogs are opened. This becuase a whole new fragment was not needed, since the required actions are rather small. The change display name dialog shows an EditText in which the user can fill in his new display name. When clicking the button, the displayname is saved in the FireBase Database. When the user leaves the field empty, again the part of the emailadress before the '@' is used as display name. The unsubscribe dialog shows a warning, explaining the user that unsubscribing is irreversable. If he still decides to delete the account, the user is signed out, his data is deleted from the database and then the account is deleted with the FireBase delete() method. This order is important, because once the account is deleted the database cannot be accessed anymore and therefor deleting from the database after deleting the account will lead to errors. As well when the user is not signed out before deleting the database, the onDataChange methods will still be called, while there is no data in the database anymore. This will lead to errors as well. Both the change display name and unsubscribe dialogs have cancel buttons, pressing these buttons will close the dialog without further action.

#### ChangeEmailFragment
The ChangeEmailFragment is displayed in the MainActivity container, but only accessable via the SettingsFragment. Therefor the navigation is still accessable while in the ChangeEmailFragment. The ChangeEmailFragment works the same as the ForgotPasswordActivity. It contains a simple form in which the user can fill in his new email adress. If the emailadress is valid, the FireBase updateEmail() method will be called and the user will be notified that he has to sign in with the new emailadress from that moment on. The email adress will be updated in the FireBase authentication and the FireBase database. As well a securitymail will be send to the old emailadress, notifying the user that the emailadress for this app was changed. By following the instructions in the email, he can revert this action. When the user is done, he will be taken back to the SettingsFragment.

#### ChangePasswordFragment
The ChangePasswordFragment works the same as the ChangeEmailFragment. It is displayed in the MainActivity container and accessed via the SettingsFragment. The user can fill in his new password and repeat that. If the password is valid (checked by the fuctions: isPasswordValid() and isPasswordSame() in RegisterActivity), this will be updated in FireBase Authentication by the FireBase updatePassword() method. A securityemail will again be send to the user's emailadress. By following the instruction in the email, he can revert this action. When the user is done, he will be taken back to the SettingsFragment.

