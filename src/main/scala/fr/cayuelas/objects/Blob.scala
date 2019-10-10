package fr.cayuelas.objects

import java.io.File
import java.nio.file.Paths

import fr.cayuelas.filesManager.{FilesIO, Stage}
import fr.cayuelas.helpers.HelpersApp

object Blob {

  val blobsPath: String = Paths.get(".sgit/objects/blobs").toAbsolutePath.toString

  def createBlob(f: File): String = {

    val source = scala.io.Source.fromFile(f.getPath)
    val content = try source.mkString finally source.close()
    val idSha1 = HelpersApp.convertToSha1(content)

    addBlob(idSha1, content)

    val blob = s"Blob ${idSha1} ${f.getPath}\n"
    Stage.deleteLineInStageIfFileAlreadyExists(f.getPath)
    FilesIO.writeInFile(Stage.stagePath,blob,true) //WriteInStage
    blob
  }

  def addBlob(idSha1: String, contentBlob: String): Unit = {

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)

    new File(blobsPath + File.separator +  folder).mkdir()
    new File(blobsPath + File.separator +  folder+File.separator+nameFile).createNewFile()

    FilesIO.writeInFile(blobsPath + File.separator +  folder+File.separator+nameFile,contentBlob,false)//WriteInBlob
  }
}
