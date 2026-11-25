# Patrones de diseño — Backend

Índice de dónde se aplica cada patrón. Cada clase tiene además, en su encabezado, **para qué sirve** y sus patrones, y en el código hay comentarios `// PATRÓN ...` en las líneas exactas donde se aplica. En VS Code: **Ctrl+Shift+F** y buscar `PATRÓN`.

## Arquitectura hexagonal (puertos y adaptadores)

- [`TallerApplication.java`](src/main/java/com/taller/TallerApplication.java) — el código se reparte en capas: api (entrada HTTP), aplicacion (casos de uso), dominio (reglas, sin Spring) e infraestructura (MongoDB e integraciones).
- [`CitaRepositorio.java`](src/main/java/com/taller/dominio/puerto/CitaRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`ClienteRepositorio.java`](src/main/java/com/taller/dominio/puerto/ClienteRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`CodificadorClaves.java`](src/main/java/com/taller/dominio/puerto/CodificadorClaves.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`EmisorTokens.java`](src/main/java/com/taller/dominio/puerto/EmisorTokens.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`EscanerDiagnostico.java`](src/main/java/com/taller/dominio/puerto/EscanerDiagnostico.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`FacturaRepositorio.java`](src/main/java/com/taller/dominio/puerto/FacturaRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`FacturacionElectronica.java`](src/main/java/com/taller/dominio/puerto/FacturacionElectronica.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`GeneradorConsecutivos.java`](src/main/java/com/taller/dominio/puerto/GeneradorConsecutivos.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`MantenimientoRepositorio.java`](src/main/java/com/taller/dominio/puerto/MantenimientoRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`MecanicoRepositorio.java`](src/main/java/com/taller/dominio/puerto/MecanicoRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`MovimientoInventarioRepositorio.java`](src/main/java/com/taller/dominio/puerto/MovimientoInventarioRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`NotificadorCliente.java`](src/main/java/com/taller/dominio/puerto/NotificadorCliente.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`OrdenCompraRepositorio.java`](src/main/java/com/taller/dominio/puerto/OrdenCompraRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`OrdenTrabajoRepositorio.java`](src/main/java/com/taller/dominio/puerto/OrdenTrabajoRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`PasarelaPagos.java`](src/main/java/com/taller/dominio/puerto/PasarelaPagos.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`PublicadorEventos.java`](src/main/java/com/taller/dominio/puerto/PublicadorEventos.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`RepuestoRepositorio.java`](src/main/java/com/taller/dominio/puerto/RepuestoRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`ServicioRepositorio.java`](src/main/java/com/taller/dominio/puerto/ServicioRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`UsuarioRepositorio.java`](src/main/java/com/taller/dominio/puerto/UsuarioRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).
- [`VehiculoRepositorio.java`](src/main/java/com/taller/dominio/puerto/VehiculoRepositorio.java) — puerto: interfaz que define el dominio y que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología (inversión de dependencias).

## Repository

- [`CitaRepositorio.java`](src/main/java/com/taller/dominio/puerto/CitaRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`ClienteRepositorio.java`](src/main/java/com/taller/dominio/puerto/ClienteRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`FacturaRepositorio.java`](src/main/java/com/taller/dominio/puerto/FacturaRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`MantenimientoRepositorio.java`](src/main/java/com/taller/dominio/puerto/MantenimientoRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`MecanicoRepositorio.java`](src/main/java/com/taller/dominio/puerto/MecanicoRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`MovimientoInventarioRepositorio.java`](src/main/java/com/taller/dominio/puerto/MovimientoInventarioRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`OrdenCompraRepositorio.java`](src/main/java/com/taller/dominio/puerto/OrdenCompraRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`OrdenTrabajoRepositorio.java`](src/main/java/com/taller/dominio/puerto/OrdenTrabajoRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`RepuestoRepositorio.java`](src/main/java/com/taller/dominio/puerto/RepuestoRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`ServicioRepositorio.java`](src/main/java/com/taller/dominio/puerto/ServicioRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`UsuarioRepositorio.java`](src/main/java/com/taller/dominio/puerto/UsuarioRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`VehiculoRepositorio.java`](src/main/java/com/taller/dominio/puerto/VehiculoRepositorio.java) — se comporta como una colección de objetos del dominio y esconde cómo se guardan.
- [`CitaRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/CitaRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`ClienteRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/ClienteRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`FacturaRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/FacturaRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`MantenimientoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MantenimientoRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`MecanicoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MecanicoRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`MovimientoInventarioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MovimientoInventarioRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`OrdenCompraRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/OrdenCompraRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`OrdenTrabajoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/OrdenTrabajoRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`RepuestoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/RepuestoRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`ServicioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/ServicioRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`UsuarioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/UsuarioRepositorioMongo.java) — es la implementación concreta del repositorio.
- [`VehiculoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/VehiculoRepositorioMongo.java) — es la implementación concreta del repositorio.

## Adapter

