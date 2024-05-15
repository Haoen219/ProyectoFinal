# Z Cash

<img src="https://github.com/Haoen219/ProyectoFinal/blob/main/logo/logo.png" alt="Logo del proyecto" width="100" height="100">

Este es un Proyecto de Fin de Curso DAM2 que consta de dos programas: uno principal y otro secundario.

El programa principal opera como un registro de cajas así como un gestor de ventas y artículos.
Mientras tanto, el programa secundario opera en un dispositivo móvil y funciona como un escáner para los artículos.

Los dos programas se comunican a travéz de una red local con Sockets.
## Funcionalidades principales

### Programa Principal
El programa principal es capaz de realizar ventas y de registrar artículos. Esto se realiza usando una Base de Datos de tipo SQLite.
- **Gestión de ventas:** Permite realizar ventas de artículos, calcular el total de la venta y generar recibos.
- **Administración de artículos:** Permite agregar, eliminar y actualizar información sobre los artículos disponibles para la venta.
- **Registro de cajas:** Lleva un registro de las transacciones realizadas, incluyendo información sobre el monto total, artículos vendidos y fechas.

### Programa Secundario
El programa secundario hace uso de la cámara del móvil para escanear. Entonces podrá enviar el número de barra al programa principal.
- **Escaneo de artículos:** Permite escanear códigos de barras o utilizar otro método de identificación para agregar rápidamente artículos a una venta en el programa principal.
- **Diseño simple:** Programa simple fácil e intuitivo de usar, permitiendo un aprendizaje rápida.
