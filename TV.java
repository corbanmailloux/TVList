import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

/**
 * TV.java
 *
 * File:
 *  $Id$
 *
 * Revisions:
 *  $Log$
 * 
 */

/**
 * A TV object for use with TVList
 * 
 * @author Corban Mailloux <corb@corb.co>
 */
public class TV implements Comparable<TV> {

  /**
   * File object representing this series' top level folder
   */
  private File fileObject;

  /**
   * The series' folder's name
   */
  private String fileName;

  /**
   * The newest episode in the series.
   */
  private String newestEpisodeNum;

  /**
   * The string of the file extensions in the series.
   */
  private String fileExts;

  /**
   * The constructor for a TV
   * 
   * @param inFile
   *          - a File representing the TV
   */
  public TV(File inFile) {
    fileObject = inFile;
    fileName = inFile.getName();
    newestEpisodeNum = "Not available.";
    fileExts = "Not available.";

    // Find the newest episode, and the extension types.
    // Get highest season folder
    Set<String> fileExtsSet = new HashSet<String>();
    File[] seasons = fileObject.listFiles();
    File highestSeason = null;
    int highestSeasonNum = 0;
    int highestEpisodeNum = 0;
    for (File season : seasons) {
      // If the folder doesn't exist, is a file, or doesn't contain "Season",
      // skip it.
      if (!season.exists() || !season.isDirectory()
          || !season.getName().contains("Season")) {
        continue;
      }

      File[] extEpisodes = season.listFiles();
      for (File extEpisode : extEpisodes) {
        int i = extEpisode.getName().lastIndexOf('.');
        if (i >= 0) {
          fileExtsSet.add(extEpisode.getName().toUpperCase().substring(i + 1));
        }
      }

      // Try to get the int value of the season
      try {
        int currSeasonNum =
            Integer.parseInt(season.getName().substring(7,
                season.getName().length()));
        // Is it the highest?
        if (currSeasonNum > highestSeasonNum) {
          highestSeasonNum = currSeasonNum;
          highestSeason = season;
        }
        // Any folder errors pop-up a message.
      } catch (Exception e) {
        JOptionPane
            .showMessageDialog(null, "Folder error in \"" + fileName
                + "\" at folder \"" + season.getName()
                + "\". Please check naming.");
        // Get out of here.
        System.exit(1);
      }
    }
    // Find the highest episode in the season's folder.
    if (highestSeason != null) {
      File[] episodes = highestSeason.listFiles();
      for (File episode : episodes) {
        // If the episode doesn't exist or is a folder, skip it.
        if (!episode.exists() || episode.isDirectory()) {
          continue;
        }
        // Find the index of the "S" in "SxxExx" of the filename.
        int seasonIndex =
            episode.getName().indexOf(
                "S" + String.format("%02d", highestSeasonNum) + "E");
        if (seasonIndex > -1) {
          int currEpisodeNum =
              Integer.parseInt(episode.getName().substring(seasonIndex + 4,
                  seasonIndex + 6));
          if (currEpisodeNum > highestEpisodeNum) {
            highestEpisodeNum = currEpisodeNum;
          }
        }
      }
    }

    if (highestSeasonNum != 0 && highestEpisodeNum != 0) {
      newestEpisodeNum =
          "S" + String.format("%02d", highestSeasonNum) + "E"
              + String.format("%02d", highestEpisodeNum);
    }

    if (fileExtsSet.size() > 0) {
      fileExts = fileExtsSet.toString();
    }

  }

  /**
   * The basic toString() method.
   * 
   * @return - a String with the name and path
   */
  @Override
  public String toString() {
    String returnStr = "";
    returnStr += "Name: " + fileName + "\n";
    returnStr += "Path: " + fileObject.getAbsolutePath() + "\n";
    returnStr += "Newest Episode: " + newestEpisodeNum + "\n";
    returnStr += "File Extensions: " + fileExts + "\n";
    return returnStr;
  }

  /**
   * Return the TV's name
   * 
   * @return - the filename
   */
  public String getName() {
    return fileName;
  }

  /**
   * Return the full path to the TV
   * 
   * @return - the path to the TV
   */
  public String getPath() {
    return fileObject.getAbsolutePath();
  }

  /**
   * Compare this TV to another TV, based only on name, ignoring case
   * 
   * @return - -1 if this < m1; 0 if this == m1; 1 if this > m1
   */
  @Override
  public int compareTo(TV m1) {
    return fileName.compareToIgnoreCase(m1.getName());
  }

  /**
   * Compare this TV to another TV, based only on name, ignoring case
   * 
   * @return - true iff the name's are the same, ignoring case
   */
  @Override
  public boolean equals(Object m1) {
    if (m1 instanceof TV) {
      return fileName.equalsIgnoreCase((((TV) m1).getName()));
    }
    return false;
  }

  /**
   * Computes a hashCode for a TV. Uses File's hashCode()
   * 
   * @return - the hashCode
   */
  @Override
  public int hashCode() {
    return fileObject.hashCode();
  }
}
