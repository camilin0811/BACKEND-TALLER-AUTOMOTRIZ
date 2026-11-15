package com.taller.infraestructura.seguridad;

import com.taller.dominio.puerto.CodificadorClaves;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CodificadorBCrypt implements CodificadorClaves {

    private final PasswordEncoder delegado = new BCryptPasswordEncoder();

    @Override
    public String codificar(String claveEnClaro) {
        return delegado.encode(claveEnClaro);
    }

    @Override
    public boolean coincide(String claveEnClaro, String hash) {
        if (claveEnClaro == null || hash == null) {
            return false;
        }
        return delegado.matches(claveEnClaro, hash);
    }
}
