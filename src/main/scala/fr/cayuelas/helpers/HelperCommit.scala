package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{FilesManager, IoManager, LogsManager, StageManager}
import fr.cayuelas.objects.{Commit, Tree, Wrapper}

import scala.annotation.tailrec

object HelperCommit {

  def currentRefs : String = HelperPaths.branchesPath + File.separator + HelperBranch.getCurrentBranch
  def commitsPath: String = HelperPaths.objectsPath+File.separator+"commits"
  def treesPath: String = HelperPaths.objectsPath+File.separator+"trees"

  /**
   * Do the process of commit with getting informations about the commit and writes new content inf files
   * After that print the deifféreces between the new commit and the last commit
   * @param hashTreeFinal: highest tree's hash
   * @param messageCommit: message of the commit
   */

  def commit(hashTreeFinal: String, messageCommit: String): Unit = {
    val commit = new Commit()
    val lastCommit =  HelperCommit.getLastCommitInRefs()
    val commitCopy = commit.copy(parent = lastCommit,tree = hashTreeFinal,idCommit = HelperCommit.createIdCommit(commit),message=messageCommit)
    HelperCommit.saveCommitFile(commitCopy)

    if(HelperCommit.existsCommit) HelperCommit.printResultCommit(lastCommit,commitCopy)
    else HelperCommit.printResultFirstCommit(commitCopy)

    HelperCommit.setCommitInRefs(commitCopy.idCommit)

    IoManager.clearFile(StageManager.stageToCommitPath)
    IoManager.clearFile(StageManager.stageValidatedPath)
    IoManager.writeInFile(LogsManager.getCurrentPathLogs,HelperCommit.getCommitContentForLog(commitCopy),append = true)//WriteInLogs
  }

  /*
  PART CREATION OF TREES
   */
  /**
   * Function that creates all the trees for the commit with the non root files
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
   * Function that retrieves the deepest directory
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
   * Function that retrieves the path max in a given list
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
   * Function the retrieves the parent of a givent path
   * @param path : the path to be transformed
   * @return the parent path of the given path and itself
   */
  def getParentPath(path: String): (Option[String], Option[String]) = {
    val pathSplited: List[String] = path.split("/").toList
    if (pathSplited.length <= 1) (None, Some(path))
    else {
      val parentPath = pathSplited.init.map(_+File.separator).mkString.dropRight(1)
      (Some(parentPath), Some(path))
    }
  }

  /**
   * Merge the content of the stageToCommit in the real stage
   */
  def mergeStageToCommitInStage(): Unit = {
    val currentStageCommit = StageManager.readStageToCommit().map(_.split(" "))
    val currentStageCommitWrapped = currentStageCommit.map(x => Wrapper(x(2),x(1),x(0),""))
    currentStageCommitWrapped.map(line => {
      val isInS = StageManager.writeInStagesWithChecks(line,StageManager.currentStagePath)
      if(isInS) IoManager.writeInFile(StageManager.currentStagePath,line.typeElement+" "+line.hash+" "+line.path+"\n",append = true)
    }) //WriteInStage
  }
  /*
  PART COMMIT TOOLS
   */

  //get in refs/heads/branch
  def getLastCommitInRefs(): String = IoManager.readInFile(currentRefs)

  //Set in refs/heads/branch
  def setCommitInRefs(idCommit: String): Unit = IoManager.writeInFile(currentRefs,idCommit,append = false) //WriteInRefs

  def getCommitContentInFileObject(commit : Commit): List[String] = List(s"Tree ${commit.tree}\n",s"author ${commit.author} -- ${commit.dateCommit}\n")

  //Set in objects/objects/commits
  def saveCommitFile(commit: Commit): Unit = {
    val path = HelperPaths.objectsPath + File.separator + "commits"
    val folder = commit.idCommit.substring(0,2)
    val nameFile = commit.idCommit.substring(2,commit.idCommit.length)
    FilesManager.createNewFolder(path + File.separator +  folder)
    FilesManager.createNewFile(path + File.separator +  folder + File.separator + nameFile)

    getCommitContentInFileObject(commit).map(line => IoManager.writeInFile(path + File.separator +  folder + File.separator + nameFile,line,append = true)) //WriteInCommitFile
  }

  /**
   *Do the diff between the last commit and the current stage and display to the user the number of changes
   * @param lastCommit: id of the last commit in refs
   * @param commit : commit used to get some information like the id and the message
   */

  def printResultCommit(lastCommit: String, commit: Commit): Unit = {
    val (inserted,deleted) = HelperDiff.diffWhenCommitting(lastCommit)
    val numberOfChanges = IoManager.readInFileAsLine(StageManager.stageToCommitPath).length
    IoManager.printResultCommit(numberOfChanges,commit,inserted,deleted)
  }

  /**
   *Do the diff between the current  commit and the current stage and display to the user the number of changes. There is only insertion when first commit
   * @param commit : commit used to get some information like the id and the message
   */

  def printResultFirstCommit(commit: Commit): Unit = {
    val listCommitted = IoManager.readInFileAsLine(StageManager.stageToCommitPath)
    @tailrec
    def acc(listCommitted: List[String], accumulator: Int): Int ={
      if (listCommitted.isEmpty) accumulator
      else {
        val path = HelperPaths.sgitPath+listCommitted.head.split(" ")(2)
        val lines = IoManager.readInFileAsLine(path).length
        acc(listCommitted.tail,(lines+accumulator))
      }
    }
    val inserted =  acc(listCommitted,0)
    val numberOfChanges = listCommitted.length
    IoManager.printResultCommit(numberOfChanges,commit,inserted,0)

  }