- [`DianFacturacionClient.java`](src/main/java/com/taller/infraestructura/integracion/DianFacturacionClient.java) — adapta la API HTTP del proveedor al puerto FacturacionElectronica.
- [`FacturacionSimulada.java`](src/main/java/com/taller/infraestructura/integracion/FacturacionSimulada.java) — implementa el puerto FacturacionElectronica.
- [`GeneradorConsecutivosMongo.java`](src/main/java/com/taller/infraestructura/integracion/GeneradorConsecutivosMongo.java) — implementa el puerto GeneradorConsecutivos con findAndModify de MongoDB.
- [`ObdScannerAdapter.java`](src/main/java/com/taller/infraestructura/integracion/ObdScannerAdapter.java) — adapta la API del gateway IoT al puerto EscanerDiagnostico.
- [`PasarelaPagoClient.java`](src/main/java/com/taller/infraestructura/integracion/PasarelaPagoClient.java) — implementa el puerto PasarelaPagos.
- [`PublicadorEventosEnProceso.java`](src/main/java/com/taller/infraestructura/integracion/PublicadorEventosEnProceso.java) — implementa el puerto PublicadorEventos; se podría cambiar por RabbitMQ o Kafka sin tocar el dominio.
- [`WhatsAppGateway.java`](src/main/java/com/taller/infraestructura/integracion/WhatsAppGateway.java) — implementa el puerto NotificadorCliente.
- [`CitaRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/CitaRepositorioMongo.java) — implementa el puerto CitaRepositorio del dominio usando Spring Data y Mapeadores.
- [`ClienteRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/ClienteRepositorioMongo.java) — implementa el puerto ClienteRepositorio del dominio usando Spring Data y Mapeadores.
- [`FacturaRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/FacturaRepositorioMongo.java) — implementa el puerto FacturaRepositorio del dominio usando Spring Data y Mapeadores.
- [`MantenimientoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MantenimientoRepositorioMongo.java) — implementa el puerto MantenimientoRepositorio del dominio usando Spring Data y Mapeadores.
- [`MecanicoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MecanicoRepositorioMongo.java) — implementa el puerto MecanicoRepositorio del dominio usando Spring Data y Mapeadores.
- [`MovimientoInventarioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/MovimientoInventarioRepositorioMongo.java) — implementa el puerto MovimientoInventarioRepositorio del dominio usando Spring Data y Mapeadores.
- [`OrdenCompraRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/OrdenCompraRepositorioMongo.java) — implementa el puerto OrdenCompraRepositorio del dominio usando Spring Data y Mapeadores.
- [`OrdenTrabajoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/OrdenTrabajoRepositorioMongo.java) — implementa el puerto OrdenTrabajoRepositorio del dominio usando Spring Data y Mapeadores.
- [`RepuestoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/RepuestoRepositorioMongo.java) — implementa el puerto RepuestoRepositorio del dominio usando Spring Data y Mapeadores.
- [`ServicioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/ServicioRepositorioMongo.java) — implementa el puerto ServicioRepositorio del dominio usando Spring Data y Mapeadores.
- [`UsuarioRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/UsuarioRepositorioMongo.java) — implementa el puerto UsuarioRepositorio del dominio usando Spring Data y Mapeadores.
- [`VehiculoRepositorioMongo.java`](src/main/java/com/taller/infraestructura/persistencia/VehiculoRepositorioMongo.java) — implementa el puerto VehiculoRepositorio del dominio usando Spring Data y Mapeadores.
- [`CodificadorBCrypt.java`](src/main/java/com/taller/infraestructura/seguridad/CodificadorBCrypt.java) — adapta el BCryptPasswordEncoder de Spring al puerto CodificadorClaves.
- [`ProveedorJwt.java`](src/main/java/com/taller/infraestructura/seguridad/ProveedorJwt.java) — adapta la librería JWT al puerto EmisorTokens.

## Strategy

- [`FacturacionElectronica.java`](src/main/java/com/taller/dominio/puerto/FacturacionElectronica.java) — tiene dos implementaciones intercambiables, FacturacionSimulada y DianFacturacionClient; una propiedad de configuración decide cuál se usa.
- [`DianFacturacionClient.java`](src/main/java/com/taller/infraestructura/integracion/DianFacturacionClient.java) — es una de las dos estrategias de facturación; la otra es FacturacionSimulada.
- [`FacturacionSimulada.java`](src/main/java/com/taller/infraestructura/integracion/FacturacionSimulada.java) — estrategia de facturación activa por defecto; se cambia por DianFacturacionClient sin tocar el dominio.
- [`RespuestasDeSeguridad.java`](src/main/java/com/taller/infraestructura/seguridad/RespuestasDeSeguridad.java) — Spring Security delega en este objeto cómo responder cuando falta la sesión o el permiso.

## Observer (publicar/suscribir)

- [`ManejadorEstadoOrden.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorEstadoOrden.java) — es un observador concreto: soporta() declara qué eventos le interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
- [`ManejadorEventos.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorEventos.java) — define el contrato del observador (soporta y manejar). Agregar una reacción nueva es crear una subclase, sin tocar a quien emite el evento (principio abierto/cerrado).
- [`ManejadorFacturacion.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorFacturacion.java) — es un observador concreto: soporta() declara qué eventos le interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
- [`ManejadorInventario.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorInventario.java) — es un observador concreto: soporta() declara qué eventos le interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
- [`ManejadorNotificaciones.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorNotificaciones.java) — es un observador concreto: soporta() declara qué eventos le interesan y manejar() reacciona; el servicio que emite el evento no sabe que existe.
- [`FacturacionService.java`](src/main/java/com/taller/aplicacion/servicio/FacturacionService.java) — actúa como emisor: publica FacturaValidada en el PublicadorEventos y los manejadores suscritos reaccionan.
- [`InventarioService.java`](src/main/java/com/taller/aplicacion/servicio/InventarioService.java) — actúa como emisor: publica StockBajoDetectado en el PublicadorEventos y los manejadores suscritos reaccionan.
- [`EventoDominio.java`](src/main/java/com/taller/dominio/evento/EventoDominio.java) — es el mensaje que el emisor (PublicadorEventos) entrega a los observadores (subclases de ManejadorEventos).
- [`PublicadorEventos.java`](src/main/java/com/taller/dominio/puerto/PublicadorEventos.java) — es el emisor (sujeto) visto desde el dominio: quien publica no conoce a los suscriptores.
- [`PublicadorEventosEnProceso.java`](src/main/java/com/taller/infraestructura/integracion/PublicadorEventosEnProceso.java) — es el sujeto: recorre los observadores (ManejadorEventos) y notifica a los que declaran soportar el evento. Si uno falla, los demás siguen.

