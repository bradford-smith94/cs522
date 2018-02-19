# Assignment 4
CS 522 Assignment 4 submission for Bradford Smith.

## The BookStore App

I didn't have many issues, the most notable that I can remember was the white
text on light background issue that I've seen in the past few assignments. This
was again solved by forcing the text color in the layout where the issue was
present.

## The Chat Apps

The chat app also displayed the text color issue, again solved by forcing the
text color in the affected layout.  Like in assignment 3 I had to reduce the
required version of guava from 21 to 20 in order to get the project to compile.

One notable thing I had to do for the chat server that I didn't have to for the
bookstore was add the content provider to the manifest. This caused some runtime
errors during my first few tests before I realized the manifest needed updating.

Since no chat server project was included in the given sources I used the chat
client that I made for assignment 3.
