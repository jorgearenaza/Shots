@echo off
REM Script para remover iconos PNG duplicados en Windows

echo Removiendo ic_launcher.png duplicados...

for %%D in (mipmap-hdpi mipmap-mdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi) do (
    set "path=app\src\main\res\%%D\ic_launcher.png"
    if exist "!path!" (
        echo Eliminando: !path!
        del /F "!path!"
    )
)

echo.
echo Proceso completado. Los iconos .webp se mantienen.
echo.
pause
