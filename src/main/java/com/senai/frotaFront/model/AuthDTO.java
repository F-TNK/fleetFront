/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.model;

/**
 *
 * @author ftana
 */
public class AuthDTO {
    
    private String nome;
    private String email;
    private String senha;
    private String confirmSenha;
    private String cargo;

    public AuthDTO() {
    }

    public AuthDTO(String nome, String email, String senha, String confirmSenha, String cargo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmSenha = confirmSenha;
        this.cargo = cargo;
    }
    

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmSenha() {
        return confirmSenha;
    }

    public void setConfirmSenha(String confirmSenha) {
        this.confirmSenha = confirmSenha;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
