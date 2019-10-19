package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{FilesManager, IoManager, StageManager}
import fr.cayuelas.objects.Wrapper

object HelperCheckout {
  /**
   *Dispatchs and verify if the string given in parameter is a tag or branch or commit or not exists
   * @param args : should be the name of a tag or branch or a commit id
   */
  def checkout(args: Seq[String]): Unit = {
      args(1) match {
      case x if HelperBranch.isABranch(x) => checkoutBranch(x)
      case y if HelperTag.isATag(y) => checkoutCommitOrTag(y,isTag = true)
      case z if HelperCommit.isACommit(z)=> checkoutCommitOrTag(z,isTag = false)
      case _ => IoManager.printErrorCheckout()
    }
  }

  /**
   * Do the process of checkout, display an error if the branch we want to go is the same that we are currently
   * @param nameBranch : name of the branch we want to go
   */
  def checkoutBranch(nameBranch: String): Unit = {
    if(HelperBranch.getCurrentBranch.equals(nameBranch)) IoManager.printErrorOnCheckoutSameBranch(nameBranch)
    else{
      val stage: List[Wrapper] = IoManager.readInFileAsLine(StageManager.currentStagePath).map(_.split(" ")).map(blob => Wrapper(blob(2),blob(1),"Blob",""))
      StageManager.clearStage(StageManager.currentStagePath)
      HelperBranch.setNewBranchInHEAD(nameBranch)
      val lastCommit = HelperCommit.getLastCommitInRefs()
      val blobsRetrieved: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(lastCommit)
      routineCheckout(blobsRetrieved, stage)
    }
  }

  /**
   * Retrieves the blobs for a tag or a commit and calls the routineCheckout to do the rest
   * @param name : name of the tag or the commit we whant to go
   * @param isTag : indicates if it is a tag or commit (true if tag, false if it's commit)
   */
  def checkoutCommitOrTag(name: String, isTag: Boolean): Unit = {
    val stage: List[Wrapper] = IoManager.readInFileAsLine(StageManager.currentStagePath).map(_.split(" ")).map(blob => Wrapper(blob(2),blob(1),"Blob",""))
    StageManager.clearStage(StageManager.currentStagePath)

    val blobsRetrieved: List[Wrapper] = isTag match {
      case true => HelperCommit.getAllBlobsFromCommit(IoManager.readInFile(HelperPaths.tagsPath+File.separator+name))
      case false => HelperCommit.getAllBlobsFromCommit(name)
    }

    if (isTag) IoManager.writeInFile(HelperPaths.branchesPath+File.separator+HelperBranch.getCurrentBranch,IoManager.readInFile(HelperPaths.tagsPath+File.separator+name),append = false)
    else IoManager.writeInFile(HelperPaths.branchesPath+File.separator+HelperBranch.getCurrentBranch,name,append = false)

    routineCheckout(blobsRetrieved,stage)
  }

  /**
   * Creates a file if doesn't exists then write in to do all the modifications do get the same working directory as the commit we want to go
   * @param blobsRetrieved: List of blobs for the commit or the tag or the branch given
   * @param stage: List of elements that are in the stage
   */
  def routineCheckout(blobsRetrieved: List[Wrapper], stage : List[Wrapper]): Unit = {

    stage.map(line => FilesManager.deleteFile(line.path))

    StageManager.clearStage(HelperPaths.stagePath+File.separator+"stageToCommit")
    StageManager.clearStage(HelperPaths.stagePath+File.separator+"stageValidated")

    blobsRetrieved.map(blob => {
      IoManager.writeInFile(StageManager.currentStagePath,blob.typeElement+" "+blob.hash+" "+blob.path+"\n",append = true)
      FilesManager.createNewFile(HelperPaths.sgitPath+blob.path)
      val contentBlob = HelperBlob.readContentInBlob(blob.hash)
      contentBlob.map(l =>  {
        IoManager.writeInFile(HelperPaths.sgitPath+blob.path,l+"\n",append = true)
      })
    })
  }
}
