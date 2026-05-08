# Informe LaTeX

Carpeta del informe en formato LaTeX.

## Archivos

- `main.tex` — Código fuente del informe
- `main.pdf` — Informe compilado (final)
- `imagenes/` — Capturas del proceso de instalacion y pruebas (21 imagenes)

## Como compilar

### Opcion A: Overleaf (recomendado)

1. Crear nuevo proyecto en https://overleaf.com
2. Subir `main.tex` y la carpeta `imagenes/` completa
3. Subir tambien `Logo_Javeriana.png` a la raiz del proyecto
4. Compilar con pdfLaTeX

### Opcion B: Local

Requiere tener instalada una distribucion de TeX (TeX Live o MiKTeX):

```bash
pdflatex main.tex
pdflatex main.tex   # segunda corrida para resolver referencias
```

## Estructura del informe

1. Introduccion
2. Marco Teorico (Sistemas de ficheros, DFS, NFS, HDFS, conceptos avanzados)
3. Arquitectura del Cluster Implementado
4. Implementacion Paso a Paso
5. Pruebas y Resultados
6. Conclusiones
7. Referencias
