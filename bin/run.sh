#!/bin/bash

ARGS="$@"
PRG="$0"
PRGDIR=`dirname $PRG`

# JAVA options
# You can set JVM additional options here if you want
if [ -z "$JVM_OPTS" ]; then
    JVM_OPTS="-Xverify:none -XX:+TieredCompilation -XX:+UseBiasedLocking -XX:+UseStringCache -XX:+UseParNewGC -XX:InitialCodeCacheSize=8m -XX:ReservedCodeCacheSize=32m -Dorg.terracotta.quartz.skipUpdateCheck=true"
fi
# Set up security options
SECURITY_OPTS="-Djava.security.debug=failure"

export JAVA_OPTS="$SECURITY_OPTS $JAVA_OPTS $JVM_OPTS"

for JAVA in "java" "${JAVA_HOME}/bin/java" "${JAVA_HOME}/Home/bin/java" "/usr/bin/java" "/usr/local/bin/java"
do
  if [ -x "$JAVA" ]
  then
    break
  fi
done

if [ ! -x "$JAVA" ]
then
  echo "Unable to locate Java. Please set JAVA_HOME environment variable."
  exit
fi

# start open-transcoder
echo "Starting open-transcoder"
exec "$JAVA" $JAVA_OPTS -jar "$PRGDIR"/../open-transcoder.jar $ARGS
