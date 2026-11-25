package com.taller.dominio.puerto;

import java.math.BigDecimal;

/** Mensajeria saliente hacia el cliente. */
/**
 * Para qué sirve: Envía mensajes y correos al cliente. Lo implementa WhatsAppGateway.
 * <p>PATRÓN Arquitectura hexagonal (puertos y adaptadores): puerto: interfaz que define el dominio y
 *     que la infraestructura implementa. El dominio depende de la interfaz, no de la tecnología
 *     (inversión de dependencias).
 */
public interface NotificadorCliente {

    void enviarCotizacion(String telefono, String numeroCotizacion, BigDecimal total);

    void enviarMensaje(String telefono, String texto);

    void enviarCorreo(String email, String asunto, String cuerpo);
}
