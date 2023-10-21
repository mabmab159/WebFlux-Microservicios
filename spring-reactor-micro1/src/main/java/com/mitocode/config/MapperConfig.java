package com.mitocode.config;

import com.mitocode.dto.ClientDTO;
import com.mitocode.model.Client;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class MapperConfig {

    @Bean("defaultMapper")
    public ModelMapper defaultMapper(){
        return new ModelMapper();
    }

    @Bean("clientMapper")
    public ModelMapper clientMapper(){
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //Escritura
        TypeMap<ClientDTO, Client> typeMap1 = mapper.createTypeMap(ClientDTO.class, Client.class);
        typeMap1.addMapping(ClientDTO::getNameClient, (dest, v)-> dest.setFirstName((String) v));
        typeMap1.addMapping(ClientDTO::getSurnameClient, (dest, v)-> dest.setLastName((String) v));
        typeMap1.addMapping(ClientDTO::getUrlPhotoClient, (dest, v)-> dest.setUrlPhoto((String) v));

        //Lectura
        TypeMap<Client, ClientDTO> typeMap2 = mapper.createTypeMap(Client.class, ClientDTO.class);
        typeMap2.addMapping(Client::getFirstName, (dest, v)-> dest.setNameClient((String) v));
        typeMap2.addMapping(Client::getLastName, (dest, v)-> dest.setSurnameClient((String) v));
        typeMap2.addMapping(Client::getBirthDate, (dest, v)-> dest.setBirthDateClient((LocalDate) v));
        typeMap2.addMapping(Client::getUrlPhoto, (dest, v)-> dest.setUrlPhotoClient((String) v));

        return mapper;

    }
}
