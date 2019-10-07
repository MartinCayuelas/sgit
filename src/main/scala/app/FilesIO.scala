package app

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths

object FilesIO {








  def writeHead(): Unit = {
    val path = Paths.get(".sgit").toAbsolutePath().toString().concat("/HEAD")
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("ref: refs/heads/master")
    bw.close()

  }


  def writeBlob(pathB: String, contentblob: String): Unit = {
    val path = Paths.get(pathB).toAbsolutePath().toString()

    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(contentblob)
    bw.close()
  }

  def writeTree(path: String, content: String): Unit = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file,true))
    bw.write(content)
    bw.close()
  }



}
