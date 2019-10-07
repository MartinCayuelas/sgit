package app.commands

import app.FilesIO

object Tag {
  /*
  TAG---------------------
   */

  def tag(args: Array[String]): Unit = {
    if (args.length == 2)  FilesIO.createTag(args(1))
    else  println(s"Number of arguments not supported for the command '${args(0)}'.")
  }
  /*
  ---------------------
   */

}
