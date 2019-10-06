package app

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.{Files, Paths}

object FilesIO {
  /*
   * initSgitRepository:
   * Method that create all the necessary folders for sgit
   * Inform the user with a warning message if sommething went wrong (i.e .sgit already exists) or Inform the user that all is successful
   */

  def initSgitRepository() : Unit = {
    val listFolders = List("objects", "objects/blobs", "objects/trees","objects/commits", "refs","refs/heads", "refs/tags","logs")
    val listFiles = List("HEAD","STAGE_AREA")
    val path = Paths.get("").toAbsolutePath().toString()
    val sgitPath = path + File.separator +  ".sgit"
    val sgitRepository = new File(".sgit")

    if(Files.notExists(Paths.get(".sgit"))){
      sgitRepository.mkdir()
      listFolders.map( folder => new File(sgitPath + File.separator +  folder).mkdir())
      listFiles.map(file => new File(sgitPath + File.separator + file).createNewFile())
      new File(Paths.get(".sgit").toAbsolutePath().toString().concat("/refs/heads/master")).createNewFile()
      writeHead()
      println(s"Empty Git repository initialized in ${path}/.sgit/")
    } else {
      println(s"Sgit repository already exists.")
    }
  }


  def createBlob(f: File): String = {

    val source = scala.io.Source.fromFile(f.getAbsoluteFile)
    val content = try source.mkString finally source.close()
    val idSha1 = HelpersApp.convertToSha1(content)
    val contentBlob = content.getBytes.toString

    addBlob(idSha1, contentBlob)
    val blob = s"Blob ${idSha1} ${f.getName}\n"
    return blob


  }

  def addBlob(idSha1: String, contentBlob: String): Unit = {
    val path = Paths.get(".sgit/objects/blobs").toAbsolutePath().toString()
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)

    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder+File.separator+nameFile).createNewFile()


    writeBlob(path + File.separator +  folder+File.separator+nameFile,contentBlob)

  }

  def addTree(idSha1: String, contentTree: List[String]): Unit = {
    val path = Paths.get(".sgit/objects/trees").toAbsolutePath().toString()
    val folder = idSha1.substring(0,2)
    val nameFile = idSha1.substring(2,idSha1.length)
    new File(path + File.separator +  folder).mkdir()
    new File(path + File.separator +  folder + File.separator + nameFile).createNewFile()
    val contentToWrite = contentTree.reduce(_.concat(_))

    writeTree(path + File.separator +  folder + File.separator + nameFile,contentToWrite)

  }

  /*

   */

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

  /*
  Branches--------------
   */

  def createBranch(nameBranch: String): Unit = {
    val path = Paths.get(s".sgit/refs/heads/${nameBranch}")
    if(Files.notExists(path)){
      new File(path.toString).createNewFile()
    }else {
      println(s"Fatal: a branch named ${nameBranch} is exits already")
    }
  }
  def getCurrentBranch(): String = {
    val path = Paths.get(".sgit/").toAbsolutePath().toString().concat("/HEAD")
    val source = scala.io.Source.fromFile(path)
    val content = try source.mkString finally source.close()
    val pattern = "([A-Za-z]+)(:) ([A-Za-z]+)(/)([A-Za-z]+)(/)([A-Za-z]+)".r
    val pattern(ref, a, refs,b,heads,c,currentBranch) = content
    return currentBranch

  }
  def displayAllBranches(): Unit = {
    val currentBranch = getCurrentBranch()
    val listOfBranches = FilesManager.getListOfFiles(Paths.get(".sgit/refs/heads").toAbsolutePath.toString)
    listOfBranches.map(b =>{
      if(currentBranch.equals(b.getName)){
        println(s"* ${b.getName}")
      }else{
        println(s"  ${b.getName}")
      }
    })
  }

  /*
  Tags------------------------
   */


  def createTag(nameTag: String): Unit = {
    if(Files.notExists(Paths.get(Paths.get(".sgit/refs/tags").toAbsolutePath().toString()+File.separator+nameTag))){
      val path =Paths.get(".sgit/refs/heads").toAbsolutePath().toString()
      new File(path + File.separator +  nameTag).createNewFile()
    }else {
      println(s"Fatal: a branch named ${nameTag} is exits already")
    }
  }
}
