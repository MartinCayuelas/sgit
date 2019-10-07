package app.filesManager

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths

import app.commands.Branch

object FilesIO {


  def writeHead(): Unit = {
    val path = Paths.get(".sgit").toAbsolutePath.toString.concat("/HEAD")
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("ref: refs/heads/master")
    bw.close()

  }


  def writeBlob(pathB: String, contentblob: String): Unit = {
    val path = Paths.get(pathB).toAbsolutePath.toString

    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(contentblob)
    bw.close()
  }

  def writeBlobStage(contentblob: String): Unit = {
    val currentBranch = Branch.getCurrentBranch()
    val path = Paths.get(".sgit").toAbsolutePath.toString.concat(s"/stages/${currentBranch}")
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file,true))
    bw.write(contentblob)
    bw.close()
  }

  def writeTree(path: String, content: String): Unit = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file,true))
    bw.write(content)
    bw.close()
  }


  def readStage(): String = {
    val path = Paths.get(".sgit").toAbsolutePath.toString.concat(s"/stages/${Branch.getCurrentBranch()}")
    val source = scala.io.Source.fromFile(path)
    val content = try source.mkString finally source.close()
    content
  }


}
