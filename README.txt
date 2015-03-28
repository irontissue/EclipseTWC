-----------------------------------------ECLIPSE - THE WAXING CRESCENT-----------------------------------------
-----------------------------------------------README/HELP FILE------------------------------------------------
(Turn word wrap off and make the window as wide as possible for better viewing.)

Required Software:
	Java (version 7 - required to play the game)
	Netbeans (version 7.3.1 - not required to play the game, but allows for easier viewing of the game's code)

To run the game:
	1) Copy the entire "EclipseTWC Game" folder onto your desktop (This README file should be in that folder, in fact).
	2) Open the folder and double click on the "EclipseTWC Installer" file. Allow the package to install.
	3) Simply double click on the "EclipseTWC.jar" file to play the game.
	3) To run the Level Creator that is included in the installer, double click on the "EclipseTWC Level Creator.jar" file.
	4) If you would like to view the code, download the "EclipseTWC" folder and open Netbeans 7.3.1. Then, open that
		folder through Netbeans.

	After opening the game, click on one of the four options on the main menu:

		NEW GAME: Start a new game by clicking on a file.
		LOAD GAME: Load from an existing game by clicking on a file.
		CREDITS: See who made the game.
		EXIT: Exit from the game.

	If you choose to make a new game or load a game, you will then be prompted to chose the file you want to make a new game in (or load).
	There are only three files to choose from. If you would like to delete a file, then simply start a new game in that file.
	NOTE: If you start a new game and do not save, all of your progress will be lost and you will be forced to start from scratch.

	When you start the game, you will start in a tutorial level. You will be guided through the levels by several people, and in the process,
	learn the game's controls.

To view the source code:
	The source code is in the src/EclipseTWC folder. An easy way to view it is to open up the entire project in netbeans (See "To run the game" above).

The compiled classes can be located in build/classes/moving folder.

The JavaDocs (a hyptertext structure of the project organization [classes, methods, etc]) is located in the "EclipseTWC/javadocs" folder.
To run the JavaDocs, double click on index.html.




“STATS” AND WHAT THEY MEAN:


In game, you can use “level up points” (you get four level up points every time you level up) to upgrade some of your stats:

	

STAMINA = how much health you have. Each point in stamina gives 19 more maximum health.
	
POTENTIAL = how much energy you have. Each point in potential gives 12 more maximum energy.
	
DEFENSE = Damage reduction. Each point in defense gives you 0.8% damage reduction.
	
SPEED = How fast you move and how fast you attack. Each point in speed gives you 0.05 moveSpeed and 0.001 acceleration, and 0.07 attack speed.
	
STRENGTH = How potent your attacks are. Each point in strength increases damage done by your attacks by a factor of 0.015.
	
INTELLIGENCE = How fast you regenerate health/energy. Each point in intelligence gives 0.05% health regen and 0.032% energy regen.
	
LUCK = How often you get a critical hit, and how potent they are. Each point in luck gives 0.18% extra chance to crit, and 2% extra crit damage.



Each stat has a “cap” (a limit) of 50 points.
The following table shows the potential values that each stat can become at level 50, and their real-time benefits:





		at 0 points……													at 50 points……

STAMINA		50 maximum health												1000 maximum health

POTENTIAL	30 maximum energy												630 maximum energy

DEFENSE		0% damage reduction												40% damage reduction.

SPEED		2 pixels/frame movement speed, 0.16 pixels/frame^2 x-acceleration, and 1 attack per second.			4.5 movement speed, 0.21 acceleration, 4.5 attacks/second.

STRENGTH	Each attack does 75% damage.											Each attack does 150% damage.

INTELLIGENCE	0.5% of your health regenerates per second, and 0.4% of your energy regenerates every second.			3% health regen, 2% energy regen.

LUCK		There is a 1% chance for you to have a critical strike, and a critical strike deals 150% damage.		10% chance of critical strike, and a critical strike deals 250% damage.





As you can see, the balance between these stats is very delicate. It took quite a while to deal with issues regarding how strong the character becomes

and how well it balances in different stages of the game. (HINT: Stamina, speed, and intelligence are probably the most important stats to max!)