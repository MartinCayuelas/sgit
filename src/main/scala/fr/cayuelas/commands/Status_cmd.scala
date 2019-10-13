package fr.cayuelas.commands

import fr.cayuelas.helpers.HelperPaths
import fr.cayuelas.managers.{FilesManager, StageManager}
import fr.cayuelas.objects.Blob
object Status_cmd {

  /**
   * Main function that dispatch the action
   */
  def status() : Unit ={
    printChangesThatWillBeValidated()
    printChangesThatWillNotBeValidated()
    printUntrackedFiles()
  }

  /**
   *Method that retrieve the files that will be added in the next commit
   * @return a list[String] containing all changes that will be validated
   */
  def getChangesThatWillBeValidated: List[String] = {
    StageManager.readStageValidated()
  }

  /**
   * Print all the files that will be added in the next commit
   */

  def printChangesThatWillBeValidated(): Unit = {
    println(s"On the ${Branch_cmd.getCurrentBranch} branch")
    println("Changes that will be validated : \n")
    getChangesThatWillBeValidated.map(elem => println(s"   ${Console.GREEN}"+elem+Console.RESET))
    println

  }

  /**
   *Method that retrieve the files that will not be validated but they are tracked
   *
   * @return a list[String] containing all changes that will not be validated
   */

  def getChangesThatWillNotBeValidated: List[String] = {


    getPathsOfFilesTracked.filter(
      elem =>

      //Case 1 Not in currentStage and not the same in StageCommit
      (!StageManager.checkIfFileIsInStage(elem, StageManager.currentStagePath) && StageManager.checkIfFileIsInStage(elem, StageManager.stageCommitPath)  && StageManager.checkModification(elem, Blob.createSha1Blob(HelperPaths.sgitPath+elem), StageManager.stageCommitPath))
        ||
        //Case 2  Only in stage
        (!StageManager.checkIfFileIsInStage(elem, StageManager.stageCommitPath)&&
          StageManager.checkIfFileIsInStage(elem, StageManager.currentStagePath) && StageManager.checkModification(elem,Blob.createSha1Blob(HelperPaths.sgitPath+elem)
          , StageManager.currentStagePath))
        ||
        //Case3 In stageCommit and stage
        (StageManager.checkIfFileIsInStage(elem, StageManager.stageCommitPath) && StageManager.checkIfFileIsInStage(elem, StageManager.currentStagePath)
          && StageManager.checkModification(elem,Blob.createSha1Blob(HelperPaths.sgitPath+elem)
          , StageManager.stageCommitPath))
    )
  }

  /**
   * Print all the files that will not be validated
   */
  def printChangesThatWillNotBeValidated(): Unit = {
    println("Changes that will not be validated:")
    println("   (use \"git add <file> ...\" to update what will be validated)\n")

    getChangesThatWillNotBeValidated.map(e => println(s"   ${Console.RED}modified : "+e+Console.RESET))
    println
  }

  /**
   * Method that retrieve all the files not tracked
   * @return a list[String] containing all Files not tracked in the stage
   */

  def getUntracked: List[String] = {
    val listOfAll = FilesManager.getListOfContentInDirectory(HelperPaths.sgitPath)
    val listOfAllCleared = listOfAll.map(e => HelperPaths.getRelativePathOfFile(e.getAbsolutePath))

    listOfAllCleared.diff(getPathsOfFilesTracked) //Différence between the Working directory and the "stage"
  }

  /**
   * Print all the files not tracked in the stage
   */
  def printUntrackedFiles(): Unit = {
    println("Files untracked:")
    println("   (use \"git add <file> ...\" to include what will be validated)\n")

    getUntracked.map(e => println(s"   ${Console.RED}"+e+Console.RESET))
    println
  }


  /**
   * Methot that retrieves all the files tracked (In stage or in stageCommit)
   * @return a list of string that represents the files tracked
   */
  def getPathsOfFilesTracked: List[String] = {
    val staged = StageManager.readStageAsLines()
    val stagedInCommit = StageManager.readStageCommit()

    val stagedInCommitSplited = stagedInCommit.map(x => x.split(" ")) //Split lines
    val pathsInStageCommit = stagedInCommitSplited.map(x => x(2))

    val stagedSplited= staged.map(x => x.split(" ")) //Split lines
    val pathsInStage = stagedSplited.map(x => x(2))

    pathsInStage.concat(pathsInStageCommit).distinct //Union of the 2 lists of paths

  }

}