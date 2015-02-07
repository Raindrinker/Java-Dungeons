# Java-GameStuff
Various bits of code for games in Java

###LIST OF STUFFS

* **DungeonGenerator**

Creates a dungeon in the format of a 2D matrix where 0s are walls and 1s are walkable tiles.
Has various configuration values (size, number of rooms, room size, etc.).

![DungeonGenerator example](http://i.gyazo.com/e4281665a98b4df64da869207200eb30.png)
One result in a specific configuration, drawn using Java graphic library Slick2D

* **DungeonIlluminator**

Returns a 2D matrix of light values, given a 2D matrix of 0s and 1s where 0s are walls and an ArrayList of LightSources(custom class). Base light value can be configurated.

![DungeonIlluminator example](http://i.gyazo.com/140639406b0a434124da6e843d9bc943.gif)
With base light 0 and one source of intensity 20 that can be moved around, drawn using Java graphic library Slick2D

###CHANGELOG
* **07/02/15**

  * Added DungeonIlluminator
  * Added LightSource
  
* **05/02/15**
  
  * Created Repository
  * Added DungeonGenerator
