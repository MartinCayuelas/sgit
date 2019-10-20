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
If you can't launch later, you can add this to your PATH 
  ```shell script
  export PATH="/Your/Path/to/sgit/target/scala-2.13/:$PATH"
  alias sgit="sgit-assembly-0.1.jar"
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

Allows you to create a .sgit repository
```shell script
sgit init
``` 
    
## Local Changes:

This commands shows the state of each file.(New or modified)
```shell script
sgit status
``` 
This command allows to see differences between files stage or not if they are modified

```shell script
sgit diff
``` 
This command allows to add files or folder or files with regex expression. The deletion isn't handled
```shell script
sgit add <filename/filenames or . or regexp or folder>
```     

This command is used to do a record of the stage and to have history of it. With a message or not
```shell script
sgit commit 
sgit commit -m <message>
```     
        
    
## Commit History:
    
This command is used to list the version history for all the branches.
-p and --stat offer more specific information
```shell script
sgit log
sgit log -p
sgit log --stat
```     

## Branches and Tags
    
This command is used to create a new branch linked to the last commit.
```shell script
sgit branch <nameBranch>
``` 
This command is used to display all the branches and tags created. Shows also the current branch 
```shell script
sgit branch -av
``` 
  
This command is used to switch from one branch to another or to a specific commit or to a specific tag. It's possible there are some bugs with folder not deleted
```shell script
sgit checkout <nameBranch or nameTag or commitHash>
``` 
 
This command is used to create a new tag linked to the last commit.
```shell script
sgit tag <nameTag>
``` 
      
## Merge & Rebase
    

-   sgit merge <branch>  ❌
    
-   sgit rebase <branch> ❌
    
-   sgit rebase -i <commit hash or banch name> ❌
