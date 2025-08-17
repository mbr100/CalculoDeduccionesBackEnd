package com.marioborrego.api.calculodeduccionesbackend.helper;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.CrearEconomicoDTO;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class Sanitize {
    public static CrearEconomicoDTO sanitize(CrearEconomicoDTO dto) {
        CrearEconomicoDTO sanitizedDto = new CrearEconomicoDTO();
        sanitizedDto.setNombre(Jsoup.clean(dto.getNombre(), Safelist.none()));
        sanitizedDto.setCif(Jsoup.clean(dto.getCif(), Safelist.none()));
        sanitizedDto.setDireccion(Jsoup.clean(dto.getDireccion(), Safelist.none()));
        sanitizedDto.setTelefono(Jsoup.clean(dto.getTelefono(), Safelist.none()));
        sanitizedDto.setNombreContacto(Jsoup.clean(dto.getNombreContacto(), Safelist.none()));
        sanitizedDto.setEmailContacto(Jsoup.clean(dto.getEmailContacto(), Safelist.none()));
        sanitizedDto.setUrllogo(Jsoup.clean(dto.getUrllogo(), Safelist.none()));
        sanitizedDto.setUrlWeb(Jsoup.clean(dto.getUrlWeb(), Safelist.none()));
        return sanitizedDto;
    }
}
