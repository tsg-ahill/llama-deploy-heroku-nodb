package com.sguild.llamadeploy.nodb;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.collections.ListChangeListener.Change;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ahill
 */
//@CrossOrigin // Only enable if you want to use this to prototype local demos
@Controller
@EnableAutoConfiguration
public class WebserviceControllerProto {

    private Map<String, Llama> llamas;

    public WebserviceControllerProto() {
        llamas = new TreeMap<>();
        resetHerd();
    }

    @RequestMapping(value = "/llamas", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Llama> getAllLlamas() {
        return llamas.values();
    }

    @RequestMapping(value = "age/{min_age}/{max_age}", method = RequestMethod.GET)
    @ResponseBody
    public List<Llama> purchaseItem(@PathVariable("min_age") int minAge, @PathVariable("max_age") int maxAge) throws NoSuchLlamaException, BadUserInputException {
        
        if (minAge > maxAge ) {
            throw new BadUserInputException("Yeah, that's not gonna work.");
        }
        
        List<Llama> llamasInRange =
        llamas.values().stream()
                .filter(llama -> llama.getAge() < minAge)
                .filter(llama -> llama.getAge() > maxAge)
                .collect(Collectors.toList());

        if(llamasInRange.isEmpty()){
            throw new NoSuchLlamaException("We don't have any of those kind of llamas, Dave.");
        }
        
        return llamasInRange;
    }

    
    @ResponseBody @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler({NoSuchLlamaException.class, BadUserInputException.class})
    public Map<String, String> processException(Exception e){
        Map<String, String> error = new HashMap<>();
        error.put("message", e.getMessage());
        return error;
    }
    
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void reset() {
        resetHerd();
    }
    
    private void resetHerd(){
        llamas.clear();
        llamas.put("BeeBop", 
                new Llama(0, 12, "BeeBop", "Fluffy"));
        llamas.put("Floyd", 
                new Llama(1, 4, "Floyd", "Crop Top"));
        llamas.put("Boo", 
                new Llama(2, 2, "Boo", "Brown Fuzz"));
        
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebserviceControllerProto.class, args);
    }
}
