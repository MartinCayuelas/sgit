package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{IoManager, StageManager}
import fr.cayuelas.objects.Wrapper

object HelperBlob {

  //Blobs's path where each blob is stored in .sgit/objects/blobs
  def blobsPath: String = HelperPaths.objectsPath+File.separator+"blobs"

  /**
   * Function that checks exitency in stage and stageCommit and write in files
   * @param blobWrapped: element blob wrapped
   */
  def checksAndWriteInFiles(blobWrapped: Wrapper): Unit = {
    val isInStage: Boolean = StageManager.checkIfFileIsInStage(blobWrapped.path, StageManager.currentStagePath)
    val isInStageCommit: Boolean = StageManager.checkIfFileIsInStage(blobWrapped.path, StageManager.stageToCommitPath)
    val modifiedInStage: Boolean = StageManager.checkModification(blobWrapped.path,blobWrapped.hash,StageManager.currentStagePath)

    if(isInStageCommit && !isInStage) write(blobWrapped,"newFile")
    else if(!isInStageCommit && isInStage ){
      if (modifiedInStage) write(blobWrapped,"modified")
    }
    else if(isInStage && isInStageCommit)write(blobWrapped,"modified")
    else write(blobWrapped,"newFile")
  }

  /**
   * Writes in différents files given params
   * @param blobWrapped: wrapper of the blob beeing written
   * @param typeOfAdd : "Modified or newFile
   */
  def write(blobWrapped: Wrapper, typeOfAdd: String): Unit = {
    val isIn1ButNotIn2C = StageManager.writeInStagesWithChecks(blobWrapped,StageManager.stageToCommitPath)
    val isIn1ButNotIn2V = StageManager.writeInStagesWithChecks(blobWrapped,StageManager.stageValidatedPath)

    if(isIn1ButNotIn2C&&isIn1ButNotIn2V){//New
      val blob : String = s"Blob ${blobWrapped.hash} ${blobWrapped.path}\n"
      IoManager.writeInFile(StageManager.stageValidatedPath,s"${typeOfAdd} : "+blobWrapped.path,append = true)
      IoManager.writeInFile(StageManager.stageToCommitPath,blob,append = true)
    }
  }

  /**
   * Allows to creates an id string
   * @param f : file to read the content
   * @return a string in sha1
   */
  def createSha1Blob(f: String): String = HelperSha1.convertToSha1(IoManager.readInFile(f)) //Creates the id in sha1

  /**
   * Reads the content of a given blob's sha1
   * @param sha1 : id of the file that we want to readIn
   * @return the content of the blob in a list of strings
   */
  def readContentInBlob(sha1: String): List[String] = {
    val folder = sha1.substring(0,2)
    val file = sha1.substring(2,sha1.length)
    IoManager.readInFileAsLine(HelperBlob.blobsPath+File.separator+folder+File.separator+file)
  }

  /**
   * Checks if the given sha1 is part of a blob
   * @param sha1: id of a blob
   * @return true if the blob exists else false
   */
  def blobExists(sha1: String): Boolean = {
    val folder = sha1.substring(0,2)
    val file = sha1.substring(2,sha1.length)
    new File(HelperBlob.blobsPath+File.separator+folder+File.separator+file).exists()
  }

}
