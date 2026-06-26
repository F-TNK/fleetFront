/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.senai.frotaFront.service;

import com.senai.frotaFront.model.AuthDTO;
import com.senai.frotaFront.model.EquipDTO;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Micro
 */
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
                .baseUrl("http://localhost:8081/api")
                .build();
    }

    /**
     * Envia as credenciais do usuário para o endpoint de login.
     *
     * @param user objeto DTO contendo email e senha
     * @return token ou resposta de autenticação como String
     */
    public String logar(AuthDTO user) {
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
                .body(String.class);
    }

    public String registrar(AuthDTO user) {
        if (!user.getSenha().equals(user.getConfirmSenha())){
            return "As senhas não estão iguais";
        }
        
        user.setCargo("operador");
        user.setConfirmSenha(null);;
//        String retorno = 
                restClient
                .post()
                .uri("/autenticar/register")
                .body(user)
                .retrieve()
                .body(String.class);
                
        return null;
    }
    
    /**
     * Lista os editais do backend usando o token JWT no cabeçalho Authorization.
     *
     * @param token token de autenticação recebido após o login
     * @return lista de editais retornada pela API
     */
    public List<EquipDTO> listaEquip(String token) {
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
}