## Eventos de dominio (DDD)

- [`OrdenTrabajoService.java`](src/main/java/com/taller/aplicacion/servicio/OrdenTrabajoService.java) — después de guardar la orden publica los eventos de dominio que ella acumuló (eventosPendientes) y luego los limpia.
- [`CotizacionAprobada.java`](src/main/java/com/taller/dominio/evento/CotizacionAprobada.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`CotizacionEmitida.java`](src/main/java/com/taller/dominio/evento/CotizacionEmitida.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`CotizacionRechazada.java`](src/main/java/com/taller/dominio/evento/CotizacionRechazada.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`DiagnosticoRegistrado.java`](src/main/java/com/taller/dominio/evento/DiagnosticoRegistrado.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`EventoDominio.java`](src/main/java/com/taller/dominio/evento/EventoDominio.java) — cada evento es un hecho inmutable, nombrado en pasado (OrdenCerrada, CotizacionAprobada...).
- [`FacturaValidada.java`](src/main/java/com/taller/dominio/evento/FacturaValidada.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`OrdenCerrada.java`](src/main/java/com/taller/dominio/evento/OrdenCerrada.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`OrdenCreada.java`](src/main/java/com/taller/dominio/evento/OrdenCreada.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`RepuestoConsumido.java`](src/main/java/com/taller/dominio/evento/RepuestoConsumido.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`StockBajoDetectado.java`](src/main/java/com/taller/dominio/evento/StockBajoDetectado.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`VehiculoEntregado.java`](src/main/java/com/taller/dominio/evento/VehiculoEntregado.java) — hecho inmutable del negocio; los manejadores que lo escuchan están indicados arriba.
- [`OrdenTrabajo.java`](src/main/java/com/taller/dominio/modelo/OrdenTrabajo.java) — cada operación relevante agrega un evento a eventosPendientes(); el servicio los publica después de guardar.

## Máquina de estados (variante de State)

- [`EstadoOrden.java`](src/main/java/com/taller/dominio/modelo/EstadoOrden.java) — la tabla TRANSICIONES declara a qué estados se puede pasar desde cada uno. OrdenTrabajo consulta permiteTransicionA() antes de cambiar, así que es imposible llegar a un estado inválido. Es una máquina de estados por tabla (no una clase por estado como en el State clásico).
- [`OrdenTrabajo.java`](src/main/java/com/taller/dominio/modelo/OrdenTrabajo.java) — todo cambio de estado pasa por cambiarEstado(), que consulta la máquina de estados de EstadoOrden.

## Agregado y raíz de agregado (DDD)

