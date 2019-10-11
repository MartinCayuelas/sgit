package fr.cayuelas.objects

import java.io.File

import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IOManager, StageManager}

object Blob {

  //Blobs's path where each blob is stored in .sgit/objects/blobs
  val blobsPath: String = HelperPaths.objectsPath+File.separator+"blobs"


  /**
   *Method that creates a blob with the content of the file given in parameter and transforms it in Sha1 string
   * @param f : File which will be precocessed
   * @return a string that is the id after the process of digest with Sha1 Algorithm
   */
  def createBlob(f: File): String = {
    val content = IOManager.readInFile(f.getPath)
    val idSha1 = HelperSha1.convertToSha1(content) //Creates the id in sha1

    addBlobInObjects(idSha1, content) //Add blob in osgit/objects/blobs

    val blob = s"*Blob ${idSha1} ${HelperPaths.getRelativePathOfFile(f.getAbsolutePath)}\n"

    StageManager.deleteLineInStageIfFileAlreadyExists(HelperPaths.getRelativePathOfFile(f.getAbsolutePath))
    IOManager.writeInFile(StageManager.currentStagePath,blob,append = true) //WriteInStage

    blob
  }
  /**
   *Function that creates a blob in .sgit/objects/blobs
   * @param idSha1 : id of the blob that will be used to create a folder and a file
   * @param contentBlob : content that will be stored in the file created
   */
  def addBlobInObjects(idSha1: String, contentBlob: String): Unit = {
    val folder = idSha1.substring(0,2) //The 2 first letters of the id
    val nameFile = idSha1.substring(2,idSha1.length) //The rest of the id (exclude the 2 firsts letters)
    val pathFolder = blobsPath + File.separator +  folder
    val pathFile = blobsPath + File.separator +  folder + File.separator + nameFile

    FilesManager.createNewFolder(pathFolder)
    FilesManager.createNewFile(pathFile)
    IOManager.writeInFile(pathFile,contentBlob,append = false)//WriteInBlob ex: .sgit/objects/blobs/ed/72d396fae9206628714fb2ce00f72e94f2258f
  }
}
