# Assignment 7
CS 522 Assignment 7 submission for Bradford Smith.

## The Chat App

While implementing the Chat App I ended up running into some issues.

I started by copying over a large amount of work from previous assignments that
was still applicable. However, this time there were some modifications that
needed to be made. The `ChatMessage` and `Peer` entities needed to be changed
slightly to have 'latitude' and 'longitude' attributes, which resulted in
needing to modify their respective contracts and the `ContentProvider`.

The first things that needed fixing was the ChatServer URI, professor posted an
update in the class discussion forum, this and the `RestMethod` class ended up
being updated slightly.

Then when I finished implementing most of the functionality and began testing I
ran into other issues. The `RegisterActivity` was not declared in the manifest,
and the `ChatActivity.onCreate()` handler was exiting early to start the
`RegisterActivity`. Both of these caused crashes almost immediately, but were
simple fixes.

After getting past the immediate crashes I then needed to fix a series of
crashes that occurred when I attempted to send a message to the server. The
first cause for this was that the app needed `WAKE_LOCK` permission (another
thing that I believe was mentioned on the class discussion forum).

The app also wasn't initially even communicating with the server because all the
code was using `127.0.0.1` (which points to the AVD localhost) this was fixed by
using `10.0.2.2` (which is the IP at which the AVD is guaranteed to reach the
development host).

Then I found that the `ChatHelper` wasn't hooked up to the `ResultReceiver`s so
that even though requests went through to the server results never made it back
to the UI. This was caused by the given code making use of a version of
`addRequest()` that set the `ResultReceiver` to null, adding the
`ResultReceiver` to the function call hooked it up and fixed that issue.

All messages that hit the server were failing because the JSON object that was
being sent did not match what the server expected. Editing the keys of the JSON
object in the `PostMessageRequest` class fixed this fairly easily.

Finally, the app crashed every time a message was sent because the messages were
failing to be added to the local database. This was initially because latitude
and longitude were null, but after adding them it still failed. This time it
failed because the `Peer` foreign key reference was failing. After trying to
also add `Peer`s I ran into issues with the entity manager and eventually solved
the `ContentProvider` issues the same way I did in assignment 6, by making the
insertions synchronous.  I felt that this was a reasonable solution because we
were already off the UI thread so they didn't need to be asynchronous.

After that I found that the `SettingsActivity` wasn't working as expected.
Fixing this required changing the expected keys in the `settings.xml` file as
well as adding some code to `SettingsActivity` to actually populate the fields
with their values.
