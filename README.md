# Word Train
This was my high school senior year prom asking :)

Basically, it's a word game in which the player and the computer take turns changing one letter from the previous word to make a new word. 
For example: made->make->fake->and so on. I never told her the purpose of the game, 
which is for the computer to lead the player to the word "prom". 
Once the word "prom" is written by either the player or the computer, the computer asks the question: "yes or no?" and the player
wins by typing yes

How it works:
Originally, the word is 5 words away from "prom".
Each turn, the computer simply types in a word that is one path length closer to the word prom, 
and unless the player is deliberately avoiding "prom" the program will get to the word relatively quickly. 
The computer also cheats by disallowing uncommon four letter words that will take the player farther from "prom", 
but will use/allow uncommon four letter words if they are closer to "prom". The words and path lengths to "prom" were 
calculated beforehand with Dijkstra's and stored in an sql database packaged within the app.
