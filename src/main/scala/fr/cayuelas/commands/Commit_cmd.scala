package fr.cayuelas.commands


import java.io.File

import fr.cayuelas.managers.{IOManager, StageManager}
import fr.cayuelas.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec


object Commit_cmd {

  /**
   * Main function that process the commit.
   * Checks if it is possible to commit.
   * If yes, then retrieves all files in root et in subdirectories and call a method to creates all the trees. Then th Commit class do the commit
   * If no, the user is informed that there is nothing to commit
   */

  def commit(): Unit = {
    if (!StageManager.canCommit) IOManager.nothingToCommit()
    else {
      val stage: List[Wrapper] = StageManager.retrieveStageCommitStatus()
      val blobsInRoot: List[Wrapper] = StageManager.retrieveStageCommitRootBlobs()

      val hashFinalGhostTree: String = stage.nonEmpty match {
        case true => Tree.createTree(Some(createAllTrees(stage, None)), Some(blobsInRoot))
        case false => Tree.createTree(Some(List()), Some(blobsInRoot))
      }

      //Creation and process about a Commit object (class)
      Commit.commit(hashFinalGhostTree)
    }
  }

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
   *
   * @param l
   * @return
   */
  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String], Option[String]) = {
    /*val (_,pathMax)=getMaxAndPathForMax(l,0,"")
    val rest: List[Wrapper] = l.filter(x => !x.path.equals(pathMax))
    val deepest: List[Wrapper] = l.filter(x => x.path.equals(pathMax))
    val (fatherPath, oldPath) = getParentPath(pathMax)

    (deepest, rest, fatherPath, oldPath)*/

    var max: Int = 0
    var pathForMax: String = ""

    l.map(line => if (line.path.split("/").size >= max) {
      max = line.path.split("/").size
      pathForMax = line.path
    })

    val rest: List[Wrapper] = l.filter(x => !x.path.equals(pathForMax))
    val deepest: List[Wrapper]  = l.filter(x => x.path.equals(pathForMax))
    val (fatherPath, oldPath) = getParentPath(pathForMax)

    (deepest, rest, fatherPath, oldPath)
  }


  /**
   *
   * @param l
   * @param max
   * @param pathForMax
   * @return
   */

    //TODO REGLAGE MAX
  @tailrec
  def getMaxAndPathForMax(l: List[Wrapper], max: Int, pathForMax: String): (Int, String) = {
    if (l.isEmpty) (max, pathForMax)
    else {
      val newList = l.filter(line => line.path.split("/").length > max)
      if(newList.nonEmpty) getMaxAndPathForMax(newList, newList.head.path.split("/").length, newList.last.path)
      else (max, pathForMax)
    }
  }

  /**
   *
   * @param path
   * @return
   */
  def getParentPath(path: String): (Option[String], Option[String]) = {
    val pathSplited: Array[String] = path.split("/")
    if (pathSplited.length <= 1) (None, Some(path))
    else {
      var parentPath: String = ""
      var first_dir: Boolean = true
      var index: Int = 0
      pathSplited.map(x => if (index < pathSplited.length - 1) {
        if (first_dir) {
          parentPath = x
          first_dir = false
        } else parentPath = parentPath + File.separator + x
        index = index + 1
      })
      (Some(parentPath), Some(path))
    }
  }


}
