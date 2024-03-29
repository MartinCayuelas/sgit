package fr.cayuelas.commands

import java.io.File

import fr.cayuelas.helpers.{HelperBranch, HelperCommit, HelperPaths, HelperTag}
import fr.cayuelas.managers.{FilesManager, IoManager, StageManager}
import org.scalatest.{BeforeAndAfterEach, FlatSpec}


class CommandCheckoutSpec  extends FlatSpec with BeforeAndAfterEach {

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

  "the checkout command" should "modify the HEAD file if the checkout is on a branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)

    Branch_cmd.branch(Array("branch","devTest"))
    Checkout_cmd.checkout(Array("checkout","devTest"))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    ////Then
    assert(oldHeadContent != newHeadContent)
    assert(newHeadContent.equals("ref: refs/heads/devTest"))

  }

  it should "be a branch existing before doing the checkout on a branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))

    Commit_cmd.commit(Array("commit"))
    Checkout_cmd.checkout(Array("checkout","devTest"))

    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    ////Then
    assert(!newHeadContent.equals("ref: refs/heads/devTest"))

  }
  it should "be a commit existing before doing the checkout on a branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))

    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)

    Checkout_cmd.checkout(Array("checkout","devTest"))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    //Then
    assert(oldHeadContent == newHeadContent)
  }

  it should "be a commit existing before doing the checkout on a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))

    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)

    Checkout_cmd.checkout(Array("checkout","commitHash"))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    //Then
    assert(oldHeadContent == newHeadContent)
  }

  it should "be a commit existing before doing the checkout on a tag" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))

    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)
    HelperTag.createTag("tag1")
    Checkout_cmd.checkout(Array("checkout","tag1"))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    //Then
    assert(oldHeadContent == newHeadContent)
  }

  it should "be a new path when we ask for the currentBranch path when we do a checkout on other branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val oldPathRefs = HelperBranch.getCurrentBranch

    Branch_cmd.branch(Array("branch","devTest"))
    Checkout_cmd.checkout(Array("checkout","devTest"))
    val newPathRefs = HelperBranch.getCurrentBranch
    //Then
    assert(oldPathRefs != newPathRefs)
  }


  it should "be the same current branch if we checkout on a tag" in {

    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))
    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)
    HelperTag.createTag("tag1")
    Checkout_cmd.checkout(Array("checkout","tag1"))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    //Then
    assert(oldHeadContent == newHeadContent)

  }
  it should "be the same current branch if we checkout on a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val lastCommit = HelperCommit.getLastCommitInRefs()
    val oldHeadContent = IoManager.readInFile(HelperPaths.headFile)

    Checkout_cmd.checkout(Array("checkout",lastCommit))
    val newHeadContent = IoManager.readInFile(HelperPaths.headFile)
    //Then
    assert(oldHeadContent == newHeadContent)
  }

  it should "not be possible to checkout on the current Branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))

    val oldPathRefs = HelperBranch.getCurrentBranch

    Checkout_cmd.checkout(Array("checkout","master"))
    val newPathRefs = HelperBranch.getCurrentBranch
    //then
    assert(oldPathRefs == newPathRefs)
  }

  it should "not delete files never added " in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    //When
    Add_cmd.add(Array("add",helloFilePath))
    Commit_cmd.commit(Array("commit"))


    Branch_cmd.branch(Array("branch","devTest"))
    Checkout_cmd.checkout(Array("checkout","devTest"))
    //then
    assert(FilesManager.getListOfFiles(sgitPath + File.separator + "testFolder").length == 2)
  }

  it should "recreate the good files when checkout on a branch" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    //When
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))
    Commit_cmd.commit(Array("commit"))
    Branch_cmd.branch(Array("branch","devTest"))
    Checkout_cmd.checkout(Array("checkout","devTest"))
    //Then
    assert(FilesManager.getListOfFiles(sgitPath + File.separator + "testFolder").length == 2)
  }

  it should "recreate the good files when checkout on an tag" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    //When
    IoManager.writeInFile("testFolder" + File.separator + "fileForTag","tagtag",append = false)
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))
    Commit_cmd.commit(Array("commit"))
    Tag_cmd.tag(Array("tag","V1"))

    val fileForTagPath = sgitPath + File.separator + "testFolder" + File.separator + "fileForTag"
    Add_cmd.add(Array("add",fileForTagPath))
    Commit_cmd.commit(Array("commit"))
    Checkout_cmd.checkout(Array("checkout","V1"))
    //Then
    assert(FilesManager.getListOfFiles(sgitPath + File.separator + "testFolder").length == 2)
  }

  it should "change the id of the last commit in the branch refs if checkout on a tag" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    //When
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))
    Commit_cmd.commit(Array("commit"))

    val lastCommit = HelperCommit.getLastCommitInRefs()
    Tag_cmd.tag(Array("tag","V1"))
    Checkout_cmd.checkout(Array("checkout","V1"))
    val lastCommitTag1 = HelperCommit.getLastCommitInRefs()
    //then
    assert(lastCommit.equals(lastCommitTag1))
  }

  it should "change the content of the stage of the last commit if checkout on a commit" in {
    //Given
    val sgitPath = HelperPaths.sgitPath
    val helloFilePath = sgitPath + File.separator + "testFolder" + File.separator + "hello"
    val worldFilePath = sgitPath + File.separator + "testFolder" + File.separator + "world"
    //When
    Add_cmd.add(Array("add",helloFilePath,worldFilePath))
    Commit_cmd.commit(Array("commit"))
    val lastCommit = HelperCommit.getLastCommitInRefs()
    val stage = IoManager.readInFileAsLine(StageManager.currentStagePath)
    val fileForTagPath = sgitPath + File.separator + "testFolder" + File.separator + "fileForTag"
    Add_cmd.add(Array("add",fileForTagPath))
    Commit_cmd.commit(Array("commit"))

    Checkout_cmd.checkout(Array("checkout",lastCommit))
    val stage2 = IoManager.readInFileAsLine(StageManager.currentStagePath)
    //Then
    assert(stage.length == stage2.length)
  }

}
