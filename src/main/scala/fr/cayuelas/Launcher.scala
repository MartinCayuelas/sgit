package fr.cayuelas

import fr.cayuelas.commands.Add_cmd.add
import fr.cayuelas.commands.Branch_cmd.branch
import fr.cayuelas.commands.Commit_cmd.commit
import fr.cayuelas.commands.Diff_cmd.diff
import fr.cayuelas.commands.Init_cmd
import fr.cayuelas.commands.Init_cmd.init
import fr.cayuelas.commands.Log_cmd.log
import fr.cayuelas.commands.Status_cmd.status
import fr.cayuelas.commands.Tag_cmd.tag
import fr.cayuelas.commands.Checkout_cmd.checkout
import fr.cayuelas.managers.IoManager

object Launcher extends App {

  dispatcher(args)

  def dispatcher(args: Array[String]): Unit =  {
    val isInSgitRepository: Boolean = Init_cmd.isInSgtRepository(System.getProperty("user.dir"))

    args match {
      //Init
      case Array("init", _) => IoManager.noArgumentsExpected()
      case Array("init") => init(System.getProperty("user.dir"))

      case Array(_*) if (!isInSgitRepository) => IoManager.notSgitRepository()
      //ADD
      case Array("add", _*) => add(args)
        //Status
      case Array("status", _) => IoManager.noArgumentsExpected()
      case Array("status") => status()
      //Diff
      case Array("diff", _) => IoManager.noArgumentsExpected()
      case Array("diff") =>  diff()
      //Commit
      case Array("commit", _*) => commit(args)
      case Array("commit") => commit(args)
      //Log
      case Array("log", _*) => log(args)
      //Branch
      case Array("branch", _*)  => branch(args)
      //Checkout
      case Array("checkout", _*)  => checkout(args)
      //tag
      case Array("tag", _*)  => tag(args)
      //Merge
      case Array("merge", _*) => merge(args)
      //Rebase
      case Array("rebase", _*) => rebase(args)
      //Default Case
      case _ => IoManager.notExistingCommand()
    }
  }

  def merge(args: Array[String]): Unit = {
    if (args.length == 2) println(s"merge")
    else  IoManager.numberOfArgumentNotSupported(args(0))
  }
  def rebase(args: Array[String]): Unit = {
    if ((args.length == 3) && args(1).equals("-i")) println(s"rebase -i")
    else if (args.length == 2)  println(s"rebase")
    else  IoManager.numberOfArgumentNotSupported(args(0))
  }

}
