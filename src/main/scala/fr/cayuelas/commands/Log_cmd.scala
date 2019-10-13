package fr.cayuelas.commands

import fr.cayuelas.managers.{IOManager, LogsManager}

object Log_cmd {

  /**
   * Main Function that dispatchs action for the command Log
   * @param args : Contains the argument of thecommand to dispatch the good action
   */
  def log(args: Array[String]) : Unit ={
    if (args.length == 1) displayLogs()
    else if(args.length == 2) {
      args match {
        case Array(_,"-p") => println("log -p")
        case Array(_,"--stat") => println("log --stat")
        case _ => IOManager.argumentNotSupported()
      }
    }
    else IOManager.numberOfArgumentNotSupported(args(0))
  }

  /**
   * Function that displays all the logs for the current branch
   */
  def displayLogs() : Unit = {
    val  listLogs = LogsManager.getCurrentLogs
    listLogs match {
      case Nil => IOManager.printFatalError()
      case _ => {
       IOManager.printCurrentBranch()
        listLogs.map(log => println(logFormatting(log)))
      }
    }
  }

  /**
   * Method that transform a given string in other
   * @param stringToFormat : the string tha will be formated
   * @return a string to be displayed
   */
  def logFormatting(stringToFormat : String) : String = {
    val splitedString = stringToFormat.split(" ")
    s"${Console.YELLOW}commit ${splitedString(1)} ${Console.RESET} \nAuthor: ${splitedString(2)}\nDate:   ${splitedString(3)} ${splitedString(4)} ${splitedString(5)} ${splitedString(6)} ${splitedString(8)} +0200\n"
  }





}
