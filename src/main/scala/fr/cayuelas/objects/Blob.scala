package fr.cayuelas.objects

import java.io.File

import fr.cayuelas.helpers.{HelperBlob, HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IoManager}

object Blob {

  /**
   *Function that creates a blob with the content of the file given in parameter and transforms it in Sha1 string
   * Do somme checks. like if the file is already or not in the Stage or in the stageCommit. Then we check if the file is a new version or not
   * @param f : File which will be processed
   * @return a string that is the id after the process of digest with Sha1 Algorithm
   */
  def createBlob(f: File): Unit = {
    val content: String = IoManager.readInFile(f.getPath)
    val idSha1: String = HelperSha1.convertToSha1(content) //Creates the id in sha1
    val relativePath: String = HelperPaths.getRelativePathOfFile(f.getAbsolutePath)

    relativePath.startsWith(".") match {
      case true => HelperBlob.checksAndWriteInFiles(Wrapper(relativePath.substring(2,relativePath.length),idSha1,"Blob",""))
      case false => HelperBlob.checksAndWriteInFiles(Wrapper(relativePath,idSha1,"Blob",""))
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
    val pathFolder: String  = HelperBlob.blobsPath + File.separator +  folder
    val pathFile: String  = HelperBlob.blobsPath + File.separator +  folder + File.separator + nameFile

    FilesManager.createNewFolder(pathFolder)
    FilesManager.createNewFile(pathFile)
    IoManager.writeInFile(pathFile,contentBlob,append = false)//WriteInBlob ex: .sgit/objects/blobs/ed/72d396fae9206628714fb2ce00f72e94f2258f
  }
}
