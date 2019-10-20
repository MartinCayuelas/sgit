package fr.cayuelas.objects


import java.io.File

import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IoManager}

case class Tree( contentTree: List[Wrapper] = List.empty, id: String = "") {

  //Path to folder of trees
  def treesPath: String = HelperPaths.objectsPath + File.separator + "trees"

  /**Function that creates the id of a tree given his content
   *
   * @param contentTree : content of the tree
   * @return tree's id string digested by sha1
   */
  def createTreeId(contentTree: List[Wrapper]): String = {
    val content = contentTree.map(x => x.typeElement+ " " + x.hash+" "+ x.name+ "\n").mkString
    HelperSha1.convertToSha1(content)
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
    contentTree.map(elem => IoManager.writeInFile(pathFile,elem.typeElement+ " " + elem.hash+" "+ elem.name+ "\n",append = true)) //WriteInTree
  }

  /**
   * Function that clear all the content of a tree file
   * @param folder : tree's name folder
   * @param nameFile : tree's name file
   */
  def clearTreefile(folder: String, nameFile: String): Unit = IoManager.clearFile(treesPath + File.separator + folder + File.separator + nameFile)

  /**
   * Function thats accumulated all the content of a given list
   * @param listA : list to get elements
   * @param accumulator: list of all elements accumulated
   * @return a list of of all elment of the given list accumulated in an Option or NOne
   */
  def accumulateContentTree(listA: Option[List[Wrapper]], accumulator: Option[List[Wrapper]]): Option[List[Wrapper]]= {
    listA match {
      case None => accumulator
      case Some(s) => {
        if (listA.get.nonEmpty) accumulateContentTree(Some(listA.get.tail), Some(listA.get.head::accumulator.get))
        else accumulator
      }
    }
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

    val nonRootFilesContentAccumulated : Option[List[Wrapper]] = tree.accumulateContentTree(nonRootFiles,Some(List()))
    val rootFilesContentAccumulated: Option[List[Wrapper]] = tree.accumulateContentTree(rootFiles,Some(List()))

    val newContentTree : List[Wrapper] = nonRootFilesContentAccumulated.getOrElse(List())++rootFilesContentAccumulated.getOrElse(List())
    val hash : String = tree.createTreeId(newContentTree)

    val treeCopy: Tree = tree.copy(id = hash, contentTree = newContentTree)
    treeCopy.saveTreeInObjects(treeCopy.id, treeCopy.contentTree) //Save in objects
    treeCopy.id
  }
}


