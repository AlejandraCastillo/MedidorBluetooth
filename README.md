# Proyecto de Tesis: Aplicación móvil en Android para registro de telemetría vía Bluetooth de un watthorímetro digital

Tesis que para obtener el titulo de Ingeniera en computacion.

Presenta: María Alejandra Castillo Martínez

Director de Tesis: M. I. Juan Ricardo Damián Zamacona

Desarrollada en el Instituto de Ciencia Aplicada y Tecnología, UNAM.

## Descripción

Este proyecto consiste en el desarrollo de una aplicación móvil en Android para registrar la señal emitida por un watthorímetro digital a 
través de Bluetooth. La aplicación captura las variables de voltaje, corriente, potencia y energía, mostrándolas en tiempo real y almacenándolas 
en una base de datos local de tipo relacional (SQLite). Los datos pueden ser descargados en archivos CSV para análisis posterior.

## Funcionalidades

- Registro de señal telemétrica vía Bluetooth.
- Captura y visualización en tiempo real de variables como voltaje, corriente, potencia y energía.
- Almacenamiento de datos en una base de datos local (SQLite).
- Descarga de datos en archivos CSV para análisis externo.

## Tecnologías utilizadas

- Java
- XML
- Android Studio
- Bluetooth
- SQLite

## Instalación y Uso

1. Clona el repositorio
2. Abre el proyecto en Android Studio.
3. Compila y ejecuta la aplicación en un dispositivo Android.
4. Conecta el watthorímetro digital vía Bluetoot.
5. Visualiza y registra la señal telemétrica en la aplicación.

## Conexión Bluetooth

La aplicación está diseñada para conectarse a cualquier dispositivo con un perfil de Bluetooth general. 
Sin embargo, es importante tener en cuenta que la cadena de datos que se recibe debe seguir la siguiente expresión regular: 

1?2?3::\\d*(-\\d*\\.\\d*){4}::456

Esta expresión regular asegura que los datos recibidos cumplan con el formato esperado para su correcta captura y procesamiento en la aplicación.

