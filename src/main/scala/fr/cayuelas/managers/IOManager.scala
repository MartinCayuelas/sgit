package fr.cayuelas.managers


import java.io.{BufferedWriter, File, FileWriter}

import scala.annotation.tailrec

object IOManager {

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param path of file in which we want to write
   * @param content to write in the file
   * @param append could be True or False if we want to rewrite the content or append it
   */
  def writeInFile(path: String, content: String, append: Boolean): Unit = {
    val file = new File(path)
    val bw = new BufferedWriter(new FileWriter(file, append))
    bw.write(content)
    bw.close()
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a String (Could be an empty string if the file is empty)
   */

  def readInFile(pathToFile: String): String ={
    val source = scala.io.Source.fromFile(pathToFile)
    val content = try source.mkString finally source.close()
    content
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a List
   */
  def readInFileAsLine(pathToFile: String): List[String] = {
    val file = new File(pathToFile)
    val source = scala.io.Source.fromFile(file)
    val lines = source.getLines.toList
    source.close()
    lines
  }

  /*
  Println part
   */

  def noArgumentsExpected() : Unit = println("No argument(s) expected.")
  def numberOfArgumentNotSupported(cmd: String): Unit = println(s"Number of arguments not supported for the command '${cmd}'.")
  def notSgitReposiroty(): Unit = println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
  def notExistingCommand(): Unit = println("This command doesn't exists")
  def argumentNotSupported(): Unit =println("Argument not supported.")
  /*
  INIT
   */
  def emptyRepositoryInitialized(path: String): Unit = println(s"Empty Sgit repository initialized in ${path}/.sgit/")
  def sgitRepoAlreadyExists(): Unit = println(s"Sgit repository already exists.")

  /*
  ADD
   */
  def specifyFileOrFolder(): Unit = println("You need to specify a folder or a file")
  def printFatalAdd(f: File): Unit = println(s"fatal: the path ${f.getName} does not correspond to any file")
  /*
  BRANCH
   */
  def printBranch(nameBranch: String): Unit = println("\n"+Console.GREEN+"("+nameBranch+")")
  def printFatalError(nameBranch: String): Unit = println(s"fatal: your current '${nameBranch}' branch does not yet contain any log")
  /*
  TAG
   */
  def printFatalCreation(typeE: String, name: String): Unit = println(s"Fatal: a ${typeE} named ${name} is exits already")
  /*
  COMMIT
   */
  def nothingToCommit(): Unit = println("Nothing to commit")
  def printErrorNoCommitExisting(): Unit = println("There is no commit yet, you must commit something before")

  /*
  DIFF
   */
  def printDiffForFile(path: String, sha1: String): Unit = {
    println(s"diff --sgit a/${path} b/${path}\nindex ${sha1.substring(0, 7)}..${sha1.substring(sha1.length - 7, sha1.length)}\n--- a/${path}\n+++ b/${path}\n")
  }
  /**
   * Prints the différence with the deltas given
   * In green if content is added else in red
   *
   * @param deltas : List containing the différences
   */
  @tailrec
  def printDiff(deltas: List[String]): Unit = {
    if (deltas.nonEmpty) {
      if (deltas.head.startsWith("+")) println(Console.GREEN + deltas.head + Console.RESET)
      else println(Console.RED + deltas.head + Console.RESET)
      printDiff(deltas.tail)
    }
  }

  /*
  CHECKOUT
   */
  def printSuccessCheckoutBranch(nameBranch: String): Unit = println(s"Tipping on the branch ${nameBranch}")

  def printErrorCheckout(): Unit = println("There is no branch or tag or commit corresponding. Please try again with a rigth name")
  def printErrorOnCheckoutSameBranch(nameBranch: String): Unit = println(s"You are already on the branch ${nameBranch}")
}
