package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.commands.Diff_cmd
import fr.cayuelas.managers.{FilesManager, IOManager, LogsManager, StageManager}
import fr.cayuelas.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec

object HelperCommit {

  def currentRefs : String = HelperPaths.branchesPath + File.separator + HelperBranch.getCurrentBranch
  def commitsPath: String = HelperPaths.objectsPath+File.separator+"commits"
  def treesPath: String = HelperPaths.objectsPath+File.separator+"trees"



  def commit(hashTreeFinal: String, messageCommit: String): Unit = {
    val commit = new Commit()
    val lastCommit =  HelperCommit.getLastCommitInRefs()
    val commitCopy = commit.copy(parent = lastCommit,tree = hashTreeFinal,idCommit = HelperCommit.createIdCommit(commit),message=messageCommit)
    HelperCommit.saveCommitFile(commitCopy)

    if(!HelperCommit.isFirstCommit) HelperCommit.printResultCommit(lastCommit,commitCopy)
    else HelperCommit.printResultFirstCommit(commitCopy)
    HelperCommit.setCommitInRefs(commitCopy.idCommit)
    StageManager.clearStage(StageManager.stageToCommitPath)
    StageManager.clearStage(StageManager.stageValidatedPath)

    IOManager.writeInFile(LogsManager.getCurrentPathLogs,HelperCommit.getCommitContentForLog(commitCopy),append = true)//WriteInLogs
  }

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

  /**
   *
   */
  def mergeStageToCommitInStage(): Unit = {
    val currentStageCommit = StageManager.readStageToCommit()
    currentStageCommit.map(line => {
      StageManager.deleteLineInStageIfFileAlreadyExists(line.split(" ")(2),StageManager.currentStagePath)
      IOManager.writeInFile(StageManager.currentStagePath,line,append = true)
    }) //WriteInStage
  }


  /*
  PART COMMIT TOOLS
   */

  //get in refs/heads/branch
  def getLastCommitInRefs(): String = {
    IOManager.readInFile(currentRefs)
  }

  //Set in refs/heads/branch
  def setCommitInRefs(idCommit: String): Unit = {
    IOManager.writeInFile(currentRefs,idCommit,append = false) //WriteInRefs
  }
  def getCommitContentInFileObject(commit : Commit): List[String] = {
    List(s"Tree ${commit.tree}\n",s"author ${commit.author} -- ${commit.dateCommit}\n")
  }
  //Set in objects/objects/commits
  def saveCommitFile(commit: Commit): Unit = {
    val path = HelperPaths.objectsPath + File.separator + "commits"
    val folder = commit.idCommit.substring(0,2)
    val nameFile = commit.idCommit.substring(2,commit.idCommit.length)
    FilesManager.createNewFolder(path + File.separator +  folder)
    FilesManager.createNewFile(path + File.separator +  folder + File.separator + nameFile)

    getCommitContentInFileObject(commit).map(line => IOManager.writeInFile(path + File.separator +  folder + File.separator + nameFile,line,true)) //WriteInCommitFile
  }



  def printResultCommit(lastCommit: String, commit: Commit): Unit = {

    val (inserted,deleted) = Diff_cmd.diffWhenCommitting(lastCommit)
    val numberOfChanges = IOManager.readInFileAsLine(StageManager.stageToCommitPath).length
    val resToPrint = numberOfChanges match {
      case  1 =>"["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} file changed, ${inserted} insertions(+), ${deleted} deletions(-)"
      case _ =>"["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} files changed, ${inserted} insertions(+), ${deleted} deletions(-)"
    }
    println(resToPrint)
  }

  def printResultFirstCommit(commit: Commit): Unit = {
    val listCommitted = IOManager.readInFileAsLine(StageManager.stageToCommitPath)
    @tailrec
    def acc(listCommitted: List[String], accumulator: Int): Int ={
      if (listCommitted.isEmpty) accumulator
      else {
        val path = HelperPaths.sgitPath+listCommitted.head.split(" ")(2)
        val lines = IOManager.readInFileAsLine(path).length
        acc(listCommitted.tail,(lines+accumulator))
      }
    }
    val inserted =  acc(listCommitted,0)
    val numberOfChanges = listCommitted.length

    val resToPrint = numberOfChanges match {
      case  1 =>"["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} file changed, ${inserted} insertions(+)"
      case _ =>"["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} files changed, ${inserted} insertions(+)"
    }
    println(resToPrint)
  }


  def getCommitContent(commit: Commit):String = {
    s"Tree ${commit.tree} author ${commit.author} -- ${commit.dateCommit}"
  }
  def getCommitContentForLog(commit: Commit): String = {
    if (commit.parent.length >0)  s"${commit.parent} ${commit.idCommit} ${commit.author} ${commit.dateCommit} ${commit.message}\n"
    else s"0000000000000000000000000000000000000000 ${commit.idCommit} ${commit.author} ${commit.dateCommit} ${commit.message}\n"
  }


  def createIdCommit(commit: Commit): String = {
    HelperSha1.convertToSha1(HelperCommit.getCommitContent(commit))
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
    val(newAcc,treesList) = retrievesBlobsAndTreesInTree(hashTree,"",List())
    recursiveRetrievesBlobsInTrees(treesList,newAcc)
  }

  /**
   *
   * @param hashTree
   * @return
   */
  def retrievesBlobsAndTreesInTree(hashTree: String, pathParent: String, accBlobs: List[(String,String)]):  (List[(String,String)],List[(String,String)]) = {
    val pathToAdd = pathParent.length match {
      case 0 => ""
      case _ => pathParent+File.separator
    }
    val (folder,file) = HelperPaths.getFolderAndFileWithSha1(hashTree)
    val path = treesPath + File.separator + folder + File.separator  +file
    val contentOfTree = IOManager.readInFileAsLine(path)

    val blobsSplited = contentOfTree.filter(x => x.startsWith("Blob")).map(b => b.split(" "))
    val listHashesAndPaths : List[(String,String)]= blobsSplited.map(x => (x(1),pathToAdd+x(2)))

    val newAcc = accumulate(listHashesAndPaths,accBlobs)
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
      val(newAcc,treesList) = retrievesBlobsAndTreesInTree(listTrees.head._1,listTrees.head._2,accBlobs)
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


  //TODO
 /* def getCommmitInLogs(sha1: String, branch: String): List[String] = {
    val logs = LogsManager.getLogsForBranch(HelperPaths.branchesPath+File.separator+branch)
   // val log : String = logs.filter(l => l.split(" ")(1)==sha1).take(0)
  //  log.split(" ").toList

  }*/


}
