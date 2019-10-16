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
   * Method that retrieve the current content in the stage file
   * @return the current content of the stage
   */
  def readStage(): String = IOManager.readInFile(currentStagePath)


  /**
   * Method that retrieve the current content in the stage file
   * @return the current content of the stage
   */
  def readStageAsLines(): List[String] = IOManager.readInFileAsLine(currentStagePath)

  /**
   * Method that retrieve the current content in the stageCommit file used to do commit
   * @return the current content of the stageCommit
   */
  def readStageToCommit(): List[String] = IOManager.readInFileAsLine(stageToCommitPath)

  /**
   * Method that retrieve the current content in the stageValidated file used to sgit status
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
   * Method that verify if the stage can be commited or not. If there are at least one line, it's true else false
   * @return true if the stage can be commited else false
   */
  def canCommit: Boolean = {
    IOManager.readInFileAsLine(stageToCommitPath).nonEmpty
  }

  /**
   * Given the stage, this method check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * @return a List of Wrapper containing all the files in same directory as .sgit
   */
  def retrieveStageRootBlobs(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = IOManager.readInFile(currentStagePath)
    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))
    val blobs = stage_content.filter(x => x(2).split("/").length==1).toList
    blobs.map(e => Wrapper(e(2),e(1),e(0),BFile(e(2)).name))
  }

  /**
   *Given the stage, this method check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * After that, for each line we get the path and retrieve his parent
   *  3 lists are created so then we zip them together and return only one List[Wrapper]
   * @return a ist of Wrapper containing all the files in subdirectories of .sgit path folder
   */
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = IOManager.readInFile(currentStagePath)

    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))

    val filesNotInRoot = stage_content.filter(x => x(2).split("/").length > 1).toList
    //Cleaning from the filenames
    val paths = filesNotInRoot.map(x => BFile(System.getProperty("user.dir")).relativize(BFile(x(2)).parent).toString)
    val files_names = filesNotInRoot.map(x =>{
      //x(2) the entire path
      BFile(x(2)).name // only the file name
    } )

    val hashes = filesNotInRoot.map(x =>x(1)).toList
    val blobs = List.fill(paths.size)("Blob")
    //Merging the result

    paths zip hashes zip blobs zip files_names map {case (((a,b),c),d)=>Wrapper(a,b,c,d)}

  }

  /**
   * Function that verify if the blob already exists in the stage given his path
   * The content is filtered. If the path given equals the path already in the file. It's deleted
   * After the process, the content is rewritten in the file stage
   * @param pathLine : the path of the file that will be added in the stage
   */
  def deleteLineInStageIfFileAlreadyExists(pathLine: String, stageToWrite: String): Unit = {
    val lines = IOManager.readInFileAsLine(stageToWrite)
    //Clean the file
    val writer = new PrintWriter(stageToWrite)
    writer.print("")
    writer.close()
    val stageContent = lines.map(x => x.split(" "))
    val stageFiltered =  stageContent.filter(x => !x(2).equals(pathLine))
    val stage: List[String] = stageFiltered.map(x => x(0)+" "+x(1)+" "+x(2)+"\n")

    stage.map(line => IOManager.writeInFile(stageToWrite,line,true))//WriteInStage
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
