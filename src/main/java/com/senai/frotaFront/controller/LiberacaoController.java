/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.controller;

import com.senai.frotaFront.model.LiberacaoDTO;
import com.senai.frotaFront.model.UserDTO;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String liberacoes(HttpSession session, Model model, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");

        // Validacao token e admin
        if (token == null || !"administrador".equalsIgnoreCase(role)) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
            return "redirect:/login";
        }

        model.addAttribute("alerta", restService.listAlerta(token));
        model.addAttribute("open", restService.listOpen(token));
        model.addAttribute("operadores", restService.listUsers(token));
        model.addAttribute("equipamentos", restService.listEquip(token));
        
        model.addAttribute("liberacao", new LiberacaoDTO());

        return "liberacoes";
    }

    @GetMapping("/historico")
    public String historico(HttpSession session, Model model, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao token e admin
        if (token == null || !"administrador".equalsIgnoreCase(role)) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
            return "redirect:/login";
        }

        List<LiberacaoDTO> closeList = restService.listClose(token);
        model.addAttribute("close", closeList);

        return "historico";
    }

    @PostMapping("/admin/liberacao/save")
    public String addLiberacao(@ModelAttribute LiberacaoDTO liberacao, HttpSession session, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role"); 
        
        if (token == null || !"administrador".equalsIgnoreCase(role)) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
            return "redirect:/login";
        }
        
        try {
            restService.addLiberacao(liberacao, token);
            redirect.addFlashAttribute("sucesso", "Liberação agendada");
        } catch (HttpClientErrorException e) {
            // Captura ResponseStatusException (erros) do Service(back)
            redirect.addFlashAttribute("erro", e.getResponseBodyAsString());
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Falha no agendamento. Tente novamente.");
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
    public String resolve(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        String role = (String) session.getAttribute("role");
        
        // Validacao token e admin
        if (token != null && "administrador".equalsIgnoreCase(role)) {
            
            try {
                restService.resolve(id, token);
                redirectAttributes.addFlashAttribute("sucesso", "Ocorrência concluída com sucesso!");

            } catch (HttpClientErrorException.BadRequest e) {
                // Captura ResponseStatusException (erros) do Service(back)
                redirectAttributes.addFlashAttribute("erro", "Equipamento não pode ser liberado. Horímetro muito alto");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", "Falha no processo. Tente novamente");
            }
        }

        return "redirect:/liberacoes";
    }

    // ----------------------- OPERADOR -----------------------

    @GetMapping("/op")
    public String opLiberacoes(HttpSession session, Model model, RedirectAttributes redirect) {
        String token = (String) session.getAttribute("token");
        
        //Valid Token
        if (token == null) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
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
    public String pickUp(@ModelAttribute LiberacaoDTO liberacao, HttpSession session, RedirectAttributes redirect) {
        // RedirectAttributes permite addFlashAttribute(), carregando informacoes (mensagens de erro)
        // atraves do redirect:/op para serem mostradas ao renderizar a tela novamente
        String token = (String) session.getAttribute("token");

        if (token == null) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
            return "redirect:/login";
        }

        try {
            restService.pickUp(liberacao, token);
            redirect.addFlashAttribute("sucesso", "Equipamento retirado");
        } catch (HttpClientErrorException e) {
            // Captura ResponseStatusException (erros) do Service(back)
            redirect.addFlashAttribute("erro", e.getResponseBodyAsString());
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Tente novamente.");
        }

        return "redirect:/op";
    }

    @PostMapping("/op/liberacao/close")
    public String close(@ModelAttribute LiberacaoDTO liberacao, HttpSession session, RedirectAttributes redirect) {
        // RedirectAttributes permite addFlashAttribute(), carregando informacoes (mensagens de erro)
        // atraves do redirect:/op para serem mostradas ao renderizar a tela novamente
        String token = (String) session.getAttribute("token");

        if (token == null) {
            redirect.addFlashAttribute("erro", "Erro na sessão. Faça login novamente.");
            return "redirect:/login";
        }

        try {
            restService.close(liberacao, token);
            redirect.addFlashAttribute("sucesso", "Devolução realizada com sucesso!");
        } catch (HttpClientErrorException e) {
            // Captura ResponseStatusException (erros) do Service(back)
            redirect.addFlashAttribute("erro", e.getResponseBodyAsString());
        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Falha na devolução. Tente novamente.");
        }

        return "redirect:/op";
    }
}
