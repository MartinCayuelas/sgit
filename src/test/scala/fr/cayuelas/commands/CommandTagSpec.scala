package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.{HelperCommit, HelperPaths, HelperTag}
import fr.cayuelas.managers.{FilesManager, IOManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}

class CommandTagSpec   extends FlatSpec with BeforeAndAfterEach {

  //init an .sgit repo and testFolder repo before each test
  override def beforeEach(): Unit = {
    Init_cmd.init(System.getProperty("user.dir"))
    new File( "testFolder").mkdir()

    FilesManager.createNewFile("testFolder" + File.separator + "hello")
    FilesManager.createNewFile("testFolder" + File.separator + "world")
    IOManager.writeInFile("testFolder" + File.separator + "hello","hello",false)
    IOManager.writeInFile("testFolder" + File.separator + "world","world",false)
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

  "The tag command" should "create a file in .sgit/refs/tags" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperTag.createTag("Tag1")

    assert(new File(HelperPaths.tagsPath+File.separator+"Tag1").exists())
    assert(FilesManager.getListOfFiles(HelperPaths.tagsPath).length == 1)
  }

  it should "be the right content in the file created" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperTag.createTag("Tag1")
    assert(new File(HelperPaths.tagsPath+File.separator+"Tag1").exists())
    val contentNewTagFile = IOManager.readInFile(HelperPaths.tagsPath+File.separator+"Tag1")
    assert(contentNewTagFile == HelperCommit.getLastCommitInRefs())
  }

  it should "not create a new tag if already exists" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))
    HelperTag.createTag("Tag1")
    HelperTag.createTag("Tag1")
    //Then
    assert(FilesManager.getListOfFiles(HelperPaths.tagsPath).length == 1)
  }

  it should "not create a new tag if there is no commit" in {
    HelperTag.createTag("Tag1")
    assert(!new File(HelperPaths.tagsPath+File.separator+"Tag1").exists())

  }

  it should "be the same content in a tag if there is no new commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"

    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    HelperTag.createTag("Tag1")
    HelperTag.createTag("Tag2")
    assert(new File(HelperPaths.tagsPath+File.separator+"Tag1").exists())
    assert(new File(HelperPaths.tagsPath+File.separator+"Tag2").exists())
    val contentNewTagFile = IOManager.readInFile(HelperPaths.tagsPath+File.separator+"Tag1")
    val contentNewTagFile2 = IOManager.readInFile(HelperPaths.tagsPath+File.separator+"Tag2")
    assert(contentNewTagFile == HelperCommit.getLastCommitInRefs())
    assert(contentNewTagFile2 == HelperCommit.getLastCommitInRefs())
    assert(contentNewTagFile == contentNewTagFile2)

  }


}
