# Teamfight Tactics Stats

## What is TFT?
Teamfight Tactics is a free-for-all auto-battler video game. Your goal is to grow your team of champions and be the last player standing. For more information, see https://teamfighttactics.leagueoflegends.com/en-us/news/game-updates/what-is-teamfight-tactics/.

The gameplay is split into stages, each having seven rounds. Stage 1 is special in that there are only four rounds. 

## What is TFT Stats?
TFTStats is a statistics tracker that help players improve by using players' game results to display meaningful statistics for all areas of the game.

## Entering data
### How and when to enter data
TFTStats requires players to input the game state as it progresses. Since this is difficult to do in the middle of the game, the player only needs to screenshot their game at the start of a few rounds. In particular:
- Stage 1: screenshot at the start of round 2.
- Stage 2-4: screenshot at start of round 1, 2, and 4. The screenshot at round 1 will help you fill out the last bit of information from the previous stage.
- Stage 5+: screenshot at start of round 1 and 4.
When you die, it is unlikely you will have time to screenshot your final game state. Instead, go to https://lolchess.gg/leaderboards, select your region and input your username. Find your game and use it input your final game state into the app.

Here's a demo on how to input game results:

1. On the Home page, click the + button.
2. Your game has started. You have just picked an item from the carousel. Take a screenshot.(pictures: screenshot of 1-1 and 1-2)
3. Stage 2 round 1 (2-1) has started. Take a screenshot. Here's what stage 1 should look like: (screenshot of 2-1 and app. circle the numbers on the 2-1 screenshot and match the circle colours onto the app ss)
4. 2-2 has started. After you select an armory item, take a screenshot.
5. 2-4 has started. After you select a carousel item, take a screenshot.
6. 3-1 has started. Take a screenshot. Here's what stage 2 should look like:
7. This continues until 5-1. From 5-2 and onward, there is no armory item so you do not need to screenshot round 2.
8. 5-4 has started. After you select a carousel item, take a screenshot.
9. 6-1 has started. Take a screenshot. Here's what stage 5 should look like:
10. The game has ended and you have been eliminated. Go to https://lolchess.gg/leaderboards, input your username and find your game. Depending on when you died, you may or may not have to fill out certain fields. For example, if you died before round 4, you will not have to add carousel or pve items. Here's what your final stage should look like: (ss of ending screen)
   
### Functionality
To change the placement or level, click on the number to open a dropdown.
After you add an item, you can click on the item to edit it.
When adding a champion, type in their name to display some suggestions. If you do not select a valid champion, you will get an error.

### Error checking
- Health cannot exceed 100 or increase between stages.
- Level cannot decrease between stages.
- XP cannot exceed the maximum for a level. For example, level 3 xp 6 is not allowed, it should be level 4 xp 0.
- A champion cannot equip two copies of a unique item.
- A champion cannot equip a spat item of an origin that they already are. For example, Katarina, a Forgotten and Assassin, cannot equip a Forgotten or Assassin spat.

## Viewing statistics
When you click the top left hamburger, you will see some statistics pages.

### Home
ss
You will see a list of your last 10 games. Each game will show your final placement and your final team comp, along with each champion's items. To view more information about a game, you can tap anywhere on the game's row (ss). 
Then you will see the items you got from armory, carousel, and pve. You can also see the progression of gold, health, level, and placements. ss

### Gold, Health, Level, and Placement
ss
These pages will show you the average gold/health/level/placement per stage, separated by the end game placement. For example, suppose you had three games, all that only lasted two stages:
- Game 1: stage 1 had 1 gold, stage 2 had 5 gold, and you placed 2nd.
- Game 2: stage 1 had 5 gold, stage 2 had 3 gold, and you placed 2nd.
- Game 3: stage 1 had 1 gold, stage 2 had 1 gold, and you played 8th.
Then the red line will show 3 gold at stage 1 and 4 gold at stage 2. The purple line will show 0 gold at stage 1 and 0 gold at stage 2.
The average line shows overall average (aka merging all the placements). In this case, it will show 2.33 gold at stage 1 and 3 gold at stage 2.
You can use the checkboxes to hide certain lines. If a line doesn't appear even when the box is checked, then you have no games where you ended at that placement. The bottom of the graph shows all possible lines. ss

### Item Stats
This page will help you learn which items get you the best results.
Under "Average Placement by Item", you can enter an item name and view your average final placement when you got that item at a specific stage. For example, in the following screenshot, my average final placement is 2.5 when I got a Tear of the Goddess from stage 4 armory (aka 4-2). sss
Under "Best Items", you can view the items that give the highest average final placement. For example, in the following screenshot, when I get a Tear of the Goddess from stage 4 armory I have the highest average placement compared to all other items. 

### Final Comp Stats
This page will help you learn which champions perform the best and which team compositions you are successful with.
Under "Average Placement by Trait", you can enter a trait and view your average final placement when your team composition has that trait. It is also divided by trait levels. ss
Under "Best Final Traits", you can view the traits that give the highest average final placement. These are sorted by best placement, count, and the trait level.
Under "Average Placement by Champion", you can enter a champion and view your average final placement when the champion is in your final team. The items that are most successful on the champion are shown. ss
Under "Best Final Champions", you can view the champions that give the highest average final placement. These are sorted by placement, count, star level, carry percentage, and number of items.

## Coming up
