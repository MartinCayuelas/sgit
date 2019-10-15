package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.managers.IOManager
import fr.cayuelas.objects.{Tree, Wrapper}

import scala.annotation.tailrec

object HelperCommit {

  def currentRefs : String = HelperPaths.branchesPath + File.separator + Branch_cmd.getCurrentBranch
  def commitsPath: String = HelperPaths.objectsPath+File.separator+"commits"
  def treesPath: String = HelperPaths.objectsPath+File.separator+"trees"
  /*
  PART CREATION OF TREES
   */
  /**
   * FMethod that creates all the trees for the commit with the non root files
   * @param l : list to use
   * @param WrapperWithHashFinal : Potential final hash for the last tree in a list to have it every step
   * @return a list containing the final hash and the final path
   */

  @tailrec
  def createAllTrees(l: List[Wrapper], WrapperWithHashFinal: Option[List[Wrapper]]): List[Wrapper] = {
    if (l.isEmpty) WrapperWithHashFinal.get
    else {
      val (deeper, rest, father, oldPath) = getDeeperDirectory(l)
      val hash: String = Tree.createTree(Some(deeper), None)
      if (father.isEmpty) {
        if (WrapperWithHashFinal.isEmpty) createAllTrees(rest, Some(List(Wrapper(deeper(0).path, hash, "Tree", oldPath.get))))
        else createAllTrees(rest, Some(Wrapper(deeper(0).path, hash, "Tree", oldPath.get) :: WrapperWithHashFinal.get))
      }
      else createAllTrees(Wrapper(father.get, hash, "Tree", oldPath.get) :: rest, WrapperWithHashFinal)
    }
  }

  /**
   * Method that retrieves the deepest directory
   * @param l : list on which we do action
   * @return a tuple4 containing the dispest path, the rest of the orthers paths, the parent path of the dispest direcoty and itself path
   */
  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String], Option[String]) = {

    val pathMax = getPathMax(l,"",0)
    val rest: List[Wrapper] = l.filter(x => !x.path.equals(pathMax))
    val deepest: List[Wrapper]  = l.filter(x => x.path.equals(pathMax))
    val (fatherPath, oldPath) = getParentPath(pathMax)

    (deepest, rest, fatherPath, oldPath)
  }

  /**
   * Method that retrieves the path max in a given list
   * @param l : list that we look at
   * @param pathMax : longest path
   * @param max : Accumulator
   * @return the path max of e given list
   */
  @tailrec
  def getPathMax(l: List[Wrapper], pathMax: String, max: Int): String = {
    if(l.isEmpty) pathMax
    else {
      if (l.head.path.split("/").length >= max) getPathMax(l.tail,l.head.path,l.head.path.split("/").length)
      else getPathMax(l.tail,pathMax,max)
    }
  }

  /**
   * Method the retrieves the parent of a givent path
   * @param path : the path to be transformed
   * @return the parent path of the given path and itself
   */
  def getParentPath(path: String): (Option[String], Option[String]) = {
    val pathSplited: List[String] = path.split("/").toList
    if (pathSplited.length <= 1) (None, Some(path))
    else {
      val parentPath = pathSplited.init.map(x => x+File.separator).mkString.dropRight(1)
      (Some(parentPath), Some(path))
    }
  }


  /*
  PART COMMIT TOOLS
   */

  //get in refs/heads/branch
  def get_last_commitInRefs(): String = {
    IOManager.readInFile(currentRefs)
  }

  def isFirstCommit: Boolean = {
    IOManager.readInFile(currentRefs).length == 0
  }
  /**
   *
   * @param hashCommit : string corresponding to the commit we want to retrieve
   * @return
   */
  def getAllBlobsFromCommit(hashCommit: String): List[(String,String)] ={
    val firstTree = getFirstTreeFromCommit(hashCommit)
    val blobs:List[(String,String)] = accumulatorBlobs(firstTree)
    blobs
  }

  /**
   *
   * @param hashTree
   * @return
   */
  def accumulatorBlobs(hashTree: String): List[(String,String)] ={
    val(newAcc,treesList) = retrievesBlobsAndTreesInTree(hashTree,"")
    recursiveRetrievesBlobsInTrees(treesList,newAcc)
  }

  /**
   *
   * @param hashTree
   * @return
   */
  def retrievesBlobsAndTreesInTree(hashTree: String, pathParent: String):  (List[(String,String)],List[(String,String)]) = {
    val (folder,file) = HelperPaths.getFolderAndFileWithSha1(hashTree)
    val path = treesPath + File.separator + folder + File.separator  +file
    val contentOfTree = IOManager.readInFileAsLine(path)

    val blobsSplited = contentOfTree.filter(x => x.startsWith("Blob")).map(b => b.split(" "))
    val listHashesAndPaths : List[(String,String)]= blobsSplited.map(x => (x(1),pathParent+File.separator+x(2)))

    val newAcc = accumulate(listHashesAndPaths,List())
    val treesInTree = contentOfTree.filter(x => x.startsWith("Tree")).map(x => x.split(" ")).map(x => (x(1),x(2)))

    (newAcc,treesInTree)
  }

  /**
   *
   * @param listTrees
   * @param accBlobs
   * @return
   */
  @tailrec
  def recursiveRetrievesBlobsInTrees(listTrees: List[(String,String)], accBlobs: List[(String,String)] ): List[(String,String)] = {
    if(listTrees.isEmpty) accBlobs
    else{
      val(newAcc,treesList) = retrievesBlobsAndTreesInTree(listTrees.head._1,listTrees.head._2)
      val newListOfTrees = treesList++listTrees.tail
      recursiveRetrievesBlobsInTrees(newListOfTrees,newAcc)

    }
  }

  /**
   *
   * @param hashCommit
   * @return
   */
  def getFirstTreeFromCommit(hashCommit: String): String ={
    val (folder,file) = HelperPaths.getFolderAndFileWithSha1(hashCommit)
    val firstLine = IOManager.readInFileAsLine(HelperPaths.objectsPath+File.separator+"commits"+File.separator+folder+File.separator+file)(0)
    firstLine.split(" ")(1)
  }

  /**
   *
   * @param newBlobs
   * @param acc
   * @return
   */
  @tailrec
  def accumulate(newBlobs: List[(String,String)], acc: List[(String,String)]  ): List[(String,String)] ={
    if (newBlobs.isEmpty) acc
    else {
      accumulate(newBlobs.tail,newBlobs.head::acc)
    }
  }



}
