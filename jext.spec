Summary: Jext, the Java text editor for programmers
%define version 3.2pre4
License: GPL
Group: Editors
Name: jext
#Prefix: /opt
#Prefix: /etc
#Prefix: /usr
Provides: jext
Release: 1
Source: jext-%{version}.tar
URL: http://www.jext.org/
Version: %{version}
BuildRoot: /tmp/jextrpm
BuildArch: noarch
AutoReq: 0

%description
Jext is a powerful, pure Java text editor primarily targeted for use by
programmers.  For this reason, Jext provides many useful programming
functions: syntax colorization, auto indentation, a source code browser, a
class browser, and an integrated console. It requires a JDK/JRE (at least
version 1.2) to work.

Other users will find some additional advanced Jext functions useful.
These include: HTML editing, the ability to send mail directly, scripting,
and a configurable clip library.
Lastly, Jext is fully customizable with the help of plugins and XML
configuration files. This enables anyone to easily translate Jext from English
into other languages. This distribution includes Chinese, Czech, French,
German, Italian, Polish and Spanish translations. Development is hosted at:
http://sourceforge.net/projects/jext/
and you can always download new versions at:
http://www.jext.org/

%prep
%setup -q

%build

%install
rm -rf $RPM_BUILD_ROOT/opt/jext
mkdir -p $RPM_BUILD_ROOT/opt/jext
rm -rf $RPM_BUILD_ROOT/usr/bin
mkdir -p $RPM_BUILD_ROOT/usr/bin
rm -rf $RPM_BUILD_ROOT/etc
mkdir -p $RPM_BUILD_ROOT/etc
rm -f jikes.spec
cd bin
rm -f Jext.exe jext.js jextlauncher.ini jext.ncf MouseWheel.dll
cd ..
cp -R * $RPM_BUILD_ROOT/opt/jext
ln -s /opt/jext/bin/jext $RPM_BUILD_ROOT/usr/bin/jext
mv -f $RPM_BUILD_ROOT/opt/jext/bin/jextrc.sample $RPM_BUILD_ROOT/etc/jextrc

%clean
[ -n "$RPM_BUILD_ROOT" -a "$RPM_BUILD_ROOT" != / ] && rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root)
/usr/bin/jext
/opt/jext
%config(noreplace) /etc/jextrc

%changelog
* Wed Dec 03 2003 Paolo Giarrusso <blaisorblade_work@yahoo.it>
+ jext-3.2pre4-1
- renamed file jextrc to jextrc.sample in the sources and updated here the install 
  section consistently;
- changed clean section to avoid cleaning if $RPM_BUILD_ROOT = / 
- added BuildArch = noarch.
- modified buildRpm.sh script
- switched to using uncompressed tar file as sources
- include /opt/jext in the files section instead of /opt/jext/*;
  when removing the package the /opt/jext directory wasn't deleted.
- updated info section for Polish translation and some cosmetic changes.
