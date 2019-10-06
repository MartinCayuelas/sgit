package app

import java.security.MessageDigest

object HelpersApp {
  def convertToSha1(string : String): String ={
    MessageDigest.getInstance("SHA-1").digest(string.getBytes("UTF-8")).map("%02x".format(_)).mkString
  }
}