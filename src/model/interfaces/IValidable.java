package model.interfaces;

import excepts.UsuarioNoValidoException;

public interface IValidable {
    void validar() throws UsuarioNoValidoException;
}