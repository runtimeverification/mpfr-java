set WD=%CD%
cd mpfr\mpir\build.vc10
echo 7 | mpir_config.py
cd lib_mpir_p4_sse2
msbuild lib_mpir_p4_sse2.vcxproj /p:Configuration=Release;Platform=win32 /t:Clean,Build
xcopy /Y /s /i ..\..\lib ..\lib
xcopy /Y /s win32 ..\lib\win32
cd ..\..\..\src\build.vc10\lib_mpfr
msbuild lib_mpfr.vcxproj /p:Configuration=Release;Platform=win32 /t:Clean,Build
cd ..\..\..\..\mpfr-java
call mvn process-test-sources
xcopy /Y /s /i target\generated-sources\hawtjni\native-package target\native-build
set INCLUDE=%INCLUDE%;%WD%\mpfr\mpir\build.vc10\lib\win32\Release;%WD%\mpfr\src
cd target\native-build
copy %WD%\vs2010.vcxproj .
msbuild vs2010.vcxproj /p:Configuration=Release;Platform=win32;UseEnv=true;MpfrLib=%WD%\mpfr\src\build.vc10\lib_mpfr\lib\win32\Release\mpfr.lib /t:Clean,Build
cd ..\..
mkdir target\generated-sources\hawtjni\lib\META-INF\native\windows32
copy target\native-build\target\win32-release\lib\mpfr_java.dll target\generated-sources\hawtjni\lib\META-INF\native\windows32
cd %WD%\mpfr-java
mvn hawtjni:package-jar
cd ..