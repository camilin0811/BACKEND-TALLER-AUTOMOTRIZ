package com.taller.dominio.puerto;

import java.math.BigDecimal;

/** Mensajeria saliente hacia el cliente. */
public interface NotificadorCliente {

    void enviarCotizacion(String telefono, String numeroCotizacion, BigDecimal total);

    void enviarMensaje(String telefono, String texto);

    void enviarCorreo(String email, String asunto, String cuerpo);
}
