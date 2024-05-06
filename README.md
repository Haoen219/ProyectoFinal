Este es el proyecto personal para el curso DAW2.

El programa se llamrá 'Z Cash' (zeta cash), un programa de registro de cajas.
Operará con una Base De Datos SQLite para guardar los articulos y las ventas.

Formado por 2 partes:
-Programa Principal:
  -Interfáz de usuario para la operación principal.
  -Realiza las operaciones sobre la BDD.
  -Imprenta de ticket de venta.
-Programa Móvil:
  -Interfáz básico.
  -Acceso a la cámara.
  -Comunicación con el porgrama principal.

El programa móvil es un escaner de barra que se comunicará con el
programa principal a travéz de una red local usando Sockets. Su
funcionalidad será simplemente escanear una barra de código y
enviar el código escaneado al ordenador.
