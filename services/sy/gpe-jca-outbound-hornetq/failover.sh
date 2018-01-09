#!/bin/sh

ha0exists=0
for jProc in `ps -C java -o pid=`;
do
    pInfo=$(ps -p $jProc -f)
    if [[ $pInfo =~ ha-0 ]]; then
        echo -en "killJbossProcesses() about to kill ha-0 with process id = $jProc\n"
        kill -9 $jProc
        ha0exists=1
    fi
done

if [ $ha0exists == 0  ]; then
    echo -en "about to restart ha-0\n"
    $JBOSS_HOME/bin/jboss-cli.sh --controller=$HOSTNAME:9999 --connect --command="/host=master/server-config=ha-0:stop(blocking=false)"
    $JBOSS_HOME/bin/jboss-cli.sh --controller=$HOSTNAME:9999 --connect --command="/host=master/server-config=ha-0:start(blocking=false)"
fi


