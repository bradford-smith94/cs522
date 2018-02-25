# Assignment 5
CS 522 Assignment 5 submission for Bradford Smith.

## The BookStore App

When implementing the BookStore app with Entity Managers I didn't run into any
major issues. I was also able to resolve an issue I've been seeing in the past
few assignments (the white text on light background issue). I managed to figure
out the right keywords to find [a StackOverflow
answer](https://stackoverflow.com/a/3001546) which made me realize I had been
using `getApplicationContext()` in place of using `this` and that
`getApplicationContext()` has some issues with themes.

I was able to copy over the Content Provider, contracts, entities and a decent
amount of the UI from assignment 4. This only left finishing the implementation
of the asynchronous classes and the Entity Manager itself.

## The Chat Apps

While implementing the Chat Server app with Entity Managers I didn't run into
any major issues either. I applied the same fix for the white text on light
background issue that I found for the BookStore app, but nothing else caused any
trouble.

Most of the asynchronous classes and interfaces were able to be copied over from
the BookStore app, so once I had done them once I didn't need to write them
again. Combine that with copying the Content Provider, contracts and entities
from assignment 4 and the only real work I had to do for this app was
implementing the Entity Managers.
