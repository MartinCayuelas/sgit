package fr.cayuelas.managers

import java.io.File


object FilesManager {

  /**
   * Functions that gives recursively all the files contained in a directory and his subdirectories
   * @param dir : directry name
   * @return a list of File of the given directory, could be empty if the directory is empty
   */

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) d.listFiles.filter(_.isFile).toList
    else List[File]()

  }

  /**
   * Functions that gives recursively all the files and directories contained in a directory and his subdirectories
   * @param dir :directry name
   * @return a list of files and directories of the given directory, could be empty if the directory is empty
   */
  def getListOfContentInDirectory(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory && !d.getName.equals(".sgit") && !d.getName.equals(".git")) d.listFiles.toList
     else List[File]()
  }

  /**
   * Deletes a file given is name
   * @param fileName : the name's file to delete
   */
  def deleteFile(fileName: String): Unit = {
    val file = new File(fileName)
    if (file.exists) file.delete()
  }
  
  /**
   * Function that creates a folder given a path (String)
   * @param pathNewFolder : the path of the new folder that will be created
   * @return true if it's created else false
   */
  def createNewFolder(pathNewFolder: String): Boolean = new File(pathNewFolder).mkdir()


  /**
   * Function that creates a file given a path (String)
   * @param pathNewFile : the path of the new file that will be created
   * @return true if it's created else false
   */
  def createNewFile(pathNewFile: String): Boolean = new File(pathNewFile).createNewFile()



}
