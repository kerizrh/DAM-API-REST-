package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.RegisterDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.TokenDTO;

public interface AuthServices {
    TokenDTO login(String user, String pass);
    boolean register(RegisterDTO registerDTO);
}
