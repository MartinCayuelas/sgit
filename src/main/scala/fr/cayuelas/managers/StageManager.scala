package fr.cayuelas.managers

import java.io.{File, PrintWriter}

import better.files.{File => BFile}
import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.helpers.HelperPaths
import fr.cayuelas.objects.Wrapper

object StageManager {

  val currentStagePath : String = HelperPaths.stagePath + File.separator + Branch_cmd.getCurrentBranch

  /**
   * Method that retrieve the current content in the stage file
   * @return the current content of the stage
   */
  def readStage(): String = IOManager.readInFile(currentStagePath)

  /**
   * Function that clears the content of a tree's file
   */
  def clearStage(): Unit ={
    val writer = new PrintWriter(currentStagePath)
    writer.print("")
    writer.close()
  }

  /**
   * Method that verify if the stage can be commited or not. If there are at least one line beginning with *, it's true else false
   * @return true if the stage can be commited else false
   */
  def canCommit: Boolean = {
    val lines = IOManager.readInFileAsLine(currentStagePath)
    var canBeCommitted = false
    lines.map(e => {if(e.startsWith("*")) canBeCommitted = true})
    canBeCommitted
  }

  /**
   * Given the stage, this method check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * @return a List of Wrapper containing all the files in same directory as .sgit
   */
  def retrieveStageRootBlobs(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = readStage()
    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))
    val blobs = stage_content.filter(x => x(2).split("/").length==1).toList
    blobs.map(e => Wrapper(e(2),e(1),e(0)))
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:

  //(hello/world,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0, blob)

  /**
   *Given the stage, this method check if a file is at the root of .sgit directory or not. The content is filtered given a predicat
   * After that, for each line we get the path and retrieve his parent
   *  3 lists are created so then we zip them together and return only one List[Wrapper]
   * @return a ist of Wrapper containing all the files in subdirectories of .sgit path folder
   */
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = readStage()

    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))

    val filesNotInRoot = stage_content.filter(x => x(2).split("/").length > 1).toList
    //Cleaning from the filenames
    val paths = filesNotInRoot.map(x => BFile(System.getProperty("user.dir")).relativize(BFile(x(2)).parent).toString)

    val hashes = stage_content.map(x =>x(1)).toList
    val blobs = List.fill(paths.size)("Blob")
    //Merging the result
    val listTobeReturned=((paths,hashes,blobs).zipped.toList)
    listTobeReturned.map(elem => Wrapper(elem._1,elem._2,elem._3))
  }

  /**
   * Function that verify if the blob already exists in the stage given his path
   * The content is filtered. If the path given equals the path already in the file. It's deleted
   * After the process, the content is rewritten in the file stage
   * @param pathLine : the path of the file that will be added in the stage
   */
  def deleteLineInStageIfFileAlreadyExists(pathLine: String): Unit = {
    val lines = IOManager.readInFileAsLine(currentStagePath)
    //Clean the file
    val writer = new PrintWriter(currentStagePath)
    writer.print("")
    writer.close()
    val stageContent = lines.map(x => x.split(" "))
    val stageFiltered =  stageContent.filter(x => {
      if (x(2).endsWith("+")) !x(2).substring(0,x(2).length-1).equals(pathLine)
      else !x(2).equals(pathLine)
    })
    val stage: List[String] = stageFiltered.map(x => x(0)+" "+x(1)+" "+x(2)+"\n")

    stage.map(line => IOManager.writeInFile(currentStagePath,line,true))//WriteInStage
  }



  /**
   * Function that remove the * before each line that have one
   */
  def clearStarsInStage(): Unit = {
    val lines = IOManager.readInFileAsLine(currentStagePath)
    clearStage()

    val newStage = lines.map(e => {
      if(e.startsWith("*")) e.substring(1,e.length)+"\n"
      else e+"\n"
    })

    newStage.map(line => IOManager.writeInFile(currentStagePath,line,true))//WriteInStage
  }

  /**
   * Function that remove the + at the end of each line that have one
   */
  def clearPlusInStage(): Unit = {
    val lines = IOManager.readInFileAsLine(currentStagePath)
    clearStage()

    val newStage = lines.map(e => {
      if(e.endsWith("+")) e.substring(0,e.length-1)+"\n"
      else e+"\n"
    })

    newStage.map(line => IOManager.writeInFile(currentStagePath,line,true))//WriteInStage
  }

  /**
   *
   * @return
   */
  def retrieveLinesBeginningWithStars : List[String] ={
    val stage = IOManager.readInFileAsLine(currentStagePath)
    stage.filter(line => line.startsWith("*"))
  }


}
