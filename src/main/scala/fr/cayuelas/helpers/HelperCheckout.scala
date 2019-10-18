package fr.cayuelas.helpers

import java.io.File

import fr.cayuelas.managers.{FilesManager, IOManager, StageManager}
import fr.cayuelas.objects.Wrapper

object HelperCheckout {

  def checkout(args: Seq[String]): Unit = {
    val valueForCheckout = args(1)
    valueForCheckout match {
      case x if HelperBranch.isABranch(x) => checkoutBranch(x)
      case y if HelperTag.isATag(y) => checkoutCommitOrTag(y,isTag = true)
      case z if HelperCommit.isACommit(z)=> checkoutCommitOrTag(z,isTag = false)
      case _ => IOManager.printErrorCheckout()
    }
  }


  def checkoutBranch(nameBranch: String): Unit = {
    if(HelperBranch.getCurrentBranch.equals(nameBranch)) IOManager.printErrorOnCheckoutSameBranch(nameBranch)
    else{

      val stage: List[Wrapper] = IOManager.readInFileAsLine(StageManager.currentStagePath).map(x => x.split(" ")).map(blob => Wrapper(blob(2),blob(1),"Blob",""))

      HelperBranch.setNewBranchInHEAD(nameBranch)
      val lastCommit = HelperCommit.getLastCommitInRefs()
      val blobsRetrieved: List[Wrapper] = HelperCommit.getAllBlobsFromCommit(lastCommit).map(blob => Wrapper(blob._2,blob._1,"Blob",""))
      routineCheckout(blobsRetrieved, stage)
    }
  }

  def checkoutCommitOrTag(name: String, isTag: Boolean): Unit = {
    val stage: List[Wrapper] = IOManager.readInFileAsLine(StageManager.currentStagePath).map(x => x.split(" ")).map(blob => Wrapper(blob(2),blob(1),"Blob",""))
    val blobsRetrieved: List[Wrapper] = isTag match {
      case true => HelperCommit.getAllBlobsFromCommit(IOManager.readInFile(HelperPaths.tagsPath+File.separator+name)).map(blob => Wrapper(blob._2,blob._1,"Blob",""))
      case false => HelperCommit.getAllBlobsFromCommit(name).map(blob => Wrapper(blob._2,blob._1,"Blob",""))
    }
    routineCheckout(blobsRetrieved,stage)
  }


  def routineCheckout(blobsRetrieved: List[Wrapper], stage : List[Wrapper]): Unit = {
    stage.map(line => FilesManager.deleteFile(line.path))
    StageManager.clearStage(HelperPaths.stagePath+File.separator+"stageToCommit")
    StageManager.clearStage(HelperPaths.stagePath+File.separator+"stageValidated")
    blobsRetrieved.map(blob => {
      FilesManager.createNewFile(HelperPaths.sgitPath+blob.path)
      val contentBlob = HelperBlob.readContentInBlob(blob.hash)
      contentBlob.map(l =>  {
        IOManager.writeInFile(HelperPaths.sgitPath+blob.path,l+"\n",append = true)
        IOManager.writeInFile(HelperPaths.stagePath+File.separator+HelperBranch.getCurrentBranch,l+"\n",append = true)
      })
    })
  }



}
