/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.controller;

import com.senai.frotaFront.model.AuthDTO;
import com.senai.frotaFront.model.UserDTO;
import com.senai.frotaFront.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Micro
 */
public class UserController {
    
    // Injeção do serviço de autenticação para delegar a lógica de login.    
    @Autowired
    private ApiService restService;
    
     // Tratador para requisições GET no caminho raiz "/".
    // Retorna o nome da view Thymeleaf "index".
    @GetMapping("/")
    public String home(HttpSession session) {
        Object token = session.getAttribute("token");
        
        if(token == null) {
            return "redirect:/login";
        }
        return "index";
    }

    // Tratador para requisições GET em "/login".
    // Prepara o modelo com um objeto UserRequestDTO vazio para preencher o formulário.
    @GetMapping("/login")
    public String login(Model model) {
        AuthDTO credenciais = new AuthDTO();
        model.addAttribute("credenciais", credenciais);
        return "login";
    }

    // Tratador para requisições POST em "/logar".
    // Recebe as credenciais submetidas pelo formulário e tenta autenticar.
    @PostMapping("/logar")
    public String logar(@ModelAttribute AuthDTO credenciais, HttpSession session) {
        // Chama o serviço de autenticação para obter um token JWT ou similar.
        String token = restService.logar(credenciais);
        // Armazena o token na sessão HTTP para uso posterior.
        
        String role = (String) session.getAttribute("role");
        System.out.println("token: " + token);
        session.setAttribute("token", token);
        // Redireciona de volta para a página inicial após login bem sucedido.
        session.setAttribute("role", role);
        return "redirect:/editais";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registrar")
    public String registrar(Model model) {
        UserDTO newUser = new UserDTO();
        model.addAttribute("user", newUser);
        return "registrar";
    }

    @PostMapping("/registrar")
    public String mandarRegistro(@ModelAttribute AuthDTO user, Model model) {
        
        // Gera erro para o front-end, retornando na mesma pagina com mensagem 
        if (!user.getSenha().equals(user.getConfirmSenha())) {
            model.addAttribute("erro", "As senhas não estão iguais");
            model.addAttribute("user", user);
            return "registrar";
        }
        
        restService.registrar(user);
        return "redirect:/login";
    }
}
