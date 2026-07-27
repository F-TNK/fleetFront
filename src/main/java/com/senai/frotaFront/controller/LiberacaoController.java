/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.controller;

import com.senai.frotaFront.model.LiberacaoDTO;
import com.senai.frotaFront.service.ApiService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Micro
 */
@Controller
public class LiberacaoController {
    
    // Injeção do serviço de autenticação para delegar a lógica de login.    
    @Autowired
    private ApiService restService;
    
    // ----------------------- ADMIN -----------------------

    
    @GetMapping("/liberacoes")
    public String liberacoes(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        // Validacao token
        if (token == null) {
            return "redirect:/login";
        }
        // Valida Admin, se nao redirect pra pagina do operado
        if (!"administrador".equalsIgnoreCase(role)) {
            return "redirect:/op";
        }

        model.addAttribute("alerta", restService.listAlerta(token));
        model.addAttribute("open", restService.listOpen(token));
        model.addAttribute("operadores", restService.listUsers(token));
        model.addAttribute("equipamentos", restService.listEquip(token));
        model.addAttribute("liberacao", new LiberacaoDTO());

        return "liberacoes";
    }

    @GetMapping("/historico")
    public String historico(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao token e admin
        if (token == null) {
            return "redirect:/login";
        }
        // Valida Admin, se nao redirect pra pagina do operado
        if (!"administrador".equalsIgnoreCase(role)) {
            return "redirect:/op";
        }

        List<LiberacaoDTO> closeList = restService.listClose(token);
        model.addAttribute("close", closeList);

        return "historico";
    }

    @PostMapping("/admin/liberacao/save")
    public String addLiberacao(@ModelAttribute LiberacaoDTO liberacao, HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao admin
        if (token != null && "administrador".equalsIgnoreCase(role)) {
            restService.registerLiberacao(liberacao, token);
        }

        return "redirect:/liberacoes";
    }

    @PostMapping("/admin/liberacao/edit")
    public String editLiberacao(@ModelAttribute LiberacaoDTO liberacao, HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao admin
        if (token != null && "administrador".equalsIgnoreCase(role)) {
            restService.editLiberacao(liberacao, token);
        }

        return "redirect:/liberacoes";
    }

    @GetMapping("/admin/liberacao/delete/{id}")
    public String deleteLiberacao(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao token e admin
        if (token != null && "administrador".equalsIgnoreCase(role)) {
            restService.deleteLiberacao(id, token);
        }

        return "redirect:/liberacoes";
    }

    @GetMapping("/admin/liberacao/resolve/{id}")
    public String resolve(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao token e admin
        if (token != null && "administrador".equalsIgnoreCase(role)) {
            restService.resolve(id, token);
        }

        return "redirect:/liberacoes";
    }

    // ----------------------- OPERADOR -----------------------

    @GetMapping("/op")
    public String opLiberacoes(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        
        //Valid Token
        if (token == null) {
            return "redirect:/login";
        }

        // resgata id
        Long idUser = (Long) session.getAttribute("id");

        if (idUser != null) {
            model.addAttribute("liberacoes", restService.listById(idUser, token));
        }

        return "op";
    }

    @PostMapping("/op/liberacao/pickup")
    public String pickUp(@ModelAttribute LiberacaoDTO liberacao, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token != null) {
            restService.pickUp(liberacao, token);
        }

        return "redirect:/op";
    }

    @PostMapping("/op/liberacao/close")
    public String close(@ModelAttribute LiberacaoDTO liberacao, HttpSession session) {
        String token = (String) session.getAttribute("token");

        if (token != null) {
            restService.close(liberacao, token);
        }

        return "redirect:/op";
    }
}
