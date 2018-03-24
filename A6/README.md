# Assignment 6
CS 522 Assignment 6 submission for Bradford Smith.

## The Chat App

While implementing the Chat app I ran into a few issues. Most to the work was
already done in previous assignments, implementing the background Service was
pretty much the only new work.

After everything was implemented and I began testing I ran into my first issue,
which was an instantaneous crash on launch because the shared preferences threw
a `ClassCastException`. I tracked this down to be caused by the `app_port`
setting, the resource strings had already saved a string value under `app_port`
and the Service expected an integer. So I simply worked around the issue by
changing the preference key to be `chat_port` instead.

Then next issue I found was that messages weren't appearing to be sent, after
debugging I found they sent just fine but could not be added to the Content
Provider because the Entity Managers needed an Activity Context that the Service
couldn't provide. To fix this I made the Service get a synchronous Content
Resolver and used that to directly interface with the Content Provider. I
thought it would be perfectly fine to use synchronous queries and updates here
because the Service would already be running on a background thread anyway.

The last main issue I found was that the `ViewPeerActivity` didn't seem to be
working. After digging around in the layout resource I found that the labels for
each of the fields were setting their widths to `match_parent` and thus
completely obscured the text that should show up next to them. Changing the
widths on the labels to `wrap_content` easily solved this issue.

Other than those main issues work went smoothly.