- [`Cotizacion.java`](src/main/java/com/taller/dominio/modelo/Cotizacion.java) — forma parte del agregado OrdenTrabajo: solo se modifica a través de la orden.
- [`Factura.java`](src/main/java/com/taller/dominio/modelo/Factura.java) — raíz de agregado: los pagos solo se registran a través de la factura, que protege sus reglas (por ejemplo, no se modifica una vez validada).
- [`ItemOrdenCompra.java`](src/main/java/com/taller/dominio/modelo/ItemOrdenCompra.java) — pertenece al agregado OrdenCompra.
- [`OrdenCompra.java`](src/main/java/com/taller/dominio/modelo/OrdenCompra.java) — raíz de agregado: sus ítems solo cambian a través de la orden de compra.
- [`OrdenTrabajo.java`](src/main/java/com/taller/dominio/modelo/OrdenTrabajo.java) — raíz del agregado central: diagnósticos, cotización, servicios, repuestos e historial solo se modifican a través de sus métodos.
- [`ReferenciaCliente.java`](src/main/java/com/taller/dominio/modelo/ReferenciaCliente.java) — desnormalización deliberada: la orden guarda estos datos tal como estaban ese día, sin leer otra colección.
- [`ReferenciaMecanico.java`](src/main/java/com/taller/dominio/modelo/ReferenciaMecanico.java) — desnormalización deliberada: la orden guarda estos datos tal como estaban ese día, sin leer otra colección.
- [`ReferenciaVehiculo.java`](src/main/java/com/taller/dominio/modelo/ReferenciaVehiculo.java) — desnormalización deliberada: la orden guarda estos datos tal como estaban ese día, sin leer otra colección.
- [`Repuesto.java`](src/main/java/com/taller/dominio/modelo/Repuesto.java) — raíz de agregado: el stock solo cambia por sus métodos, que validan que no quede negativo.

## Objeto de valor (DDD)

- [`AnotacionHistorial.java`](src/main/java/com/taller/dominio/modelo/AnotacionHistorial.java) — record inmutable que se identifica por sus valores, no por un id.
- [`CambioEstado.java`](src/main/java/com/taller/dominio/modelo/CambioEstado.java) — record inmutable que se identifica por sus valores, no por un id.
- [`Diagnostico.java`](src/main/java/com/taller/dominio/modelo/Diagnostico.java) — record inmutable que se identifica por sus valores, no por un id.
- [`LineaRepuesto.java`](src/main/java/com/taller/dominio/modelo/LineaRepuesto.java) — record inmutable que se identifica por sus valores, no por un id.
- [`LineaServicio.java`](src/main/java/com/taller/dominio/modelo/LineaServicio.java) — record inmutable que se identifica por sus valores, no por un id.
- [`Pago.java`](src/main/java/com/taller/dominio/modelo/Pago.java) — record inmutable que se identifica por sus valores, no por un id.
- [`ReferenciaCliente.java`](src/main/java/com/taller/dominio/modelo/ReferenciaCliente.java) — record inmutable que se identifica por sus valores, no por un id.
- [`ReferenciaMecanico.java`](src/main/java/com/taller/dominio/modelo/ReferenciaMecanico.java) — record inmutable que se identifica por sus valores, no por un id.
- [`ReferenciaVehiculo.java`](src/main/java/com/taller/dominio/modelo/ReferenciaVehiculo.java) — record inmutable que se identifica por sus valores, no por un id.

## Entidad (DDD)

