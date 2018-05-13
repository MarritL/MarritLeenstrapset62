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

All four functions will now be described seperately, together with the related code.

1) Manage user account
To be able to save userdata (e.g. how many days he followed the vegetarian diet), every user needs to have an account on the Vegetariano! app. Managing of the user account is done using FireBase Authentication. Supported actions are: registration, sign in, forgot password, change email adress, change display name, change password, log out and unsubscribe. 


