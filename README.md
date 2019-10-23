# Pasos necesarios para correr el proyecto

1. Clonar el proyecto desde el repositorio Github
2. Instalar java 8, setear la variable JAVA_HOME
3. Descargar la última versión de Android Studio de la [página oficial](https://developer.android.com/studio/index.html?hl=es-419)
4. Crear el archivo google-services.json en Untha_Care/app
5. Copiar el contenido del [enlace](https://firebasestorage.googleapis.com/v0/b/untha-care.appspot.com/o/google-services.json?alt=media) en el archivo creado anteriormente
6. Una vez realizados los dos primeros pasos, abrir Android Studio, seleccionar la opción "Importar Proyecto" y abrir el proyecto desde donde haya sido clonado
7. Entrar en el menú Android Studio/preferences/Memory Settings y cambiar la memoria a 4096MB, esto permitirá un mejor rendimiento de la aplicación
8. Crear un dispositivo virtual para ejecutar la aplicación, [aquí](https://developer.android.com/studio/run/managing-avds?hl=es) los pasos de como hacerlo
9. Ejecutar la aplicación en el dispositivo previamente creado.
    
# Para continuar con el desarrollo de la aplicación

Es importante copiar el archivo pre-commit que está en la carpeta Untha_Care, en la dirección Untha_Care/.git/hooks.

Si no estás en Windows, ejecuta esta linea para copiar el archivo:
```
cp pre-commit .git/hooks/pre-commit
```
Con este hook cada vez que realices un commit, se ejecutarán las tareas necesarias para asegurar que los cambios hechos sean correctos (test, lint, detekt).
