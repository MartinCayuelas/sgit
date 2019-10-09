package app.objects

import java.io.File
import java.nio.file.Paths

import app.filesManager.FilesIO.writeBlob
import app.filesManager.Stage
import app.helpers.HelpersApp

object Blob {

  def createBlob(f: File): String = {

    val source = scala.io.Source.fromFile(f.getAbsoluteFile)
    val content = try source.mkString finally source.close()
    val idSha1 = HelpersApp.convertToSha1(content)

    addBlob(idSha1, content)

    val blob = s"Blob ${idSha1} ${f.getPath}\n"
    Stage.deleteLinesInStage(f.getPath)
    Stage.writeInStage(blob)
    blob
  }

  def addBlob(idSha1: String, contentBlob: String): Unit = {
    val path = Paths.get(".sgit/objects/blobs").toAbsolutePath.toString
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)

    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder+File.separator+nameFile).createNewFile()

    writeBlob(path + File.separator +  folder+File.separator+nameFile,contentBlob)
  }
}