- [`Cita.java`](src/main/java/com/taller/dominio/modelo/Cita.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Cliente.java`](src/main/java/com/taller/dominio/modelo/Cliente.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`MantenimientoPreventivo.java`](src/main/java/com/taller/dominio/modelo/MantenimientoPreventivo.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Mecanico.java`](src/main/java/com/taller/dominio/modelo/Mecanico.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`MovimientoInventario.java`](src/main/java/com/taller/dominio/modelo/MovimientoInventario.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Proveedor.java`](src/main/java/com/taller/dominio/modelo/Proveedor.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Recepcionista.java`](src/main/java/com/taller/dominio/modelo/Recepcionista.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Servicio.java`](src/main/java/com/taller/dominio/modelo/Servicio.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.
- [`Usuario.java`](src/main/java/com/taller/dominio/modelo/Usuario.java) — tiene identidad propia (id) y su estado cambia a lo largo del tiempo.

## Método fábrica estático

- [`Cita.java`](src/main/java/com/taller/dominio/modelo/Cita.java) — solicitar() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Cliente.java`](src/main/java/com/taller/dominio/modelo/Cliente.java) — nuevo() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Cotizacion.java`](src/main/java/com/taller/dominio/modelo/Cotizacion.java) — emitir() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Diagnostico.java`](src/main/java/com/taller/dominio/modelo/Diagnostico.java) — desdeEscaner() y manual() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Factura.java`](src/main/java/com/taller/dominio/modelo/Factura.java) — emitirPara() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`MantenimientoPreventivo.java`](src/main/java/com/taller/dominio/modelo/MantenimientoPreventivo.java) — programar() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Mecanico.java`](src/main/java/com/taller/dominio/modelo/Mecanico.java) — nuevo() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`MovimientoInventario.java`](src/main/java/com/taller/dominio/modelo/MovimientoInventario.java) — entrada() y salida() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`OrdenCompra.java`](src/main/java/com/taller/dominio/modelo/OrdenCompra.java) — borradorPorReposicion() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`OrdenTrabajo.java`](src/main/java/com/taller/dominio/modelo/OrdenTrabajo.java) — recibir() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Pago.java`](src/main/java/com/taller/dominio/modelo/Pago.java) — de() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Recepcionista.java`](src/main/java/com/taller/dominio/modelo/Recepcionista.java) — nuevo() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Repuesto.java`](src/main/java/com/taller/dominio/modelo/Repuesto.java) — nuevo() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.
- [`Usuario.java`](src/main/java/com/taller/dominio/modelo/Usuario.java) — nuevo() crea el objeto en un estado inicial válido y le da nombre a esa forma de crearlo.

## Factory (@Bean de Spring)

- [`ConfiguracionHttp.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionHttp.java) — el método @Bean fabrica el objeto que Spring inyecta donde se necesite.
- [`ConfiguracionOpenApi.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionOpenApi.java) — el método @Bean fabrica la descripción OpenAPI.

## DTO (Data Transfer Object)

- [`AutenticacionControlador.java`](src/main/java/com/taller/api/controlador/AutenticacionControlador.java) — entra y sale por objetos de transferencia (LoginDTO, TokenDTO, CambiarClaveDTO), nunca expone las entidades del dominio.
- [`CitaControlador.java`](src/main/java/com/taller/api/controlador/CitaControlador.java) — entra y sale por objetos de transferencia (CrearCitaDTO), nunca expone las entidades del dominio.
- [`ClienteControlador.java`](src/main/java/com/taller/api/controlador/ClienteControlador.java) — entra y sale por objetos de transferencia (CrearClienteDTO), nunca expone las entidades del dominio.
- [`ConsultaPublicaControlador.java`](src/main/java/com/taller/api/controlador/ConsultaPublicaControlador.java) — ConsultaPublicaRespuestaDTO expone solo lo que el cliente puede ver de su propia orden.
- [`FacturaControlador.java`](src/main/java/com/taller/api/controlador/FacturaControlador.java) — entra y sale por objetos de transferencia (RegistrarPagoDTO), nunca expone las entidades del dominio.
- [`InventarioControlador.java`](src/main/java/com/taller/api/controlador/InventarioControlador.java) — entra y sale por objetos de transferencia (CrearRepuestoDTO, AjustarCantidadCompraDTO, RecepcionCompraDTO), nunca expone las entidades del dominio.
- [`OrdenTrabajoControlador.java`](src/main/java/com/taller/api/controlador/OrdenTrabajoControlador.java) — entra y sale por objetos de transferencia (CrearOrdenDTO, CambiarEstadoDTO, OrdenDetalleDTO, OrdenResumenDTO...), nunca expone las entidades del dominio.
- [`VehiculoControlador.java`](src/main/java/com/taller/api/controlador/VehiculoControlador.java) — entra y sale por objetos de transferencia (CrearVehiculoDTO), nunca expone las entidades del dominio.
- [`AjustarCantidadCompraDTO.java`](src/main/java/com/taller/api/dto/AjustarCantidadCompraDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`AsignarMecanicoDTO.java`](src/main/java/com/taller/api/dto/AsignarMecanicoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CambiarClaveDTO.java`](src/main/java/com/taller/api/dto/CambiarClaveDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CambiarEstadoDTO.java`](src/main/java/com/taller/api/dto/CambiarEstadoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`ConsultaPublicaDTO.java`](src/main/java/com/taller/api/dto/ConsultaPublicaDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`ConsultaPublicaRespuestaDTO.java`](src/main/java/com/taller/api/dto/ConsultaPublicaRespuestaDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`ConsumirRepuestoDTO.java`](src/main/java/com/taller/api/dto/ConsumirRepuestoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CrearCitaDTO.java`](src/main/java/com/taller/api/dto/CrearCitaDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CrearClienteDTO.java`](src/main/java/com/taller/api/dto/CrearClienteDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CrearOrdenDTO.java`](src/main/java/com/taller/api/dto/CrearOrdenDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CrearRepuestoDTO.java`](src/main/java/com/taller/api/dto/CrearRepuestoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`CrearVehiculoDTO.java`](src/main/java/com/taller/api/dto/CrearVehiculoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`ErrorRespuesta.java`](src/main/java/com/taller/api/dto/ErrorRespuesta.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`LoginDTO.java`](src/main/java/com/taller/api/dto/LoginDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`OrdenDetalleDTO.java`](src/main/java/com/taller/api/dto/OrdenDetalleDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`OrdenResumenDTO.java`](src/main/java/com/taller/api/dto/OrdenResumenDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`RecepcionCompraDTO.java`](src/main/java/com/taller/api/dto/RecepcionCompraDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`RegistrarDiagnosticoDTO.java`](src/main/java/com/taller/api/dto/RegistrarDiagnosticoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`RegistrarPagoDTO.java`](src/main/java/com/taller/api/dto/RegistrarPagoDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.
- [`TokenDTO.java`](src/main/java/com/taller/api/dto/TokenDTO.java) — record inmutable que define el contrato JSON de la API (con validaciones de Bean Validation) y separa la API del modelo de dominio.

## Data Mapper / Assembler

- [`OrdenTrabajoControlador.java`](src/main/java/com/taller/api/controlador/OrdenTrabajoControlador.java) — usa MapeadorApi para convertir la orden del dominio en OrdenDetalleDTO y OrdenResumenDTO.
- [`MapeadorApi.java`](src/main/java/com/taller/api/mapeador/MapeadorApi.java) — concentra en un solo lugar la traducción dominio → DTO (Assembler); los controladores no arman respuestas a mano.
- [`Mapeadores.java`](src/main/java/com/taller/infraestructura/persistencia/Mapeadores.java) — Data Mapper: gracias a esta clase el dominio no lleva ninguna anotación de Spring Data.

## Service Layer (capa de servicio, fachada de casos de uso)

- [`AutenticacionService.java`](src/main/java/com/taller/aplicacion/servicio/AutenticacionService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`CitaService.java`](src/main/java/com/taller/aplicacion/servicio/CitaService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`ClienteService.java`](src/main/java/com/taller/aplicacion/servicio/ClienteService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`DiagnosticoService.java`](src/main/java/com/taller/aplicacion/servicio/DiagnosticoService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`FacturacionService.java`](src/main/java/com/taller/aplicacion/servicio/FacturacionService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`InventarioService.java`](src/main/java/com/taller/aplicacion/servicio/InventarioService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`OrdenTrabajoService.java`](src/main/java/com/taller/aplicacion/servicio/OrdenTrabajoService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`PersonalService.java`](src/main/java/com/taller/aplicacion/servicio/PersonalService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.
- [`VehiculoService.java`](src/main/java/com/taller/aplicacion/servicio/VehiculoService.java) — orquesta el caso de uso: carga del repositorio, delega las reglas en el dominio y guarda. Los controladores solo hablan con esta fachada.

## Inyección de dependencias / Inversión de control

- [`TallerApplication.java`](src/main/java/com/taller/TallerApplication.java) — Spring crea y conecta todos los componentes (@Service, @Component, @Repository) por constructor; cada uno es una única instancia (singleton) administrada por el contenedor.
- [`AutenticacionService.java`](src/main/java/com/taller/aplicacion/servicio/AutenticacionService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`CitaService.java`](src/main/java/com/taller/aplicacion/servicio/CitaService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`ClienteService.java`](src/main/java/com/taller/aplicacion/servicio/ClienteService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`DiagnosticoService.java`](src/main/java/com/taller/aplicacion/servicio/DiagnosticoService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`FacturacionService.java`](src/main/java/com/taller/aplicacion/servicio/FacturacionService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`InventarioService.java`](src/main/java/com/taller/aplicacion/servicio/InventarioService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`OrdenTrabajoService.java`](src/main/java/com/taller/aplicacion/servicio/OrdenTrabajoService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`PersonalService.java`](src/main/java/com/taller/aplicacion/servicio/PersonalService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.
- [`VehiculoService.java`](src/main/java/com/taller/aplicacion/servicio/VehiculoService.java) — recibe los puertos (interfaces del dominio) por constructor; no conoce MongoDB ni las integraciones concretas.

## Controller (MVC)

- [`AutenticacionControlador.java`](src/main/java/com/taller/api/controlador/AutenticacionControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`CitaControlador.java`](src/main/java/com/taller/api/controlador/CitaControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`ClienteControlador.java`](src/main/java/com/taller/api/controlador/ClienteControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`ConsultaPublicaControlador.java`](src/main/java/com/taller/api/controlador/ConsultaPublicaControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`FacturaControlador.java`](src/main/java/com/taller/api/controlador/FacturaControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`InventarioControlador.java`](src/main/java/com/taller/api/controlador/InventarioControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`OrdenTrabajoControlador.java`](src/main/java/com/taller/api/controlador/OrdenTrabajoControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`PersonalControlador.java`](src/main/java/com/taller/api/controlador/PersonalControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`VehiculoControlador.java`](src/main/java/com/taller/api/controlador/VehiculoControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.
- [`WebhookControlador.java`](src/main/java/com/taller/api/controlador/WebhookControlador.java) — recibe la petición HTTP, valida la entrada y delega en la capa de servicio; no contiene reglas de negocio.

## Chain of Responsibility

- [`LimiteConsultaPublica.java`](src/main/java/com/taller/api/LimiteConsultaPublica.java) — es un eslabón de la cadena de filtros de Servlet: responde 429 y corta la cadena, o pasa la petición al siguiente filtro.
- [`ConfiguracionSeguridad.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionSeguridad.java) — registra FiltroJwt en la cadena de filtros por la que pasa cada petición.
- [`FiltroJwt.java`](src/main/java/com/taller/infraestructura/seguridad/FiltroJwt.java) — eslabón de la cadena de filtros: hace su parte y llama a cadena.doFilter() para pasar la petición al siguiente.

