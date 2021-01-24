TVList
=========

TVList is a simple Java 1.7 media manager for TV collections that span multiple drives or folders.

It is based on the same code as [MovieList](https://github.com/corbanmailloux/MovieList), but it is modified to work for a specific folder and file name structure.

TVList allows a user to quickly search through a large TV collection that may span multiple drives or folders. It does this by quickly looking through each folder given to it, indexing the folders (TV shows), checking the internal season folders, finding the highest numbered episode, and keeping an organized list of all of the information found.

It does real-time searching, lists all of the file types in a given show, and has the ability to open a TV show folder in Windows Explorer or even list duplicate entries.

TVList expects the following folder structure in each of the folders specified in the TVList.properties file. In a future version, I hope to make this configurable, but it isn't currently:

* TV Show Name
  * Season XX
    * TV Show Name - SXXEYY - Episode Name.ZZZ
