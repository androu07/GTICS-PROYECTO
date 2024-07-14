package com.example.webapp.dao;

import com.example.webapp.entity.Data;
import com.example.webapp.entity.DataDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DataDao {
    public Data buscarPorDni(String dni){

        Data data = null;

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.verifica.id/v2/consulta/personas?dni=" + dni;

        HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "Bearer sk-hAPIhq2FiEMLje8UlVnzDxXKRW5w2yvE/nXOBNVpOxA==");

      //  headers.set("Authorization", "Bearer sk-GA97hfXDzKec06mbgmWbayMo2ySoC8l/blLBwwXzgKw==");


        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<DataDto> forEntity = restTemplate.exchange(url, HttpMethod.POST, entity, DataDto.class);

        if(forEntity.getStatusCode().is2xxSuccessful()){
            DataDto dataDto = forEntity.getBody();
            data = dataDto.getData();
        }

        return data;
    }
}
