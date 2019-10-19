package fr.cayuelas.commands

import fr.cayuelas.helpers.{HelperCheckout, HelperCommit}
import fr.cayuelas.managers.IOManager

object Checkout_cmd {

  /**
   * Main function for the checkout that dispatchs the action
   * @param args : has to be the name of a commit or tag or branch
   */
  def checkout(args: Array[String]): Unit = {
    if (args.length == 2) if(HelperCommit.existsCommit) HelperCheckout.checkout(args) else IOManager.printErrorNoCommitExisting()
    else  IOManager.numberOfArgumentNotSupported(args(0))
  }

}
