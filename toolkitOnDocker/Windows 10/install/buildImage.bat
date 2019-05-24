@ECHO OFF
REM Build a Docker Image
REM
REM 
REM NOTE: If TK_VER is provided then it must match the Toolkit GitHub Release version number.
REM

REM Start with clean ERRORLEVEL.
TYPE NUL>NUL

SETLOCAL

SET containerHostIp=129.6.59.171
REM Optional. If empty, default to latest. Otherwise use the version number such as #.#.#.
SET tkVer=%1
SET tkPropertyFile=%2
SET toolkitPropertyParentPath=%CD%

SET "Toolkit_Port="
SET "Toolkit_TLS_Port="
SET "Proxy_Port="
SET "Toolkit_Host="
SET "Listener_Port_Range="
SET "Listener_Port_RangeBeginTemp="
SET "Listener_Port_RangeEndTemp="
SET "Listener_Port_RangeBegin="
SET "Listener_Port_RangeEnd="


IF NOT DEFINED tkVer (
	SET tkVer=latest
)

IF NOT DEFINED tkPropertyFile (
	SET tkPropertyFile=toolkit.properties
) 

FOR /F "tokens=1* delims==" %%A IN (%tkPropertyFile%) DO (
REM	IF "%%A"=="External_Cache" SET External_Cache=%%B
	IF "%%A"=="Toolkit_Port" (
		SET Toolkit_Port=%%B
	) 
	IF "%%A"=="Toolkit_TLS_Port" (
		SET Toolkit_TLS_Port=%%B
	) 
	IF "%%A"=="Proxy_Port" (
		SET Proxy_Port=%%B
	) 
	IF "%%A"=="Toolkit_Host" (
		SET Toolkit_Host=%%B
	) 
	IF "%%A"=="Listener_Port_Range" (
		SET Listener_Port_Range=%%B
	)
)

FOR /F "tokens=1,2 delims=," %%G IN ("%Listener_Port_Range%") DO (
	SET Listener_Port_RangeBeginTemp=%%G
	SET Listener_Port_RangeEndTemp=%%H
)
SET Listener_Port_RangeBegin=%Listener_Port_RangeBeginTemp: =%
SET Listener_Port_RangeEnd=%Listener_Port_RangeEndTemp: =%


IF %errorlevel% EQU 0 (
	CALL :BUILD_IMAGE %tkVer%
	CALL :CREATE_CONTAINER
) ELSE (
	ECHO Error extracting parameters.
)
EXIT /B 0

:BUILD_IMAGE
SET tkVer=%1

IF NOT DEFINED tkVer (
	ECHO "Building latest image"
docker build ^
--build-arg TOOLKIT_PROPERTY_FILE=%tkPropertyFile% ^
-t usnistgov-xdstoolkit .
	) ELSE (
    ECHO "Building %tkVer% image"
docker build ^
--build-arg TK_VER=%tkVer% ^
--build-arg TOOLKIT_PROPERTY_FILE=%tkPropertyFile% ^
-t usnistgov-xdstoolkit:%tkVer% .
	)
IF %errorlevel% GEQ 1 (
		EXIT /B %errorlevel%
)
EXIT /B 0

: CREATE_CONTAINER
ECHO Creating container for %tkVer%
docker create ^
-e TZ=America/New_York ^
--hostname %Toolkit_Host% ^
--name %Toolkit_Host% ^
--add-host=host.xdstoolkit.test:%containerHostIp% ^
--network xdstoolkitNet ^
-i ^
-t ^
-p%Toolkit_Port%:%Toolkit_Port% ^
-p%Proxy_Port%:%Proxy_Port% ^
-p%Toolkit_TLS_Port%:%Toolkit_TLS_Port% ^
-p%Listener_Port_RangeBegin%-%Listener_Port_RangeEnd%:%Listener_Port_RangeBegin%-%Listener_Port_RangeEnd% ^
--mount source=ec%tkVer%,target=/opt/ecdir ^
usnistgov-xdstoolkit:%tkVer%

SET createError=%errorlevel%
IF %createError% EQU 0 (
    ECHO.
    ECHO To Start without STDIN/STDOUT ^(No screen output and runs in the background^)
    ECHO docker start %Toolkit_Host%
    ECHO.
    ECHO To attach local standard input, output, and error streams to a running container
    ECHO docker attach %Toolkit_Host%
    ECHO.
    ECHO To Start with STDIN/STDOUT ^(Catalina.out is displayed to screen^)
    ECHO docker start -a %Toolkit_Host%
    ECHO.
    ECHO To login to the container shell
    ECHO docker exec -it latest.xdstoolkit.test /bin/bash
    ECHO.
    ECHO To connect to this Toolkit running on Tomcat use the URL
    ECHO http://%Toolkit_Host%:%Toolkit_Port%/xdstools%tkVer%/
    ECHO.
) ELSE (
    ECHO Error.
    EXIT /B %createError%
)
EXIT /B 0
