package fr.cayuelas.objects

import java.io.File
import java.nio.file.Paths

import fr.cayuelas.filesManager.{FilesIO, Stage}
import fr.cayuelas.helpers.HelpersApp

object Blob {

  val blobsPath: String = Paths.get(".sgit/objects/blobs").toAbsolutePath.toString

  def createBlob(f: File): String = {

    println("Paths to blobs : "+blobsPath)
    println("f.getPath : "+f.getPath)
    val source = scala.io.Source.fromFile(f.getPath)
    val content = try source.mkString finally source.close()
    val idSha1 = HelpersApp.convertToSha1(content)

    addBlobInObjects(idSha1, content)

    val blob = s"Blob ${idSha1} ${f.getPath}\n"
    Stage.deleteLineInStageIfFileAlreadyExists(f.getPath)
    println("Stage path : "+Stage.stagePath)
    FilesIO.writeInFile(Stage.stagePath,blob,append = true) //WriteInStage
    blob
  }

  def addBlobInObjects(idSha1: String, contentBlob: String): Unit = {

    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    println("Folder: "+blobsPath + File.separator +  folder)
    new File(blobsPath + File.separator +  folder).mkdir()
    println("File: "+blobsPath + File.separator +  folder+File.separator+nameFile)
    new File(blobsPath + File.separator +  folder+File.separator+nameFile).createNewFile()

    FilesIO.writeInFile(blobsPath + File.separator +  folder+File.separator+nameFile,contentBlob,append = false)//WriteInBlob
  }
}
