#!/bin/sh
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
scala "$DIR/target/scala-2.13/sgit-assembly-0.1.jar" $@