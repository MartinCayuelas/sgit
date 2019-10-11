package fr.cayuelas.commands


import java.io.File

import fr.cayuelas.managers.StageManager
import fr.cayuelas.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec


object Commit_cmd {

  def commit(): Unit = {
    if(!StageManager.canCommit) println("Nothing to commit")
    else {

      var resHighestTrees: List[Wrapper] = List()
      val stage: List[Wrapper] = StageManager.retrieveStageStatus()
      val blobsInRoot: List[Wrapper] = StageManager.retrieveStageRootBlobs()

      if (stage.nonEmpty)  resHighestTrees = createAllTrees(stage, None)


      // Creating the tree for commit
      val hashFinalGhostTree = Tree.createTree(Some(resHighestTrees),Some(blobsInRoot))
      //Creation and process about a Commit object (class)
      Commit.commit(hashFinalGhostTree)
    }
  }

  @tailrec
  def createAllTrees(l: List[Wrapper], WrapperWithHashFinal: Option[List[Wrapper]]): List[Wrapper] = {
    if(l.isEmpty) WrapperWithHashFinal.get
    else {
      val (deeper, rest, father) = getDeeperDirectory(l)
      val hash: String = Tree.createTree(None,Some(deeper))
      if(father.isEmpty) {
        if (WrapperWithHashFinal.isEmpty) createAllTrees(rest, Some(List(Wrapper(deeper(0).path,hash,"Tree"))))
        else  createAllTrees(rest, Some(Wrapper(deeper(0).path,hash,"Tree") :: WrapperWithHashFinal.get))
      }
      else createAllTrees(Wrapper(father.get, hash, "Tree") :: rest, WrapperWithHashFinal)
    }
  }

  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String]) = {
    var max: Int = 0
    var pathForMax: String = ""

    l.map(line => if (line.path.split("/").size >= max) {
      max = line.path.split("/").size
      pathForMax = line.path
    })

    val rest: List[Wrapper] = l.filter(x => !(x.path.equals(pathForMax)))
    val deepest: List[Wrapper]  = l.filter(x => x.path.equals(pathForMax))
    val fatherPath: Option[String] = getParentPath(pathForMax)

    (deepest, rest, fatherPath)
  }

  def getParentPath(path: String): Option[String] = {
    val pathSplited: Array[String] = path.split("/")
    if(pathSplited.length <= 1) None
     else {
      var parentPath : String = ""
      var first_dir: Boolean = true
      var index: Int = 0
      pathSplited.map(x => if(index < pathSplited.length-1){
        if(first_dir){
          parentPath = x
          first_dir = false
        } else parentPath = parentPath + File.separator + x
        index = index+1
      })
      Some(parentPath)
    }
  }
}
