#!/bin/bash
# usage: thunderingherd.sh <relative or absolute path to the directory that contains config.properties>
# For example:    target/thunderingherd.sh src/main/resources/

VERSION=1.0
MAIN_JAR_NAME=asyncstreamingclient
CONFIG_FILE_NAME=config.properties

#Find the the absolute path to the directory containing the config file.
REL_CONFIG_PATH=$1
ABS_CONFIG_PATH=`cd "${REL_CONFIG_PATH}"; pwd`

#Find out where we're installed.
INSTALL_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CURRENT_DIR=`pwd`
echo ""
echo "======STARTUP PARAMS========="
echo "Absolute path of config file: " ${ABS_CONFIG_PATH}
echo "Installed Dir: " ${INSTALL_DIR}
echo "Current Working Dir: " ${CURRENT_DIR}
echo "============================="
echo ""


java -Dconfig.properties=${ABS_CONFIG_PATH}/${CONFIG_FILE_NAME} -Djava.util.logging.config.file=${ABS_CONFIG_PATH}/logging.properties -jar ${INSTALL_DIR}/${MAIN_JAR_NAME}-${VERSION}.jar