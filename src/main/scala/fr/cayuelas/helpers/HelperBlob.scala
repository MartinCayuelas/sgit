package fr.cayuelas.helpers

import fr.cayuelas.managers.{IOManager, StageManager}

object HelperBlob {

  /**
   * Function that checks exitency in stage and stageCommit and write in files
   * @param relativePath : path of the blob
   * @param idSha1 : id's blob
   */
  def checksAndWriteInFiles(relativePath: String, idSha1: String): Unit = {
    val isInStage: Boolean =StageManager.checkIfFileIsInStage(relativePath, StageManager.currentStagePath)
    val isInStageCommit: Boolean =StageManager.checkIfFileIsInStage(relativePath, StageManager.stageCommitPath)
    val modifiedInStage: Boolean = StageManager.checkModification(relativePath,idSha1,StageManager.currentStagePath)

    StageManager.deleteLineInStageIfFileAlreadyExists(relativePath,StageManager.stageCommitPath)
    StageManager.deleteLineInStageIfFileAlreadyExists(relativePath,StageManager.stageValidatedPath)

    val blob : String = s"Blob ${idSha1} ${relativePath}\n"

    if(isInStageCommit && !isInStage) {
      IOManager.writeInFile(StageManager.stageValidatedPath,s"newfile : "+relativePath,append = true)
      IOManager.writeInFile(StageManager.stageCommitPath,blob,append = true)
    }else if(!isInStageCommit && isInStage ){
      if (modifiedInStage) {
        IOManager.writeInFile(StageManager.stageCommitPath,blob,append = true)
        IOManager.writeInFile(StageManager.stageValidatedPath,s"modified : "+relativePath,append = true)
      }
    } else if(isInStage && isInStageCommit){
      IOManager.writeInFile(StageManager.stageValidatedPath,s"modified : "+relativePath,append = true)
      IOManager.writeInFile(StageManager.stageCommitPath,blob,append = true)
    }  else{
      IOManager.writeInFile(StageManager.stageValidatedPath,s"newfile : "+relativePath,append = true)
      IOManager.writeInFile(StageManager.stageCommitPath,blob,append = true)
    }
  }

  /**
   * Allows to creates an id string
   * @param f : file to read the content
   * @return a string in sha1
   */
  def createSha1Blob(f: String): String = {
    val content: String = IOManager.readInFile(f)
    HelperSha1.convertToSha1(content) //Creates the id in sha1
  }

}
