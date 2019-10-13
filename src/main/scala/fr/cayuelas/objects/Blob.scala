package fr.cayuelas.objects

import java.io.File

import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IOManager, StageManager}

object Blob {

  //Blobs's path where each blob is stored in .sgit/objects/blobs
  def blobsPath: String = HelperPaths.objectsPath+File.separator+"blobs"


  /**
   *Method that creates a blob with the content of the file given in parameter and transforms it in Sha1 string
   * Do somme checks. like if the file is already or not in the Stage or in the stageCommit. Then we check if the file is a new version or not
   * @param f : File which will be processed
   * @return a string that is the id after the process of digest with Sha1 Algorithm
   */
  def createBlob(f: File): Unit = {
    val content: String = IOManager.readInFile(f.getPath)
    val idSha1: String = HelperSha1.convertToSha1(content) //Creates the id in sha1
    val relativePath: String = HelperPaths.getRelativePathOfFile(f.getAbsolutePath)

    relativePath.startsWith(".") match {
      case true => checksAndWriteInFiles(relativePath.substring(2,relativePath.length),idSha1)
      case false => checksAndWriteInFiles(relativePath,idSha1)
    }

    addBlobInObjects(idSha1, content) //Add blob in .sgit/objects/blobs

  }
  /**
   *Function that creates a blob in .sgit/objects/blobs
   * @param idSha1 : id of the blob that will be used to create a folder and a file
   * @param contentBlob : content that will be stored in the file created
   */
  def addBlobInObjects(idSha1: String, contentBlob: String): Unit = {
    val folder: String = idSha1.substring(0,2) //The 2 first letters of the id
    val nameFile: String  = idSha1.substring(2,idSha1.length) //The rest of the id (exclude the 2 firsts letters)
    val pathFolder: String  = blobsPath + File.separator +  folder
    val pathFile: String  = blobsPath + File.separator +  folder + File.separator + nameFile

    FilesManager.createNewFolder(pathFolder)
    FilesManager.createNewFile(pathFile)
    IOManager.writeInFile(pathFile,contentBlob,append = false)//WriteInBlob ex: .sgit/objects/blobs/ed/72d396fae9206628714fb2ce00f72e94f2258f
  }


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

  def createSha1Blob(f: File): String = {
    val content: String = IOManager.readInFile(f.getPath)
    HelperSha1.convertToSha1(content) //Creates the id in sha1
  }




}
