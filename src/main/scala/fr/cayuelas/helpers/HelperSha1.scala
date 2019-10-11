package fr.cayuelas.helpers


import java.security.MessageDigest

object HelperSha1 {
  def convertToSha1(string : String): String ={
    MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }

}
