/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.controller;

import com.senai.frotaFront.model.AuthDTO;
import com.senai.frotaFront.model.LoginResponseDTO;
import com.senai.frotaFront.model.UserDTO;
import com.senai.frotaFront.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Micro
 */
@Controller
public class UserController {
    
    // Injeção do serviço de autenticação para delegar a lógica de login.    
    @Autowired
    private ApiService restService;
    
     // Tratador para requisições GET no caminho raiz "/".
    // Retorna o nome da view Thymeleaf "index".
    @GetMapping("/")
    public String home(HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Para nao logado
        if(token == null) {
            return "redirect:/login";
        }
        // Para Admin
        if ("administrador".equalsIgnoreCase(role)) {
            return "redirect:/admin";
        }
        // Para Operador
        return "redirect:/op";
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
    public String logar(@ModelAttribute AuthDTO credenciais, HttpSession session, Model model) {
        // Chama o serviço de autenticação para obter um token JWT ou similar.
        
        try {
            LoginResponseDTO l = restService.logar(credenciais);
            // Armazena nome, role e token na sessão HTTP para uso posterior.

            // Salva atributos para a sessao com os metodos .get do LoginResponseDTO
            session.setAttribute("token", l.getToken());
            session.setAttribute("role", l.getCargo());
            session.setAttribute("nome", l.getNome());
            session.setAttribute("id", l.getId());
            
            // REDIRECIONAMENTO POR CARGO
            if ("administrador".equalsIgnoreCase(l.getCargo())) {
                return "redirect:/admin";
            } else {
                return "redirect:/op";
            }
            
        } catch (Exception e) {
            model.addAttribute("erro", "E-mail ou senha incorretos.");
            model.addAttribute("credenciais", credenciais);
            return "login";
        }
    }
    
    // Pagina para Operadores
    @GetMapping("/home")
    public String userHome(HttpSession session) {
        if (session.getAttribute("token") == null) {
            return "redirect:/login";
        }
        return "index";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserDTO newUser = new UserDTO();
        model.addAttribute("user", newUser);
        return "register";
    }

    @PostMapping("/register")
    public String sendRegister(@ModelAttribute UserDTO user, Model model) {
        
        // Gera erro para o front-end, retornando na mesma pagina com mensagem 
        // Evita ir para o Service para verificacao para voltar aqui e ser redirecionado
        if (!user.getSenha().equals(user.getConfirmSenha())) {
            model.addAttribute("erro", "As senhas não estão iguais");
            model.addAttribute("user", user);
            return "register";
        }
        
        restService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        Long id = (Long) session.getAttribute("idUser");
        
        if (token == null) {
            return "redirect:/login";
        }
        
        try {
            UserDTO u = restService.findUserById(id, token);
//            u.setConfirmSenha("");
            
            model.addAttribute("user", u);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao buscar dados do perfil.");
            model.addAttribute("user", new UserDTO());
        }

        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute UserDTO user, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        
        if (token == null) {
            return "redirect:/login";
        }
        if (!user.getSenha().equals(user.getConfirmSenha())) {
            model.addAttribute("erro", "As senhas não estão iguais");
            model.addAttribute("user", user);
            return "profile";
        }
        
        try {
            restService.editProfile(user, token);
            model.addAttribute("sucesso", "Perfil atualizado com sucesso!");
            
            session.setAttribute("nome", user.getNome());
            
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao atualizar o perfil.");
        }
        
        return "profile";
    }
}
