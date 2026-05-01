@echo off
echo Compilando Musiteca...
if not exist bin mkdir bin

javac -cp "lib/*" -d bin src/App.java src/ReproductorAudio.java src/FrmReproductor.java src/entidades/Cancion.java src/entidades/Artista.java src/entidades/Artistas.java

if %errorlevel% neq 0 (
    echo ERROR al compilar. Asegurate de tener Java instalado.
    pause
    exit /b 1
)

echo Ejecutando Musiteca...
java -cp "bin;lib/*" App
pause
