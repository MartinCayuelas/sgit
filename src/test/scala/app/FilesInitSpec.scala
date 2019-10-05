package app

import java.nio.file.{Files, Paths}

import org.scalatest.{FlatSpec, Matchers}


class FilesInitSpec  extends FlatSpec with Matchers  {


  "Sgit initialization" should "create a .sgit folder with folders" in {
    val sgitInit = new FilesInit().initSgitRepository()
    assert(Files.exists(Paths.get(".sgit")))
  }

  it should "not re-create a sgit folder if existing already" in {
    val sgitInit = new FilesInit().initSgitRepository()

    assert(Files.notExists(Paths.get(".sgit")) === false)
  }

}
