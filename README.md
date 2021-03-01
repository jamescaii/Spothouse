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

### Idea 2

***PizzaParty***

****Problem and Solution****

Figuring out how much pizza to order, and which kinds, can be a real hassle. With PizzaParty, the host can input a budget and a list of invitees who will be able to enter their taste preferences, how many slices they think they could eat, any allergies they may have, as well as any drinks or sides they want. This would save time for the host and the pizza parlor, as well as making sure that everyone’s voice gets heard in the ordering process. No more trying to memorize what everyone wants, or awkwardly trying to parse through everyone’s orders in a group chat. 

****Features****

PizzaParty will be responsible for figuring out how to best fulfill everyone’s desires within the budget (if one is provided), and make sure that everyone has enough to eat. This constraint satisfaction won’t be as simple as it sounds -- ideally, the program would be able to solve “close enough” situations, where if someone wants three slices of pineapple and bacon pizza, and other person wants one slice of bacon and ham pizza, the program would know to order a half Hawaiian pizza; unless, of course, one of those two put “pineapple” on their “dislikes” list.

The beauty of this program is that it could store the user’s preferences for next time, as well as past orders for the hosts. That way, if you’re attending a PizzaParty with some new friends, you can simply select “use preset preferences” and no further input will be required! 

If this were to be made into a real application, we think it would be best if the program could have the option to split the check in different ways, evenly between each person, or based on what each person ordered. This is probably outside the scope of the class, but a proof of concept could be made for this. 

### Idea 3

***BrownTime***

****Problem and Solution****

In Korea there's an online platform called Everytime (https://everytime.kr/). About 3.5 million college students in Korea (most of the colleges in the country) use this app. Basically, in terms of Brown, Everytime is a combination of C@B, the Critical Review, Brown's Google Calendar, bits of Canvas (not the full functionality), Dear Blueno, Brown Stank Stash of Memes, Brown Buying and Selling, Brown Textbook Exchange, (and all other Brown Facebook groups you can think of), the Blognonian, and more. In short, Everytime is 'the' go-to place for all school-related groups, websites, and online resources, operating both as an educational platform and a social network. 

Everytime provides a distinct server for each university, and what's awesome about this platform is that you are:
1. required to enter your school credential to access the school resources (which the company contacts each school to provide as much as they can do)
2. but at the same "completely" anonymous so that forums like Dear Blueno or the Critical Review are run simultaneously. 

The most awesome part of Everytime is that the Critical Review-like functionality operates in an Amazon-like format. Basically, instead of writing a very vague questionnaire at the end of the semester, which was even hand-written before COVID, students give a 5-star based "review" of the course and leave comments about the course, the professor, etc. 

We think that, through 32, it could be a good chance to build a very basic prototype of such platform for Brown students. Focusing on building a better Critical Review is worth a try by itself. 

****Features****

1. getting the APIs and resources for CAB (and talking with a somewhat unresponsive Registrar office of ours :) )
2. build and load review page for each class
3. functionality to make new comments (or comments to comments)
4. update course average on every new comment

**Mentor TA:** _Put your mentor TA's name and email here once you're assigned one!_

## Meetings
_On your first meeting with your mentor TA, you should plan dates for at least the following meetings:_

**Specs, Mockup, and Design Meeting:** _(Schedule for on or before March 15)_

**4-Way Checkpoint:** _(Schedule for on or before April 5)_

**Adversary Checkpoint:** _(Schedule for on or before April 12 once you are assigned an adversary TA)_

## How to Build and Run
_A necessary part of any README!_
