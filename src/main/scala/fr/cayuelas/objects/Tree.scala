package fr.cayuelas.objects


import java.io.{File, PrintWriter}

import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IOManager}

case class Tree(var contentTree: List[Wrapper] = List.empty, var id: String = "") {

  //Path to folder of trees
  val treesPath: String = HelperPaths.objectsPath + File.separator + "trees"

  /**
   *Method that add an element to a content list
   * @param elem : element that will be added to the content lits
   * @return a new List[Wrapper] with the old content + a new element in
   */
  def addElement(elem: Wrapper): List[Wrapper] = {
    elem :: this.contentTree
  }

  /**Method that creates the id of a tree given his content
   *
   * @param contentTree : content of the tree
   * @return tree's id string digested by sha1
   */
  def createTreeId(contentTree: List[Wrapper]): String = {
    val content = treeContent(contentTree)
    HelperSha1.convertToSha1(content)
  }

  /**
   * Method tha creates a string for the file of the tree
   * @param contentTree : content of the tree
   * @return the string that will be stored in the file in .sgit/objects/trees
   */

  def treeContent(contentTree: List[Wrapper]): String = {
    var acc = ""
    contentTree.map(x => acc = acc + clearStar(x.typeElement)+ " " + x.hash+" "+ x.path+ "\n")
    acc
  }

  /**
   * Helper that clears the start at the beginning
   * @param stringToClear : the string with a *
   * @return the string without the *
   */
  def clearStar(stringToClear: String): String = {
    if(stringToClear.startsWith("*")) stringToClear.substring(1,stringToClear.length)
    else stringToClear
  }

  /**
   * Function that creates folder and file for the tree in .sgit/objects/trees
   * @param idSha1 : tree's id
   * @param contentTree: tree's content
   */

  def saveTreeInObjects(idSha1: String, contentTree: List[Wrapper]): Unit = {

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    val pathFile = treesPath + File.separator +  folder + File.separator + nameFile

    FilesManager.createNewFolder(treesPath + File.separator +  folder)
    FilesManager.createNewFile(pathFile)

    clearTreefile(folder,nameFile)
    IOManager.writeInFile(pathFile,treeContent(contentTree),append = true)//WriteInTree

  }

  /**
   * Function that clear all the content of a tree file
   * @param folder : tree's name folder
   * @param nameFile : tree's name file
   */
  def clearTreefile(folder: String, nameFile: String): Unit = {
    val path = treesPath + File.separator + folder + File.separator + nameFile
    //Clean the file
    val writer = new PrintWriter(path)
    writer.print("")
    writer.close()
  }

}

object Tree {

  /**
   * Function that process the entire creation of a tree using others methos and funcitons of the class
   * @param nonRootFiles : Files that are in subdirectories
   * @param rootFiles : Files that are in the same directory as .sgit
   * @return the id of the tree created
   */
  def createTree(nonRootFiles: Option[List[Wrapper]], rootFiles: Option[List[Wrapper]]): String = {
    val tree = new Tree()
    var contentTreeH: List[Wrapper] = List().empty

    if (nonRootFiles.isDefined) nonRootFiles.get.map(element => {
      val nTree: Tree = tree.copy(contentTree = tree.addElement(element))
      contentTreeH = nTree.contentTree ++ contentTreeH //accumulation of the content
    })

    if (rootFiles.isDefined) rootFiles.get.map(element => {
      val nTree: Tree = tree.copy(contentTree = tree.addElement(element))
      contentTreeH = nTree.contentTree ++ contentTreeH //accumulation of the content
    })

    val hash = tree.createTreeId(contentTreeH)

    val treeCopy = tree.copy(id = hash, contentTree = contentTreeH)
    treeCopy.saveTreeInObjects(treeCopy.id, treeCopy.contentTree) //Save in objects
    treeCopy.id
  }
}


