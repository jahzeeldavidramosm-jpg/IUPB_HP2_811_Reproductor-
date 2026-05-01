#!/bin/bash
echo "Compilando Musiteca..."
mkdir -p bin

javac -cp "lib/*" -d bin src/App.java src/ReproductorAudio.java src/FrmReproductor.java src/entidades/Cancion.java src/entidades/Artista.java src/entidades/Artistas.java

if [ $? -ne 0 ]; then
    echo "ERROR al compilar. Asegurate de tener Java instalado."
    exit 1
fi

echo "Ejecutando Musiteca..."
java -cp "bin:lib/*" App
