/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.controller;

import com.senai.frotaFront.model.EquipDTO;
import com.senai.frotaFront.model.UserDTO;
import com.senai.frotaFront.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 *
 * @author Micro
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    // Classe para validar CARGO e redirecionar para /admin
    
    @Autowired
    private ApiService rService;

    @GetMapping
    public String adminPage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        if (token == null || !"administrador".equalsIgnoreCase(role)) {
            return "redirect:/login";
        }                           // IgnoreCase - ignora Maiusculo e minusculo

        // alimenta o MODEL com as listas
        model.addAttribute("users", rService.listUsers(token));
        model.addAttribute("equips", rService.listEquip(token));
        // para cadastro - NEW EUIP - formulário do Thymeleaf (th:object="${equip}")
        model.addAttribute("equip", new EquipDTO());
        
        return "admin";
    }
    
    
    @PostMapping("/equip/salvar")
    public String addEquip(@ModelAttribute EquipDTO equip, HttpSession session, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        
        if (token != null) {
//            rService.addEquip(equip, token);
            try {
                rService.addEquip(equip, token);
                redirect.addFlashAttribute("sucesso", "Equipamento Registrado!");
            } catch (HttpStatusCodeException e) {
                redirect.addFlashAttribute("erro", mensagemErro(e));
            } catch (Exception e) {
                redirect.addFlashAttribute("erro", "Falha no registro. Tente novamente.");
            }
        }
        
        return "redirect:/admin";
    }
    
    @PostMapping("/equip/editar")
    public String editEquip(@ModelAttribute EquipDTO equip, HttpSession session, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");

        if (token != null) {
            rService.editEquip(equip, token);
            try {
                rService.editEquip(equip, token);
                redirect.addFlashAttribute("sucesso", "Equipamento Atualizaado!");
            } catch (HttpStatusCodeException e) {
                redirect.addFlashAttribute("erro", mensagemErro(e));
            } catch (Exception e) {
                redirect.addFlashAttribute("erro", "Falha no registro. Tente novamente.");
            }
        }

        return "redirect:/admin";
    }

    @GetMapping("/equip/delete/{id}")
    public String deleteEquip(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token != null) {
            rService.deleteEquip(id, token);
        }
        
        return "redirect:/admin";
    }
    
    @PostMapping("/user/edit")
    public String editUser(@ModelAttribute UserDTO u, HttpSession session, Model model, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        
        if (token != null) {
        if (u.getSenha() != null && !u.getSenha().trim().isEmpty()) {
            if (!u.getSenha().equals(u.getConfirmSenha())) {
                redirect.addFlashAttribute("erro", "As senhas não estão iguais");
                redirect.addFlashAttribute("user", u);
                return "redirect:/admin"; 
            }
        }
//        rService.editUser(u, token);
        try {
                rService.editUser(u, token);
                redirect.addFlashAttribute("sucesso", "Usuário Atualizaado!");
            } catch (HttpStatusCodeException e) {
                redirect.addFlashAttribute("erro", mensagemErro(e));
            } catch (Exception e) {
                redirect.addFlashAttribute("erro", "Falha na atualização. Tente novamente.");
            }
    }

    return "redirect:/admin";
    }
    
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        
        if (token != null) {
            rService.deleteUser(id, token);
        }
        
        return "redirect:/admin";
    }
    
    // ------------------- EXTRACAO MENSAGEM JSON -------------------
    
    private String mensagemErro(HttpStatusCodeException m) {
        try {
            String json = m.getResponseBodyAsString();
            if (json != null && !json.trim().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(json);

                if (jsonNode.has("message") && !jsonNode.get("message").asText().isEmpty()) {
                    return jsonNode.get("message").asText();
                }
                if (jsonNode.has("reason") && !jsonNode.get("reason").asText().isEmpty()) {
                    return jsonNode.get("reason").asText();
                }
                if (jsonNode.has("detail") && !jsonNode.get("detail").asText().isEmpty()) {
                    return jsonNode.get("detail").asText();
                }
            }
        } catch (Exception e) {
            
        }
        return "Ocorreu um erro ao processar a requisição (" + m.getStatusCode().value() + ").";
    }
}