## Builder

- [`ConfiguracionSeguridad.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionSeguridad.java) — HttpSecurity es un constructor fluido: se encadenan las reglas y build() arma la cadena de seguridad.

## Retry (reintento con espera creciente)

- [`DianFacturacionClient.java`](src/main/java/com/taller/infraestructura/integracion/DianFacturacionClient.java) — si el proveedor falla, reintenta con espera creciente; al agotar los intentos la factura queda pendiente de envío.

## Timeout

- [`ConfiguracionHttp.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionHttp.java) — patrón de resiliencia: una integración lenta falla rápido en lugar de bloquear hilos del servidor.

## Rate limiting (ventana fija)

- [`LimiteConsultaPublica.java`](src/main/java/com/taller/api/LimiteConsultaPublica.java) — cuenta las peticiones de cada IP en ventanas fijas de un minuto.

## Gateway

- [`WhatsAppGateway.java`](src/main/java/com/taller/infraestructura/integracion/WhatsAppGateway.java) — punto único de acceso al servicio externo de mensajería.

## Webhook

- [`WebhookControlador.java`](src/main/java/com/taller/api/controlador/WebhookControlador.java) — el proveedor de mensajería llama a esta URL cuando el cliente responde; antes de actuar se verifica la firma HMAC del cuerpo.

