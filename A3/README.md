# Assignment 3
CS 522 Assignment 3 submission for Bradford Smith.

## The BookStore App

I had some issues importing the given project files to Android Studio, gradle
had trouble initially syncing the projects. In the case of the BookStore app I
was able to resolve this by changing the target and compile SDK versions from 25
to 26 (the minSdk was fine staying at 22). I'm not sure if this was really an
issue with the code or my development environment, either way it was resolved.

The only other issues were problems with text showing up. For some reason text
added by the adapter to the ListView showed up white on the light background, I
resolved this by forcing the text color in the layout xml.

## The Chat Apps

The given Chat server app had issues running the initial gradle sync as well.
This was resolved by deleting and re-extracting the project from the zip file
(no idea why this was the solution, but it worked). Then I ran into a compile
time error where the guava dependency seemed to be requiring a minSdk level of
26, since the project required minSdk of 22 I resolved this by lowering the
required version of guava from 21 to 20.

The Chat server also had the white text on light background issues which I
resolved again by forcing the text color in the layout xml.

Since no Chat client was given for this assignment I copied the one from
assignment 2, which needed a few minor tweaks to send messages in the format the
Chat server expected.
