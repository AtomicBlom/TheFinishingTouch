The Finishing Touch
=

I'll describe the mod when I have more time :P



Submitting art to be included
-----------------------------

In order to have art be included in the mod, you can currently use two methods:

1) Open a PR with your art included.
   * Your art file will need to be added to [src/main/resources/assets/finishingtouch/textures/decals/](https://github.com/AtomicBlom/TheFinishingTouch/blob/master/src/main/resources/assets/finishingtouch/textures/decals/) you may add a subdirectory if you wish.
   * You will also need to add yourself to the decals.json file at 
[src/main/resources/assets/finishingtouch/textures/decals/decals.json](https://github.com/AtomicBlom/TheFinishingTouch/blob/master/src/main/resources/assets/finishingtouch/textures/decals/decals.json)

`decals.json` has the following structure:
```json
[
  {
    "Artist": "The name you wish to go by",
    "Site": "http://place.where.people.can.find.your.art/",
    "SiteName": "The a short name for the site that we'll show users",
    "Decals": [
      {
        "Name": "My Awesome Art",
        "Location": "finishingtouch:textures/decals/my_awesome_art"
      },
      {
        "Name": "My Next Best Art",
        "Location": "finishingtouch:textures/decals/my_next_best_art"
      }
    ]
  }
]
```

When specifying the location of the art, the path is relative to `src/main/resources/assets/finishingtouch`, so do not include that in the location.

2) If you are a contributor on the repository, you may add art at your own discretion.

3) Open a GitHub Issue.
    * Please attach your art directly into the issue and leave the following information:
        * Your artist name
        * The site people can look for more of your work (if you have one)
        * The name of the site, or your corner of it
        * The name of your artwork

Art specification
-----------------
There is no specific requirements for art, but I would recommend you opt for smaller images with a limited palette to fit with Minecraft's existing aesthetic.

Images with a non-power-of-two size, or non-square shape have not yet been tested, so it might be best for now to stick to 16x16, 32x32, 64x64, 128x128, etc.
 
Please keep the name of your art short if you can, we have limited space on the UI right now and although I will shrink 
the font if the text is too long, I may be forced to truncate the name and leave an ellipsis...  

Images must be child friendly. I do not currently have plans for having age filters in the mod.

Licensing
---------
### As an Artist

I'm not a legal person, and I don't want to make a big fuss over licensing, but I probably need this so here we go:
* I take no ownership of your art
* You allow The Finishing Touch to be a distribution mechanism for the art that your submit.
* You retain your normal licensing of your artwork, if this is important to you, please make sure it's available from 
  your site link.
* You may request the removal of your art from the project
  * I cannot remove your art from builds that people are using
  * It will be done as soon as I have time.
 * You allow players to use screenshots and videos that may include your art as per whatever Minecraft's normal 
   licensing for this stuff.


### As a player
* You cannot claim an artist's work as your own.
* Seriously. These people are amazing for contributing their stuff.
* Do awesome things with it.
