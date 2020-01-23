#!/bin/sh
#change to the right dir in this line
version=3.2pre4
cd ../dist/
#Since ant tar doesn't understand attributes, we do it by ourselves
chmod a+x jext-$version/bin/jext
#tar czf /usr/src/RPM/SOURCES/jext-$version.tar.gz jext-$version
tar cf /usr/src/RPM/SOURCES/jext-$version.tar jext-$version

cd ../src
#rpm -bs jext.spec
#rpm --rebuild -v --target noarch /usr/src/RPM/SRPMS/jext-$version-1.src.rpm
rpm -bb -v --clean --rmsource --target noarch jext.spec
mv /usr/src/RPM/RPMS/noarch/jext-$version-1.noarch.rpm ../dist/tgz
#mv /usr/src/RPM/SRPMS/jext-$version-1.src.rpm ../dist/tgz
