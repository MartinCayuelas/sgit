package fr.cayuelas.commands
import java.io.File

import fr.cayuelas.helpers.{HelperPaths, HelperSha1}
import fr.cayuelas.managers.{FilesManager, IoManager, StageManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandAddSpec  extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IoManager.writeInFile("testFolder" + File.separator + "hello","hello",false)
    IoManager.writeInFile("testFolder" + File.separator + "world","world",false)
  }

  //delete all files created in the .sgit directory after each test
  override def afterEach(): Unit = {
    val sgit = new File(".sgit")
    if (sgit.exists()) {
      delete(new File(".sgit"))
    }
    val testFolder = new File("testFolder")
    if (testFolder.exists()) {
      delete(new File("testFolder"))
    }

  }

  def delete(file: File): Unit = {
    if (file.isDirectory) {
      file.listFiles().foreach(delete)
    }
    file.delete()
  }

  "the add command" should "create a blob file which correspond to the content of the file added" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    val contentFile = IoManager.readInFileAsLine(helloFilePath).mkString
    val sha1Id = HelperSha1.convertToSha1(contentFile)

    //When
    Add_cmd.add(Array("add",helloFilePath))
    val blobPath = sgitPath + File.separator + ".sgit" + File.separator + "objects" + File.separator + "blobs"+ File.separator + sha1Id.substring(0,2) + File.separator + sha1Id.substring(2,sha1Id.length)
    val contentBlob = IoManager.readInFileAsLine(blobPath).mkString
    //Then
    assert(contentBlob == contentFile)
  }

  it should "add multiple files in the index file if mutiples files are added at the same time" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"

    //When
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))

    val contentFileHello = IoManager.readInFile(helloFilePath)
    val sha1IHello = HelperSha1.convertToSha1(contentFileHello)

    val contentFileWorld= IoManager.readInFile(worldFilePath)
    val sha1IdWorld = HelperSha1.convertToSha1(contentFileWorld)


    val contentOfStageCommit = IoManager.readInFile(StageManager.stageToCommitPath)


    val expextedContent = "Blob "+sha1IHello+" "+"testFolder" + File.separator + "hello"+"\n"+"Blob "+sha1IdWorld+" "+"testFolder" + File.separator + "world"+"\n"

    //Then
    assert(contentOfStageCommit == expextedContent)

  }

  it should "add multiple files in the objects/blobs directory if mutiples files are added at the same time" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    //When
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))

    val numberOfBlobs = FilesManager.getListOfContentInDirectory(HelperPaths.objectsPath+File.separator+"blobs")
    assert(numberOfBlobs.length == 2)
  }


  it should "updates the stageCommit in there is modification" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    val previousSha1 = IoManager.readInFileAsLine(StageManager.stageToCommitPath).head.split(" ")(1)
    IoManager.writeInFile(helloFilePath,"HelloWorld", append = true)
    Add_cmd.add(Array("add",helloFilePath))
    val newSha1 = IoManager.readInFileAsLine(StageManager.stageToCommitPath).head.split(" ")(1)
    //THEN
    assert(newSha1 != previousSha1)
  }

  it should "never add a file in stageCommit if the user doesn't give file in argument" in {
    //Given And when
    Add_cmd.add(Array("add"))
    val stageCommit = IoManager.readInFileAsLine(StageManager.stageToCommitPath)
    //Then
    assert(stageCommit.isEmpty)
  }

  it should "never add a file in stageCommit if the user give a file that doesn't exists" in {
    //Given And when
    Add_cmd.add(Array("add","FileNotExisting"))
    val stageCommit = IoManager.readInFileAsLine(StageManager.stageToCommitPath)
    //Then
    assert(stageCommit.isEmpty)
  }



}
