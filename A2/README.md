# Assignment 2
CS 522 Assignment 2 submission for Bradford Smith.

## The BookStore App

Overall I had no major issues with the app.  Some of the given activities needed
to be converted to extend `AppCompatActivity` in order to display action bars
with options items as expected. Other than that the only real challenge was
learning the Parcelable interface and how to pass data between Views with
Intents but even that was not very difficult.

In the testing video I add multiple books to the cart to show the `BooksAdapter`
is working with the `ListView`. The first book only had one author, the second
had two to show that the Adapter and the `ViewBookActivity` displayed the
authors separated by the word "and". The last book had three authors to show
that commas would be added between authors until the last where "and" would be
inserted. A book was clicked on to show the `ViewBookActivity` and then the
ContextMenu was displayed to remove a book. I then checked out to show the
checkout success Toast. I also attempted to checkout with no books to show a
different Toast and showed that the Cancel option worked for adding books and
checking out.

## The Chat Apps

The two chat apps (server and client) were fairly simple to get running, I had
no issues.

The test video for the chat apps simply show me sending multiple messages from the client and receiving them on the server. The server properly displays the messages in the format `"[<name>] says: <message>"`, but nothing was done to enter the name on the client so it shows the default value of "client".
