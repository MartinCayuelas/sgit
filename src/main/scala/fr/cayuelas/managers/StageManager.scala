package fr.cayuelas.managers

import java.io.{File, PrintWriter}

import better.files.{File => BFile}
import fr.cayuelas.helpers.{HelperBranch, HelperPaths}
import fr.cayuelas.objects.Wrapper

object StageManager {

  def currentStagePath : String = HelperPaths.stagePath + File.separator + HelperBranch.getCurrentBranch
  def stageToCommitPath : String = HelperPaths.stagePath + File.separator + "stageToCommit"
  def stageValidatedPath : String = HelperPaths.stagePath + File.separator + "stageValidated"
  /**
   * Function that retrieve the current content in the stage file
   * @return the current content of the stage
   */
  def readStage(): String = IOManager.readInFile(currentStagePath)


  /**
   * Function that retrieve the current content in the stage file
   * @return the current content of the stage
   */
  def readStageAsLines(): List[String] = IOManager.readInFileAsLine(currentStagePath)

  /**
   * Function that retrieve the current content in the stageCommit file used to do commit
   * @return the current content of the stageCommit
   */
  def readStageToCommit(): List[String] = IOManager.readInFileAsLine(stageToCommitPath)

  /**
   * Function that retrieve the current content in the stageValidated file used to sgit status
   * @return the current content of the stageValidated
   */
  def readStageValidated(): List[String] = IOManager.readInFileAsLine(stageValidatedPath)


  /**
   * Function that clears the content of a stage File
   * @param path : path of the file that will be cleared
   */
  def clearStage(path: String): Unit ={
    val writer = new PrintWriter(path)
    writer.print("")
    writer.close()
  }

  /**
   * Function that verify if the stage can be commited or not. If there are at least one line, it's true else false
   * @return true if the stage can be commited else false
   */
  def canCommit: Boolean = {
    IOManager.readInFileAsLine(stageToCommitPath).nonEmpty
  }

  /**
   * Given the stage, this Function check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * @return a List of Wrapper containing all the files in same directory as .sgit
   */
  def retrieveStageRootBlobs(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = IOManager.readInFile(currentStagePath)
    //Split lines
    val stageContent = contentInStage.split("\n").map(x => x.split(" "))
    val blobs = stageContent.filter(x => x(2).split("/").length==1).toList
    blobs.map(e => Wrapper(e(2),e(1),e(0),BFile(e(2)).name))
  }

  /**
   *Given the stage, this Function check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * After that, for each line we get the path and retrieve his parent
   *  3 lists are created so then we zip them together and return only one List[Wrapper]
   * @return a ist of Wrapper containing all the files in subdirectories of .sgit path folder
   */
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = IOManager.readInFile(currentStagePath)

    //Split lines
    val stageContent = contentInStage.split("\n").map(x => x.split(" "))

    val filesNotInRoot = stageContent.filter(x => x(2).split("/").length > 1).toList
    //Cleaning from the filenames
    val paths = filesNotInRoot.map(x => BFile(System.getProperty("user.dir")).relativize(BFile(x(2)).parent).toString)
    val filesNames = filesNotInRoot.map(x =>{
      //x(2) the entire path
      BFile(x(2)).name // only the file name
    } )

    val hashes = filesNotInRoot.map(x =>x(1)).toList
    val blobs = List.fill(paths.size)("Blob")
    //Merging the result

    paths zip hashes zip blobs zip filesNames map {case (((a,b),c),d)=>Wrapper(a,b,c,d)}

  }

  /**
   * Function that verify if the blob already exists in the stage given his path
   * The content is filtered. If the path given equals the path already in the file. It's deleted
   * After the process, the content is rewritten in the file stage
   * @param blobWrapped : the path of the file that will be added in the stage
   * @param stageToWrite : stage in wich one we will write
   */
  def writeInStagesWithChecks(blobWrapped: Wrapper, stageToWrite: String): Boolean = {
    val lines = IOManager.readInFileAsLine(stageToWrite).map(x => x.split(" "))
    val linesWrapped = lines.map(x => Wrapper(x(2),x(1),x(0),""))
   //Clean the file
    val writer = new PrintWriter(stageToWrite)
    writer.print("")
    writer.close()

    val newLinesWrapped: List[Wrapper] = linesWrapped.map(line => if(line.path.equals(blobWrapped.path) && (stageToWrite.equals(stageToCommitPath)||stageToWrite.equals(currentStagePath))) blobWrapped else line)
    newLinesWrapped.map(l => IOManager.writeInFile(stageToWrite,l.typeElement+" "+l.hash+" "+l.path+"\n",append = true))//WriteInStage

    val isIn1ButNotIn2 = inFirstListButNotInSecondListWithPath(List(blobWrapped),newLinesWrapped)
    if(isIn1ButNotIn2.nonEmpty) true
    else false
  }

   def inFirstListButNotInSecondListWithPath(l1: List[Wrapper], l2: List[Wrapper]): List[Wrapper] = {
    l1.filter(x => !l2.exists(y => x.path.equals(y.path)))
  }

  /**
   * Function that verify if the blob already exists in the stage given his path
   * @param pathLine : the path of the file that will be added in the stage
   * @param stage : the stage we want to check
   * @return true if the file exists in the Stage else false
   */
  def checkIfFileIsInStage(pathLine: String, stage: String): Boolean = {
    val lines = IOManager.readInFileAsLine(stage)
    lines.map(x => x.split(" ")).exists(elem => elem(2).equals(pathLine))
  }

  /**
   * Function that verify if the blob already exists in the stage given his path and his sha1 id
   * @param pathLine : the path of the file that will be added in the stage
   * @param idSha1 : the id of the file
   * @param stage: the stage in which we want to test
   * @return true if the file exists in the Stage else false
   */
  def checkModification(pathLine: String, idSha1: String, stage: String): Boolean = {
    val lines = IOManager.readInFileAsLine(stage)
    lines.map(x => x.split(" ")).exists(x => pathLine.equals(x(2)) && !idSha1.equals(x(1)))
  }




}
