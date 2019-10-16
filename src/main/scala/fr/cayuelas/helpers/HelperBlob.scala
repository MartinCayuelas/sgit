package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{IOManager, StageManager}

object HelperBlob {

  //Blobs's path where each blob is stored in .sgit/objects/blobs
  def blobsPath: String = HelperPaths.objectsPath+File.separator+"blobs"

  /**
   * Function that checks exitency in stage and stageCommit and write in files
   * @param relativePath : path of the blob
   * @param idSha1 : id's blob
   */
  def checksAndWriteInFiles(relativePath: String, idSha1: String): Unit = {
    val isInStage: Boolean = StageManager.checkIfFileIsInStage(relativePath, StageManager.currentStagePath)
    val isInStageCommit: Boolean = StageManager.checkIfFileIsInStage(relativePath, StageManager.stageToCommitPath)
    val modifiedInStage: Boolean = StageManager.checkModification(relativePath,idSha1,StageManager.currentStagePath)

    StageManager.deleteLineInStageIfFileAlreadyExists(relativePath,StageManager.stageToCommitPath)
    StageManager.deleteLineInStageIfFileAlreadyExists(relativePath,StageManager.stageValidatedPath)

    val blob : String = s"Blob ${idSha1} ${relativePath}\n"

    if(isInStageCommit && !isInStage) write(blob,relativePath,"newFile")
    else if(!isInStageCommit && isInStage ){
      if (modifiedInStage) write(blob,relativePath,"modified")
    }
    else if(isInStage && isInStageCommit)write(blob,relativePath,"modified")
    else write(blob,relativePath,"newFile")
  }

  /**
   * Writes in différents files given params
   * @param blob: the content that will be written
   * @param relativePath : the path of the file
   * @param typeOfAdd : "Modified or newFile
   */
  def write(blob: String, relativePath: String, typeOfAdd: String): Unit = {
    IOManager.writeInFile(StageManager.stageValidatedPath,s"${typeOfAdd} : "+relativePath,append = true)
    IOManager.writeInFile(StageManager.stageToCommitPath,blob,append = true)
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

  def readContentInBlob(sha1: String): List[String] = {
    val folder = sha1.substring(0,2)
    val file = sha1.substring(2,sha1.length)
    IOManager.readInFileAsLine(HelperBlob.blobsPath+File.separator+folder+File.separator+file)
  }

  def blobExists(sha1: String): Boolean = {
    val folder = sha1.substring(0,2)
    val file = sha1.substring(2,sha1.length)
    new File(HelperBlob.blobsPath+File.separator+folder+File.separator+file).exists()
  }

}
