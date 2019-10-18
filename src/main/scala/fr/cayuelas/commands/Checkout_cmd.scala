package fr.cayuelas.commands

import fr.cayuelas.helpers.{HelperCheckout, HelperCommit}
import fr.cayuelas.managers.IOManager

object Checkout_cmd {

  /**
   *
   * @param args
   */
  def checkout(args: Array[String]): Unit = {
    if (args.length == 2) if(HelperCommit.existsCommit) HelperCheckout.checkout(args) else IOManager.printErrorNoCommitExisting()
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }

}
