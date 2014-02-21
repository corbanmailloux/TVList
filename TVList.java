import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import javax.swing.JOptionPane;

/**
 * TVList.java
 *
 * File:
 *  $Id$
 *
 * Revisions:
 *  $Log$
 * 
 */

/**
 * A new version of TVList, using Java 1.7.
 * 
 * @author Corban Mailloux <corb@corb.co>
 */
public class TVList extends Observable {

  /**
   * The list of folders to search through
   */
  private static List<File> folderList = new ArrayList<File>();

  /**
   * The complete TVList
   */
  private static List<TV> TVList = new ArrayList<TV>();

  /**
   * The TVList that represents the current state
   */
  private static List<TV> currTVList = new ArrayList<TV>();

  /**
   * The list of duplicates.
   */
  private static List<TV> dupeList = new ArrayList<TV>();

  public TVList(List<File> inFolderList) {

    folderList = inFolderList;

    // For each folder
    for (File folder : folderList) {
      // If the folder doesn't exist, skip it
      if (!folder.exists() || !folder.isDirectory()) {
        continue;
      }

      // For each movie in this folder
      for (File series : folder.listFiles()) {
        if (series.isDirectory()) {
          // Add it to the TVList
          TVList.add(new TV(series));
        }
      }
    }

    if (TVList.isEmpty()) {
      JOptionPane
          .showMessageDialog(null,
              "No TV folders found. Please verify your \"TVList.properties\" file.");
    }

    // Sort the movie list
    Collections.sort(TVList);

    // Find duplicates
    for (TV TV1 : TVList) {
      if (Collections.frequency(TVList, TV1) > 1) {
        dupeList.add(TV1);
      }
    }

    // Set up the current TVList
    currTVList.addAll(TVList);
  }

  /**
   * A forced call to update.
   */
  public void update() {
    setChanged();
    notifyObservers();
  }

  /**
   * Set the current list to the dupeList
   */
  public void dupes() {
    currTVList.clear();
    currTVList.addAll(dupeList);
    setChanged();
    notifyObservers();
  }

  /**
   * Open a given TV in Windows Explorer.
   * 
   * @param m1
   *          - the TV to open and select
   */
  public void openExplorer(TV m1) {
    try {
      Runtime.getRuntime().exec("explorer /select, \"" + m1.getPath() + "\"");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Return the current TVList. Called by Observer's update.
   * 
   * @return - current TVList
   */
  public List<TV> getTVList() {
    return currTVList;
  }

  /**
   * Non-case-sensitive search of the current TV list for a string
   * 
   * @param searchStr
   *          - the string to search for in movie titles
   */
  public void search(String searchStr) {
    currTVList.clear();
    for (TV m1 : TVList) {
      if (m1.getName().toLowerCase().contains(searchStr.toLowerCase())) {
        currTVList.add(m1);
      }
    }
    setChanged();
    notifyObservers();
  }
}
