package app.filesManager

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths

import better.files.{File => BFile}

import app.commands.Branch
import app.objects.Wrapper

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
    val currentBranch = Branch.getCurrentBranch
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
    val path = Paths.get(".sgit").toAbsolutePath.toString.concat(s"/stages/${Branch.getCurrentBranch}")
    val source = scala.io.Source.fromFile(path)
    val content = try source.mkString finally source.close()
    content
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:
  //(src/main/scala/objects,a7dbb76b0406d104b116766a40f2e80a79f40a0349533017253d52ea750d9144)
  //(src/main/scala/utils,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0)
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val files = FilesIO.readStage()
    val base_dir = System.getProperty("user.dir")

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))

    //Cleaning from the filenames
    val paths = stage_content.map(x => BFile(base_dir).relativize(BFile(x(2)).parent).toString).toList

val pathes = paths
    val hashs = stage_content.map(x =>x(1)).toList
    val blob = List.fill(paths.size)("blob")
    //Merging the result
    val listTobeReturned=((paths,hashs,blob).zipped.toList)
    listTobeReturned.map(elem => Wrapper(elem._1,elem._2,elem._3))
  }

  def retrieveStageRootBlobs(): Unit= {
    //Retrieve useful data
    val files = FilesIO.readStage()
    val base_dir = System.getProperty("user.dir")

    //Split lines
    val stage_content = files.split("\n").map(x => x.split(" "))
    val blobs = stage_content.filter(x => x(2).split("/").length==1).toList
   blobs.map(e => println(e(2)))

  }

}
