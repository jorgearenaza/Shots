#!/bin/bash
# Script para actualizar todos los packages de com.example.espressoshots a com.example.shots
# Ejecución: bash fix_packages.sh (desde la raíz del proyecto Android)

echo "🔧 Corrigiendo packages..."

# Buscar y reemplazar en totos los archivos .kt
find app/src/main/java -name "*.kt" -type f -print0 | xargs -0 sed -i \
  's/package com\.example\.espressoshots/package com.example.shots/g'

find app/src/main/java -name "*.kt" -type f -print0 | xargs -0 sed -i \
  's/import com\.example\.espressoshots/import com.example.shots/g'

echo "✅ Packages actualizados correctamente"
echo "Ahora ejecuta: ./gradlew clean build"
