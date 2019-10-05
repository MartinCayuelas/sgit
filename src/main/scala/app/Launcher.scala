package app

object Launcher extends App {
  val repository = new FilesInit()
  def launcher(args: Array[String]): Unit =  {

    args match {
      case Array("init", _) => println("No argument(s) expected.")
      case Array("init") => repository.initSgitRepository()

      case _ => println("Bad")
    }

  }

  launcher(args)
}