  /**
   * Get the content of a given commit
   * @param commit: commit to use
   * @return the content of a tree
   */
  def getCommitContent(commit: Commit):String = s"Tree ${commit.tree} author ${commit.author} -- ${commit.dateCommit}"

  /**
   * Creates the content of the future log
   * @param commit: commit to write in logs
   * @return the content that will be written in the logs
   */
  def getCommitContentForLog(commit: Commit): String = {
    if (commit.parent.length >0)  s"${commit.parent} ${commit.idCommit} ${commit.author} ${commit.dateCommit} ${commit.message}\n"
    else s"0000000000000000000000000000000000000000 ${commit.idCommit} ${commit.author} ${commit.dateCommit} ${commit.message}\n"
  }

  /**
   *Creates the id of a commit with his content
   * @param commit: commit used to do the convertion
   * @return the id of the new commit
   */
  def createIdCommit(commit: Commit): String = HelperSha1.convertToSha1(HelperCommit.getCommitContent(commit))

  /**
   *Verify if the commit exists
   * @param commit: id string
   * @return true if the given string corresponds to a commit
   */
  def isACommit(commit: String): Boolean = {
    val (folder, file) = HelperPaths.getFolderAndFileWithSha1(commit)
    new File(HelperPaths.objectsPath+File.separator+"commits"+File.separator+folder++File.separator+file).exists()
  }

  //Verify if exists commit in ref (Like if it is the first commit or not)
  def existsCommit: Boolean = IoManager.readInFile(currentRefs).length > 0

  /**
   *Retrieves all blobs for a commit
   * @param hashCommit : string corresponding to the commit we want to retrieve
   * @return a list of element that are all the blobs contained by the commit given
   */
  def getAllBlobsFromCommit(hashCommit: String): List[Wrapper] ={
    val firstTree = getFirstTreeFromCommit(hashCommit)
    accumulatorBlobs(firstTree)
  }

  /**
   *Accumulates blobs for a tree
   * @param hashTree: hash of a tree
   * @return an accumulator containing a list of element blobs
   */
  def accumulatorBlobs(hashTree: String): List[Wrapper] ={
    val(newAcc,treesList) = retrievesBlobsAndTreesInTree(hashTree,"",List())
    recursiveRetrievesBlobsInTrees(treesList,newAcc)
  }

  /**
   * retrieves blobs and trees for a tree
   * @param hashTree : hash of a tree
   * @return a tuple containing all blobs and trees contained by a given tree
   */
  def retrievesBlobsAndTreesInTree(hashTree: String, pathParent: String, accBlobs: List[Wrapper]):  (List[Wrapper], List[Wrapper]) = {
    val pathToAdd = pathParent.length match {
      case 0 => ""
      case _ => pathParent+File.separator
    }
    val (folder,file) = HelperPaths.getFolderAndFileWithSha1(hashTree)
    val path = treesPath + File.separator + folder + File.separator +file
    val contentOfTree = IoManager.readInFileAsLine(path)

    val blobsSplited = contentOfTree.filter(x => x.startsWith("Blob")).map(_.split(" "))
    val listHashesAndPaths : List[Wrapper]= blobsSplited.map(x => Wrapper(pathToAdd+x(2),x(1),"Blob",""))

    val newAcc = accumulate(listHashesAndPaths,accBlobs)
    val treesInTree = contentOfTree.filter(x => x.startsWith("Tree")).map(_.split(" ")).map(x => Wrapper(x(2),x(1),"Tree",""))

    (newAcc,treesInTree)
  }

  /**
   *Retrieves recursively blobs in trees
   * @param listTrees: trees that we will parcour
   * @param accBlobs : accumulator of blobs element
   * @return all the blobs in all the trees given in parameter
   */
  @tailrec
  def recursiveRetrievesBlobsInTrees(listTrees: List[Wrapper], accBlobs: List[Wrapper] ): List[Wrapper] = {
    if(listTrees.isEmpty) accBlobs
    else{
      val(newAcc,treesList) = retrievesBlobsAndTreesInTree(listTrees.head.hash,listTrees.head.path,accBlobs)
      val newListOfTrees = treesList++listTrees.tail
      recursiveRetrievesBlobsInTrees(newListOfTrees,newAcc)

    }
  }

  /**
   *Get the first tree (the highest) of a given commit
   * @param hashCommit : hash of the commit
   * @return the hash of the first tree in a commit
   */
  def getFirstTreeFromCommit(hashCommit: String): String = {
    val (folder,file) = HelperPaths.getFolderAndFileWithSha1(hashCommit)
    val firstLine = IoManager.readInFileAsLine(HelperPaths.objectsPath+File.separator+"commits"+File.separator+folder+File.separator+file)(0)
    firstLine.split(" ")(1)
  }

  /**
   *Accumulate all the blobs given in parameter
   * @param newBlobs : blobs to be accumulated
   * @param acc: accumulator of blobs
   * @return a new list with all the blobs accumulated
   */
  @tailrec
  def accumulate(newBlobs: List[Wrapper], acc: List[Wrapper]): List[Wrapper] ={
    if (newBlobs.isEmpty) acc
    else accumulate(newBlobs.tail,newBlobs.head::acc)
  }
}
