set WD=%CD%
cd mpfr\mpir\build.vc10
echo 18 | mpir_config.py
cd lib_mpir_k8
msbuild lib_mpir_k8.vcxproj /p:Configuration=Release;Platform=x64
xcopy /Y /s /i ..\..\lib ..\lib
xcopy /Y /s x64 ..\lib\x64
cd ..\..\..\src\build.vc10\lib_mpfr
msbuild lib_mpfr.vcxproj /p:Configuration=Release;Platform=x64
cd ..\..\..\..\mpfr-java
call mvn process-test-sources
xcopy /Y /s /i target\generated-sources\hawtjni\native-package target\native-build
set INCLUDE=%INCLUDE%;%WD%\mpfr\mpir\build.vc10\lib\x64\Release;%WD%\mpfr\src
cd target\native-build
copy %WD%\vs2010.vcxproj .
msbuild vs2010.vcxproj /p:Configuration=Release;Platform=x64;UseEnv=true;MpfrLib=%WD%\mpfr\src\build.vc10\lib_mpfr\lib\x64\Release\mpfr.lib
cd ..\..
mkdir target\generated-sources\hawtjni\lib\META-INF\native\windows64
copy target\native-build\target\x64-release\lib\mpfr_java.dll target\generated-sources\hawtjni\lib\META-INF\native\windows64
cd %WD%\mpfr-java
mvn hawtjni:package-jar