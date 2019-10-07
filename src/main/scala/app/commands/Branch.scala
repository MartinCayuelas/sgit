package app.commands

import app.FilesIO

object Branch {
  /*
  BRANCH---------------------
   */

  def branch(args: Array[String]): Unit = {
    if ((args.length == 2) && (args(1).equals("-av"))) {
      FilesIO.displayAllBranches()
      FilesIO.displayAllTags()
    }
    else if (args.length == 2) FilesIO.createBranch(args(1))
    else println("Number of arguments not supported for the command 'branch'.")
  }


}