## Manejador centralizado de errores

- [`ManejadorGlobalErrores.java`](src/main/java/com/taller/api/ManejadorGlobalErrores.java) — @RestControllerAdvice: un único punto traduce cada tipo de excepción a su código HTTP y Spring lo aplica a todos los controladores.

## RBAC (control de acceso por roles)

- [`Rol.java`](src/main/java/com/taller/dominio/modelo/Rol.java) — cada rol declara su conjunto de permisos; ConfiguracionSeguridad traduce esa matriz a reglas HTTP.
- [`ConfiguracionSeguridad.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionSeguridad.java) — aplica por ruta los roles definidos en el dominio (Rol).

## Modelo de persistencia separado del dominio

- [`CitaDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/CitaDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`ClienteDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/ClienteDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`ConsecutivoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/ConsecutivoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`EmpleadoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/EmpleadoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`FacturaDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/FacturaDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`MantenimientoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/MantenimientoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`MovimientoInventarioDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/MovimientoInventarioDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`OrdenCompraDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/OrdenCompraDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`OrdenTrabajoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/OrdenTrabajoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`ProveedorDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/ProveedorDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`RepuestoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/RepuestoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`ServicioDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/ServicioDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`UsuarioDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/UsuarioDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.
- [`VehiculoDoc.java`](src/main/java/com/taller/infraestructura/persistencia/documento/VehiculoDoc.java) — refleja cómo se guarda el dato (con anotaciones de Spring Data); Mapeadores lo convierte al objeto del dominio.

## Repository de Spring Data (proxy dinámico)

- [`CitaMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/CitaMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`ClienteMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/ClienteMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`EmpleadoMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/EmpleadoMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`FacturaMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/FacturaMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`MantenimientoMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/MantenimientoMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`MovimientoInventarioMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/MovimientoInventarioMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`OrdenCompraMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/OrdenCompraMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`OrdenTrabajoMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/OrdenTrabajoMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`ProveedorMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/ProveedorMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`RepuestoMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/RepuestoMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`ServicioMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/ServicioMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`UsuarioMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/UsuarioMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.
- [`VehiculoMongoRepository.java`](src/main/java/com/taller/infraestructura/persistencia/repositorio/VehiculoMongoRepository.java) — interfaz declarativa: Spring genera la implementación en tiempo de ejecución (un proxy dinámico) a partir de los nombres de los métodos.

## Object Mother (pruebas)

- [`OrdenTrabajoMother.java`](src/test/java/com/taller/dominio/modelo/OrdenTrabajoMother.java) — centraliza la creación de objetos de prueba: si la entidad cambia, se ajusta solo este archivo.

## Pruebas de arquitectura (ArchUnit)

- [`ArquitecturaTest.java`](src/test/java/com/taller/ArquitecturaTest.java) — ArchUnit revisa las dependencias entre paquetes como si fuera una prueba más.

## Herencia y polimorfismo

- [`ManejadorEventos.java`](src/main/java/com/taller/aplicacion/manejador/ManejadorEventos.java) — cada motor hereda de esta clase y aporta su propio comportamiento (polimorfismo).
- [`CredencialesInvalidasException.java`](src/main/java/com/taller/dominio/excepcion/CredencialesInvalidasException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`IntegracionNoDisponibleException.java`](src/main/java/com/taller/dominio/excepcion/IntegracionNoDisponibleException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`RecursoDuplicadoException.java`](src/main/java/com/taller/dominio/excepcion/RecursoDuplicadoException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`RecursoNoEncontradoException.java`](src/main/java/com/taller/dominio/excepcion/RecursoNoEncontradoException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`ReglaNegocioException.java`](src/main/java/com/taller/dominio/excepcion/ReglaNegocioException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`StockInsuficienteException.java`](src/main/java/com/taller/dominio/excepcion/StockInsuficienteException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`TallerException.java`](src/main/java/com/taller/dominio/excepcion/TallerException.java) — jerarquía de excepciones: cada subclase representa un tipo de falla y ManejadorGlobalErrores elige el código HTTP según la clase.
- [`TransicionInvalidaException.java`](src/main/java/com/taller/dominio/excepcion/TransicionInvalidaException.java) — hereda de TallerException; el tipo de la excepción decide el código HTTP, sin if en los controladores.
- [`Automovil.java`](src/main/java/com/taller/dominio/modelo/Automovil.java) — especializa Vehiculo e implementa getTipo().
- [`Cliente.java`](src/main/java/com/taller/dominio/modelo/Cliente.java) — especializa Persona.
- [`Empleado.java`](src/main/java/com/taller/dominio/modelo/Empleado.java) — clase abstracta: Mecanico y Recepcionista la especializan.
- [`Mecanico.java`](src/main/java/com/taller/dominio/modelo/Mecanico.java) — especializa Empleado.
- [`Motocicleta.java`](src/main/java/com/taller/dominio/modelo/Motocicleta.java) — especializa Vehiculo e implementa getTipo().
- [`Persona.java`](src/main/java/com/taller/dominio/modelo/Persona.java) — clase abstracta: Cliente y Empleado la especializan.
- [`Recepcionista.java`](src/main/java/com/taller/dominio/modelo/Recepcionista.java) — especializa Empleado.
- [`Vehiculo.java`](src/main/java/com/taller/dominio/modelo/Vehiculo.java) — clase abstracta con el método abstracto getTipo(): Automovil y Motocicleta la especializan.

## Clases sin un patrón de diseño específico

Piezas sencillas (enums, utilidades, páginas, pruebas unitarias): tienen igual su comentario de para qué sirven.

- [`ConsultaPublicaTest.java`](src/test/java/com/taller/api/ConsultaPublicaTest.java)
- [`EventosYExcepcionesTest.java`](src/test/java/com/taller/dominio/evento/EventosYExcepcionesTest.java)
- [`DetalleDeAgregadosTest.java`](src/test/java/com/taller/dominio/modelo/DetalleDeAgregadosTest.java)
- [`EstadoOrdenTest.java`](src/test/java/com/taller/dominio/modelo/EstadoOrdenTest.java)
- [`InventarioYFacturacionTest.java`](src/test/java/com/taller/dominio/modelo/InventarioYFacturacionTest.java)
- [`MecanicoYRecepcionTest.java`](src/test/java/com/taller/dominio/modelo/MecanicoYRecepcionTest.java)
- [`OrdenTrabajoTest.java`](src/test/java/com/taller/dominio/modelo/OrdenTrabajoTest.java)
- [`PersonasVehiculosYCatalogoTest.java`](src/test/java/com/taller/dominio/modelo/PersonasVehiculosYCatalogoTest.java)
- [`ReposicionYComprasTest.java`](src/test/java/com/taller/dominio/modelo/ReposicionYComprasTest.java)
- [`SeguridadTest.java`](src/test/java/com/taller/dominio/modelo/SeguridadTest.java)
- [`FacturacionSimuladaTest.java`](src/test/java/com/taller/infraestructura/integracion/FacturacionSimuladaTest.java)
- [`DirectorioUsuarios.java`](src/main/java/com/taller/aplicacion/servicio/DirectorioUsuarios.java)
- [`CanalAprobacion.java`](src/main/java/com/taller/dominio/modelo/CanalAprobacion.java)
- [`EstadoCita.java`](src/main/java/com/taller/dominio/modelo/EstadoCita.java)
- [`EstadoCotizacion.java`](src/main/java/com/taller/dominio/modelo/EstadoCotizacion.java)
- [`EstadoDian.java`](src/main/java/com/taller/dominio/modelo/EstadoDian.java)
- [`EstadoOrdenCompra.java`](src/main/java/com/taller/dominio/modelo/EstadoOrdenCompra.java)
- [`MetodoPago.java`](src/main/java/com/taller/dominio/modelo/MetodoPago.java)
- [`Montos.java`](src/main/java/com/taller/dominio/modelo/Montos.java)
- [`OrigenDiagnostico.java`](src/main/java/com/taller/dominio/modelo/OrigenDiagnostico.java)
- [`TipoMovimiento.java`](src/main/java/com/taller/dominio/modelo/TipoMovimiento.java)
- [`TipoVehiculo.java`](src/main/java/com/taller/dominio/modelo/TipoVehiculo.java)
- [`CargaDatosIniciales.java`](src/main/java/com/taller/infraestructura/configuracion/CargaDatosIniciales.java)
- [`ConfiguracionCors.java`](src/main/java/com/taller/infraestructura/configuracion/ConfiguracionCors.java)
