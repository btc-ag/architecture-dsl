#!/bin/sh

set -x -e

#curl http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/photon/R/eclipse-dsl-photon-R-linux-gtk-x86_64.tar.gz >eclipse-dsl-photon-R-linux-gtk-x86_64.tar.gz
#tar -xvzf eclipse-dsl-photon-R-linux-gtk-x86_64.tar.gz

cd ArchitectureManagementTools
WORKDIR=$(pwd)

# rem Execute mwe worksflow to generate architecture dsl java sources.
MY_CLASSPATH=../eclipse_helios/plugins/org.apache.ant_1.7.1.v20100518-1145/lib/ant-launcher.jar
for DIR in ../eclipse_helios/plugins/* ; do MY_CLASSPATH=${MY_CLASSPATH}:${DIR} ; done
java -classpath ${MY_CLASSPATH} org.apache.tools.ant.launch.Launcher -f com.btc.arch.ArchitectureDsl/xtext_build.xml -Dmy.basepath="%WORKDIR%/../eclipse_helios" gen

# rem The build.xml has to be copied to the build directory, because otherwise it will not correctly resolve all paths.
cp ../eclipse_helios/plugins/org.eclipse.pde.build_3.6.2.R36x_20110203/scripts/build.xml .

# rem At least the feature.xml is required to be found in both locations.
mkdir features
cp -r com.btc.arch.ArchitectureDsl.generator.commandline.release features

# rem The buildDirectory in the build.properties file has to be an absolute path, because otherwise not all paths will be resolved correctly.
sed -i -e "s#buildDirectory=ReplaceByAbsolutePath#buildDirectory=${WORKDIR}#g" features/com.btc.arch.ArchitectureDsl.generator.commandline.release/build.properties

# rem Build all plugins. The jars are packed into a zip file in I.ArchitectureDslGenerator.
java -jar ../eclipse_helios/plugins/org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar -application org.eclipse.ant.core.antRunner -buildfile build.xml -Dbuilder=features/com.btc.arch.ArchitectureDsl.generator.commandline.release

# rem Unpack the zip file built in the previous step and move them to the plugins directory of the buildDirectory of the product build.
unzip I.ArchitectureDslGenerator/com.btc.arch.ArchitectureDsl.generator.commandline.release-ArchitectureDslGenerator.zip -d ArchitectureDslGenerator_build
mv ArchitectureDslGenerator_build/ArchitectureDslGenerator/plugins ArchitectureDslGenerator_build

# rem The product definition is required to be found in the features folder.
mkdir ArchitectureDslGenerator_build/features
cp -r com.btc.arch.ArchitectureDsl.generator.commandline/com.btc.arch.ArchitectureDsl.generator.commandline.product ArchitectureDslGenerator_build/features

# rem The buildDirectory in the build.properties file has to be an absolute path, because otherwise not all paths will be resolved correctly.
# rem The path is required to be separated by forward slashes and a double forward slash after the drive letter.
sed -i -e "s#buildDirectory=ReplaceByAbsolutePath#buildDirectory=${WORKDIR}/ArchitectureDslGenerator_build#g" com.btc.arch.ArchitectureDsl.generator.commandline.productbuildconfiguration/build.properties

# rem The productBuild.xml has to be copied to the build directory, because otherwise it will not correctly resolve all paths.
cp ../eclipse_helios/plugins/org.eclipse.pde.build_3.6.2.R36x_20110203/scripts/productBuild/productBuild.xml .

# rem Build the executable of the product. The executable can be found in ArchitectureDslGenerator_build/I.ArchitectureDslGenerator/ArchitectureDslGenerator-win32.win32.x86.zip after the build.
java -jar ../eclipse_helios/plugins/org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar -application org.eclipse.ant.core.antRunner -buildfile productBuild.xml -Dbuilder=com.btc.arch.ArchitectureDsl.generator.commandline.productbuildconfiguration
