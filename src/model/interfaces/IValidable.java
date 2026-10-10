package model.interfaces;

import excepts.UsuarioNoValidoException;

/**
 * Principio de Segregación de Interfaces (ISP):
 * Define un contrato específico para la validación,
 * que solo implementarán las clases que lo necesiten (Usuario).
 */
public interface IValidable {
    void validar() throws UsuarioNoValidoException;
}