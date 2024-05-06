# Proyecto 'Z Cash' - Registro de Cajas

## Descripción
El proyecto consiste en un programa de registro de cajas llamado 'Z Cash' (zeta cash). Este programa operará utilizando una Base de Datos SQLite para almacenar los artículos y las ventas.

## Partes del Proyecto

### Programa Principal:
El programa principal es donde el usuario tendrá la interacción principal con el programa y la mayoría de sus funcionalidades. El programa principal puede funcionar independientemente, aunque no se use el programa móvil.
- Interfaz de usuario para la operación principal.
- Realiza las operaciones sobre la Base de Datos.
- Impresión de tickets de venta.

### Programa Móvil:
El programa móvil actuará como un escáner de barras que se comunicará con el programa principal a través de una red local utilizando Sockets. Su funcionalidad principal será escanear códigos de barras y enviar el código escaneado al ordenador.
- Interfaz básica.
- Acceso a la cámara.
- Comunicación con el programa principal.




