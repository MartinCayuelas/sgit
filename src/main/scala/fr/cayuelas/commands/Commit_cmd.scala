package fr.cayuelas.commands


import java.io.File

import fr.cayuelas.filesManager.Stage
import fr.cayuelas.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec


object Commit_cmd {

  def commit(): Unit = {
    if(Stage.stageEmpty()) println("Nothing to commit")
    else {

      var resHighestTrees: List[Wrapper] = List()
      val stage: List[Wrapper] = Stage.retrieveStageStatus()
      val blobsInRoot: List[Wrapper] = Stage.retrieveStageRootBlobs()

      if (stage.length > 0)  resHighestTrees = addTrees(stage, None)


      // Creating the tree for commit
      val hashFinalGhostTree = Tree.createTreeGhost(blobsInRoot,resHighestTrees)
      //Creation and process about a Commit object (class)
      Commit.commit(hashFinalGhostTree)
    }
  }

  @tailrec
  def addTrees(l: List[Wrapper], WrapperWithHashFinal: Option[List[Wrapper]]): List[Wrapper] = {
    if(l.size == 0){

      WrapperWithHashFinal.get

    } else {
      val (deeper, rest, father) = getDeeperDirectory(l)
      val hash = Tree.createTree(deeper)
      if(father.isEmpty) {
        if (WrapperWithHashFinal.isEmpty)addTrees(rest, Some(List(Wrapper(deeper(0).path,hash,"Tree"))))
        else  addTrees(rest, Some(Wrapper(deeper(0).path,hash,"Tree") :: WrapperWithHashFinal.get))
      }
      else addTrees(Wrapper(father.get, hash, "Tree") :: rest, WrapperWithHashFinal)
    }
  }

  def getDeeperDirectory(l: List[Wrapper]): (List[Wrapper], List[Wrapper], Option[String]) = {
    var max = 0
    var pathForMax = ""

    l.map(line => if (line.path.split("/").size >= max) {
      max = line.path.split("/").size
      pathForMax = line.path
    })

    val rest = l.filter(x => !(x.path.equals(pathForMax)))
    val deepest = l.filter(x => x.path.equals(pathForMax))
    val fatherPath = getParentPath(pathForMax)

    (deepest, rest, fatherPath)
  }

  def getParentPath(path: String): Option[String] = {
    val pathSplited = path.split("/")
    if(pathSplited.length <= 1){
      None
    } else {
      var parentPath = ""
      var first_dir = true
      var index =0
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
