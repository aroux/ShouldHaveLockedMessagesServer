@echo off
setlocal
cls

set HOME_DIR=%CD%
set CLASS_DIR=classes
set SRC_DIR=src
set CLASS_NAME=Server
set STUB=stub.bat

cd /d %HOME_DIR%
javac -d %CLASS_DIR% src\%CLASS_NAME%.java
pushd %CLASS_DIR%
echo Main-Class: %CLASS_NAME% > MANIFEST.MF
jar -cvmf MANIFEST.MF ..\%CLASS_NAME%.jar %CLASS_NAME%*.class
popd
jar -uf %CLASS_NAME%.jar %CLASS_DIR%\%CLASS_NAME%.java
echo @java -jar %%~n0%%~x0 %%* > %STUB%
echo @exit /b >> %STUB%
copy %STUB% /b + %CLASS_NAME%.jar /b %CLASS_NAME%.bat /b
del %CLASS_NAME%.jar
del %STUB%

endlocal