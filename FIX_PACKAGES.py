#!/usr/bin/env python3
"""
Script para corregir todos los packages de com.example.espressoshots a com.example.shots
Ejecutar en la raíz del proyecto Android: python3 FIX_PACKAGES.py
"""

import os
import re
from pathlib import Path

def fix_packages(root_dir="app/src/main/java/com/example/shots"):
    """Reemplaza todos los packages incorrectos en los archivos Kotlin"""
    
    kotlin_files = Path(root_dir).rglob("*.kt")
    
    count = 0
    for file_path in kotlin_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Reemplazar el package incorrecto
            new_content = content.replace(
                'package com.example.espressoshots',
                'package com.example.shots'
            )
            
            # También reemplazar imports incorrectos
            new_content = new_content.replace(
                'import com.example.espressoshots',
                'import com.example.shots'
            )
            
            if new_content != content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"✓ Actualizado: {file_path}")
                count += 1
        except Exception as e:
            print(f"✗ Error en {file_path}: {e}")
    
    print(f"\n✅ Total archivos actualizados: {count}")

if __name__ == "__main__":
    fix_packages()
