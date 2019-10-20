#  Sgit
It's a simple implementation of Git in Scala. This project was realized in 2 and a half weeks as part of a functional programming project
 

#  Installation

```shell script
$ cd
$ git clone https://github.com/MartinCayuelas/sgit.git
$ cd sgit
$ source install.sh
$ cd ..
$ mkdir yourOwnFolder
$ cd yourOwnFolder
```

Now, you can enjoy **Sgit**

#  Tests

```shell script
$ cd sgit
$ sbt test
```
Now, you can test **Sgit**
# Features

## Create:
    
-   sgit init ✅ 
    
## Local Changes:
    
-   sgit status ✅ 
   
-   sgit diff ✅
    
-   sgit add <filename/filenames or . or regexp> ✅
    
-   sgit commit ✅ (You can use -m yourMessage also)
    
## Commit History:
    
-   sgit log  ✅
    Show all commits started with newest
    
-   sgit log -p  ✅
    Show changes overtime
    
-   sgit log --stat ✅
   Show stats about changes overtime  
      
    
## Branches and Tags
    

-   sgit branch <branchname>  ✅
    
-   sgit branch -av  ✅
  
-   sgit checkout  ⚠ (allows to change branch or tag or commit but there is some anomalies in the files recreated sometimes)
    
-   sgit tag <tagname>  ✅
      
    
## Merge & Rebase
    

-   sgit merge <branch>  ❌
    
-   sgit rebase <branch> ❌
    
-   sgit rebase -i <commit hash or banch name> ❌
