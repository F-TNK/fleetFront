/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.service;

import com.senai.frotaFront.model.AuthDTO;
import com.senai.frotaFront.model.EquipDTO;
import com.senai.frotaFront.model.LiberacaoDTO;
import com.senai.frotaFront.model.LoginResponseDTO;
import com.senai.frotaFront.model.UserDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Micro
 */
@Service
public class ApiService {

    // RestClient é o cliente HTTP moderno do Spring que permite
    // construir requisições de forma fluente e declarativa.
    private final RestClient restClient;

    /**
     * Construtor padrão do serviço.
     * <p>
     * Aqui criamos o RestClient apenas uma vez e configuramos a URL base
     * comum para todas as requisições deste serviço.
     */
    public ApiService() {
        this.restClient = RestClient.builder()
                // Define a base URL que será usada em todas as requisições.
                // Depois, cada chamada só precisa informar o caminho relativo.
                .baseUrl("http://localhost:8080/api")
                .build();
    }

    
    // ----------------------- USER -----------------------

    
    /**
     * Envia as credenciais do usuário para o endpoint de login.
     *
     * @param user objeto DTO contendo email e senha
     * @return token ou resposta de autenticação como String
     */
    public LoginResponseDTO logar(AuthDTO user) {
        // Inicia a construção de uma requisição POST.
        return restClient.post()
                // Define o caminho relativo ao endpoint de autenticação.
                // A URL final será "http://localhost:3333/api/auth/logar".
                .uri("/autenticar/login")
                // Define o corpo da requisição como o DTO de login.
                // O Spring converte automaticamente este objeto para JSON.
                .body(user)
                // Dispara a requisição e obtém a resposta do servidor.
                .retrieve()
                // Lê o corpo da resposta e converte para String.
                // Use outro DTO aqui se a API retornar um objeto JSON complexo.
                .body(LoginResponseDTO.class);
    }

    public String register(UserDTO user) {
        
        user.setCargo("operador");
        user.setConfirmSenha(null);
//        String retorno = 
        return
                restClient
                .post()
                .uri("/autenticar/register")
                .body(user)
                .retrieve()
                .body(String.class);
    }
    
    public List<UserDTO> listUsers(String token) {
        UserDTO[] users = restClient.get()
                .uri("/admin/user")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UserDTO[].class);
        return Arrays.asList(users);
    }
    
    public String editUser(UserDTO u, String token) {
        return restClient.put()
                .uri("/admin/user")
                .header("Authorization", "Bearer " + token)
                .body(u)
                .retrieve()
                .body(String.class);
    }
    
    public String deleteUser(Long id, String token) {
        return restClient.delete()
                .uri("/admin/user/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
    
    public List<UserDTO> listOp(String token) {
        UserDTO[] op = restClient.get()
                .uri("/admin/user/op")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UserDTO[].class);
        return Arrays.asList(op);
    }
    
    // ----------------------- OPERADOR -----------------------
    
    public UserDTO findUserById(Long id, String token) {
        return restClient.get()
                .uri("/op/liberacao/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(UserDTO.class);
    }
    
    public String editProfile(UserDTO u, String token) {
        return restClient.put()
                .uri("/op/liberacao/edit")
                .header("Authorization", "Bearer " + token)
                .body(u)
                .retrieve()
                .body(String.class);
    }
    
    
    // ----------------------- EQUIP -----------------------
    
    
    /**
     * Lista os editais do backend usando o token JWT no cabeçalho Authorization.
     *
     * @param token token de autenticação recebido após o login
     * @return lista de editais retornada pela API
     */
    public List<EquipDTO> listEquip(String token) {
        // Faz uma requisição GET para o endpoint de editais.
        EquipDTO[] equip = restClient.get()
                .uri("/equip")
                // Adiciona o header Authorization com o token Bearer.
                .header("Authorization", "Bearer " + token)
                .retrieve()
                // Converte o corpo JSON para um array de EditalDTO.
                .body(EquipDTO[].class);

        // Converte o array para List para uso mais conveniente na aplicação.
        return Arrays.asList(equip);
    }
    
    public String addEquip(EquipDTO equip, String token) {
        return restClient.post()
                .uri("/equip")
                .header("Authorization", "Bearer " + token)
                .body(equip)
                .retrieve()
                .body(String.class);
    }
    
    public String editEquip(EquipDTO equip, String token) {
        return restClient.put()
                .uri("/equip")
                .header("Authorization", "Bearer " + token)
                .body(equip)
                .retrieve()
                .body(String.class);
    }
    
    public EquipDTO findById(Long id, String token) {
        return restClient.get()
                .uri("/equip/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(EquipDTO.class);
    }
    
    public String deleteEquip(Long id, String token) {
        return restClient.delete()
                .uri("/equip/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
    
    
    
    // ----------------------- LIBERACAO -----------------------

    public String addLiberacao(LiberacaoDTO l, String token) {
        return restClient.post()
                .uri("/admin/liberacao")
                .header("Authorization", "Bearer " + token)
                .body(l)
                .retrieve()
                .body(String.class);
    }

    public String editLiberacao(LiberacaoDTO l, String token) {
        return restClient.put()
                .uri("/admin/liberacao")
                .header("Authorization", "Bearer " + token)
                .body(l)
                .retrieve()
                .body(String.class);
    }

    public String deleteLiberacao(Long id, String token) {
        return restClient.delete()
                .uri("/admin/liberacao/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    public List<LiberacaoDTO> listAlerta(String token) {
        LiberacaoDTO[] liberacoes = restClient.get()
                .uri("/admin/liberacao/alerta")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LiberacaoDTO[].class);
        return Arrays.asList(liberacoes);
    }

    public List<LiberacaoDTO> listOpen(String token) {
        LiberacaoDTO[] liberacoes = restClient.get()
                .uri("/admin/liberacao/open")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LiberacaoDTO[].class);
        return Arrays.asList(liberacoes);
    }

    public List<LiberacaoDTO> listClose(String token) {
        LiberacaoDTO[] liberacoes = restClient.get()
                .uri("/admin/liberacao/close")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LiberacaoDTO[].class);
        return Arrays.asList(liberacoes);
    }

    public String resolve(Long id, String token) {
        return restClient.put()
                .uri("/admin/liberacao/resolve/" + id)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    // ----------------------- LIBERAÇÃO (OPERADOR) -----------------------

    public List<LiberacaoDTO> listById(Long idUser, String token) {
        LiberacaoDTO[] liberacoes = restClient.get()
                .uri("/op/liberacao/user/" + idUser)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(LiberacaoDTO[].class);
        return Arrays.asList(liberacoes);
    }

    public String pickUp(LiberacaoDTO l, String token) {
        return restClient.put()
                .uri("/op/liberacao/pickup")
                .header("Authorization", "Bearer " + token)
                .body(l)
                .retrieve()
                .body(String.class);
    }

    public String close(LiberacaoDTO l, String token) {
        return restClient.put()
                .uri("/op/liberacao/close")
                .header("Authorization", "Bearer " + token)
                .body(l)
                .retrieve()
                .body(String.class);
    }
    
}
