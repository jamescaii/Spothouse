# cs0320 Term Project 2021

**Team Members:** 

James Cai, Juho Choi, Richard Durkee, and Michael Li

**Team Strengths and Weaknesses:**

James
- Strengths: back-end development, integration, communication
- Weaknesses: Front end development, web servers, limited experience with using APIs

Juho
- Strengths: experience with databases (both relational and non-relational), and data science related tools (web scraping, map reduce, data visualization)
- Weaknesses: systems-related concepts (low-level), slow coder.

Richard
- Strengths: bottom-up research, communication, open-mindedness, creative problem solving
- Weaknesses: front-end development, Javascript, no experience using APIs or scripting, sometimes bite off more than I can chew.

Michael
- Strengths: debugging and logic stuff, attention to detail
- Weaknesses: most front-end stuff and javascript, i like grinding stuff out in small amounts of time rather than working over a long time

**Project Idea(s):**
### Idea 1

***Spothouse***

A website in which a host, using Spotify, can create a room to listen to music with friends.

****Problem and Solution****

Spotify's Group Session feature is terrible and requires every user to have Spotify. Instead, using Spothouse (inspired by Clubhose)'s website, a host can create a virtual room to listen to music with others. 

The idea started from an observation of the pain-staking process of controlling the Aux (the device connected to the main auxiliary cable) by hosts, whether in parties or home invites. Hosts can now honor (or dishonor) the song requests without verbal requests, without hindering the conversations or the fun.

As we see the explosive growth of Clubhouse this year, we project that music sharing service can also be a huge platform at this era of socially distanced world. If we can extend the platform to a full functional application, we may even build a legitimate virtual room in which a central music audio is played while chatting and typing messages are enabled for the users (it could be a great platform for celebrities, too, just how TikTok became a great marketing platform for celebrities).

****Features****

But at the scope of this courses' project, we aim to build the platform as below:

1. successful and efficient connection to Spotify API to enable searching
2. a webpage enables for updating the playlist queue
3. the host logs in with the Spotify account, and users will be able to add/request songs.
3. (potentially) two settings for updating the queue:
- guests in the room can either directly add songs to the playlist.
- guests make requests, and host can select from the requested songs to the playlist.

Our main difficulties would lie on making effective and ui/ux friendly playlist.
- how can host manage the playlist efficiently during big or home parties?
- simple autoplay, or more complex algorithm to determine what songs should be autoplayed?
- how do we make sure that added songs are agreeable by the majority of the users? 
- do we need to create a voting system, or do we rely on host's capabilities? 
- should we mark some users as influencers, who suggest highly-voted-for songs?
- should we save the playlist after the occasion is over to recommend better songs next time at a given mood/occasion for a party, using how many votes each song got?

TA Approval (dlichen): This idea is really cool, but it has no algorithmic complexity. If you could create an algorithmic component that the application focuses on it could work, but currently denied. 

***EDIT: Algorithmic Complexity

Our ideas regarding algorithmic complexity centers around building the most user-friendly playlist: it would be appreciated to know how much is good enough, either a single idea or an accumulation of these.

1. When new songs are requested to the queue, the host can see the songs in an order based on the votes by other users in the virtual room, as well as the similarity of the song with the previously played songs, as to "keep the vibe." This metric would require some kind of complex algorithm to compare songs: or we can apply a recommender system to make a similarity matrix, which would keep getting updated as new songs are added.
2. Along those lines, when the room is created, the playlist is first made by the host or left blank to be filled up by the users. In this case, we also want to "recommend" the musics as well as the order of the music based on people's suggestions, what they have liked before, what they have voted for before, and so on. This can also use some kind of recommender matrix or another complex data structure.
3. Since users' votes are used as a metric, it's possible that some users can become more influential in deciding the songs. We thought we could apply an Elo rating system for these users so we can get better playlist. If the Elo rating system tends to create bias, then we also create another metric to reward the users whose requests have not been voted as much, and take their requests as priority before determining the order,.
4. When the queue is almost ending, we can suggest more songs based on these similarity metrics, and people can vote among these suggestions, and these votes will also then be applied to further suggestions after this.


**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 15)_

**4-Way Checkpoint:** _(Schedule for on or before April 5)_

**Adversary Checkpoint:** _(Schedule for on or before April 12 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
