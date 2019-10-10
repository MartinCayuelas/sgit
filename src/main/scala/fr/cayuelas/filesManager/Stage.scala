package fr.cayuelas.filesManager

import java.io.{File, PrintWriter}
import java.nio.file.Paths

import better.files.{File => BFile}
import fr.cayuelas.commands.Branch_cmd
import fr.cayuelas.objects.Wrapper

object Stage {

  val stagePath : String = Paths.get(".sgit").toString.concat(s"/stages/${ Branch_cmd.getCurrentBranch}")

  def readStage(): String = {
    val source = scala.io.Source.fromFile(stagePath)
    val contentInStage = try source.mkString finally source.close()
    contentInStage
  }

  def clearStage(): Unit ={
    val writer = new PrintWriter(stagePath)
    writer.print("")
    writer.close()
  }


  def stageEmpty(): Boolean = {
    val file = new File(stagePath)
    file.length() == 0
  }

  def retrieveStageRootBlobs(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = readStage()
    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))
    val blobs = stage_content.filter(x => x(2).split("/").length==1).toList
    blobs.map(e => Wrapper(e(2),e(1),e(0)))
  }

  //Returns a list containing the path to a file that has been converted to a Blob (because it's in the STAGE) and its Hash
  //OUTPUT is something like this:
  //(src/main/scala/objects,a7dbb76b0406d104b116766a40f2e80a79f40a0349533017253d52ea750d9144)
  //(src/main/scala/utils,29ee69c28399de6f830f3f0f55140ad97c211fc851240901f9e030aaaf2e13a0)
  def retrieveStageStatus(): List[Wrapper]= {
    //Retrieve useful data
    val contentInStage = readStage()

    //Split lines
    val stage_content = contentInStage.split("\n").map(x => x.split(" "))

    val filesNotInRoot = stage_content.filter(x => x(2).split("/").length > 1).toList
    //Cleaning from the filenames
    val paths = filesNotInRoot.map(x => BFile(System.getProperty("user.dir")).relativize(BFile(x(2)).parent).toString)

    val hashes = stage_content.map(x =>x(1)).toList
    val blobs = List.fill(paths.size)("blob")
    //Merging the result
    val listTobeReturned=((paths,hashes,blobs).zipped.toList)
    listTobeReturned.map(elem => Wrapper(elem._1,elem._2,elem._3))
  }

  def deleteLineInStageIfFileAlreadyExists(pathLine: String): Unit = {
    val file = new File(stagePath)
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines.toList
    source.close()
    //Clean the file
    val writer = new PrintWriter(stagePath)
    writer.print("")
    writer.close()

    val stageContent = lines.map(x => x.split(" "))
    val stageFiltered =  stageContent.filter(x => !x(2).equals(pathLine))
    val stage: List[String] = stageFiltered.map(x => x(0)+" "+x(1)+" "+x(2)+"\n")

    stage.map(line => FilesIO.writeInFile(stagePath,line,true))//WriteInStage
  }

}
