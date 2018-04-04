#!/bin/bash

repos="https://maven.repository.redhat.com/ga"
repos="$repos https://maven.repository.redhat.com/earlyaccess/all/"
repos="$repos https://maven.repository.redhat.com/techpreview/all/"
repos="$repos http://download-ipv4.eng.brq.redhat.com/brewroot/repos/jb-common-build/latest/maven"
repos="$repos http://download-ipv4.eng.brq.redhat.com/brewroot/repos/jb-cloud-enablement-build/latest/maven"

for var in $@
do
    case $var in
        -artifact=*)
            artifact=`echo $var | cut -f2 -d\=`
            ;;
    esac
done

echo -en "\nartifact = $artifact\n\n";
for repo in `echo $repos`; do
    echo -en "\n\nrepo = $repo";
    mvn dependency:get  \
              -Dtransitive=false \
              -DrepoUrl=$repo \
              -Dartifact=$artifact
done
