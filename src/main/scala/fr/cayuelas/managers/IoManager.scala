package fr.cayuelas.managers


import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import fr.cayuelas.helpers.HelperBranch
import fr.cayuelas.managers.LogsManager.logFormatting
import fr.cayuelas.objects.Commit

import scala.annotation.tailrec

object IoManager {

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param path of file in which we want to write
   * @param content to write in the file
   * @param append could be True or False if we want to rewrite the content or append it
   */
  def writeInFile(path: String, content: String, append: Boolean): Unit = {
    val file = new File(path)
    if(file.exists()){
      val bw = new BufferedWriter(new FileWriter(file, append))
      bw.write(content)
      bw.close()
    }
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a String (Could be an empty string if the file is empty)
   */

  def readInFile(pathToFile: String): String = {
    val file = new File(pathToFile)
    if(file.exists()) {
      val source = scala.io.Source.fromFile(pathToFile)
      val content = try source.mkString finally source.close()
      content
    }else ""
  }

  /**
   *General function that write in files (For commit, tree, blob, stage)
   * @param pathToFile in which we want to read the content
   * @return the content of a file in a List
   */
  def readInFileAsLine(pathToFile: String): List[String] = {
    val file = new File(pathToFile)
    if(file.exists()){
      val source = scala.io.Source.fromFile(file)
      val lines = source.getLines.toList
      source.close()
      lines
    }else List()

  }

  /**
   * Function that clears the content of a File
   * @param path : path of the file that will be cleared
   */
  def clearFile(path: String): Unit ={
    val writer = new PrintWriter(path)
    writer.print("")
    writer.close()
  }

  /*
  Println part
   */

  def noArgumentsExpected() : Unit = println("No argument(s) expected.")
  def numberOfArgumentNotSupported(cmd: String): Unit = println(s"Number of arguments not supported for the command '${cmd}'.")
  def notSgitRepository(): Unit = println("fatal: neither this nor any of its parent directories is a sgit: .sgit repository")
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
  def printNonCurrentBranch(nameBranch: String): Unit = println(s"  ${nameBranch} (branch)")
  def printCurrentBranch(nameBranch: String): Unit = println(s"* ${nameBranch} (branch)")
  def printSeparator(): Unit =println("   ------------")

  /*
  STATUS
   */
  def printToBValidatedInfos(nameBranch: String): Unit = println(s"On the ${nameBranch} branch\nChanges that will be validated : \n")
  def printNotValidatedInfos(): Unit = println("\nChanges that will not be validated:\n   (use \"git add <file> ...\" to update what will be validated)\n")
  def printUntrackedInfos(): Unit = println("\nFiles untracked:\n   (use \"git add <file> ...\" to include what will be validated)\n")
  def printElemValidated(elem: String) : Unit =  println(s"   ${Console.GREEN}"+elem+Console.RESET)
  def printElemNotValidated(e: String) : Unit =  println(s"   ${Console.RED}modified : "+e+Console.RESET)
  def printElemUntracked(e: String) : Unit =    println(s"   ${Console.RED}"+e+Console.RESET)

  /*
  TAG
   */
  def printFatalCreation(typeE: String, name: String): Unit = println(s"Fatal: a ${typeE} named ${name} is exits already")
  def printTag(tag: String): Unit = println(s"  ${tag} (tag)")
  /*
  COMMIT
   */
  def nothingToCommit(): Unit = println("Nothing to commit")
  def printErrorNoCommitExisting(): Unit = println("There is no commit yet, you must commit something before")

  def printResultCommit(numberOfChanges: Int, commit: Commit, inserted: Int, deleted: Int): Unit = {
    if(deleted > 0){
      val resToPrint = numberOfChanges match {
        case  1 => "["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} file changed, ${inserted} insertions(+), ${deleted} deletions(-)"
        case _ => "["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} files changed, ${inserted} insertions(+), ${deleted} deletions(-)"
      }
      println(resToPrint)
    }else{
      val resToPrint = numberOfChanges match {
        case  1 => "["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} file changed, ${inserted} insertions(+)"
        case _ => "["+HelperBranch.getCurrentBranch+" "+commit.idCommit.substring(0,8)+"] "+commit.message+s"\n  ${numberOfChanges} files changed, ${inserted} insertions(+)"
      }
      println(resToPrint)
    }
  }

  /*
  DIFF
   */
  def printDiffForFile(path: String, sha1: String): Unit = println(s"diff --sgit a/${path} b/${path}\nindex ${sha1.substring(0, 7)}..${sha1.substring(sha1.length - 7, sha1.length)}\n--- a/${path}\n+++ b/${path}\n")

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
  LOGS
   */

  def printLogFormated(log: String): Unit =  println(logFormatting(log)) //Format Display
  /**
   *Print the stat (insertion and deletion) for a file
   * @param path: path of the file
   * @param typePrint: if it is insertion or deletion
   * @param changes number of changes (insertion +deletion)
   */
  def printLineStat(path: String, typePrint: String, changes: Int): Unit ={
    typePrint match {
      case x if x.equals("+") =>println(path + " "*15+"|" + changes + Console.GREEN +"+"*changes+Console.RESET)
      case y if y.equals("-") => println(path + " "*15+"|"+ changes + Console.RED +"-"*changes+Console.RESET)
      case _ =>  println(path + " "*15+"|"  + changes + Console.GREEN+"+"+Console.RESET+Console.RED+"-"+Console.RESET)
    }
  }

  /**
   * Display the changes
   * @param filesChanged: number of files changed
   * @param inserted: number of insertions
   * @param deleted number of deletions
   */
  def printChanges(filesChanged: Int,inserted: Int,deleted: Int): Unit = {
    val resToPrint = filesChanged match {
      case  1 => filesChanged +s" file changed, ${inserted} insertions(+), ${deleted} deletions(-)"
      case _ => filesChanged +s" files changed, ${inserted} insertions(+), ${deleted} deletions(-)"
    }
    println(resToPrint)
  }

  /*
  CHECKOUT
   */
  def printSuccessCheckoutBranch(nameBranch: String): Unit = println(s"Tipping on the branch ${nameBranch}")
  def printErrorCheckout(): Unit = println("There is no branch or tag or commit corresponding. Please try again with a rigth name")
  def printErrorOnCheckoutSameBranch(nameBranch: String): Unit = println(s"You are already on the branch ${nameBranch}")
}
