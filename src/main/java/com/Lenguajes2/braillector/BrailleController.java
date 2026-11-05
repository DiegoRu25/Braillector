package com.Lenguajes2.braillector;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.util.Map;

@RestController
@RequestMapping("/api/braille")
public class BrailleController {

    @PostMapping("/traducir")
    public ResponseEntity<Map<String,String>> traducir(@RequestBody Map<String, String> request){
        String texto = request.get("texto");
        if(texto == null || texto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","Texto vacio"));
        }
        try {
            BrailleParser parser = new BrailleParser(new StringReader(texto));
            String braille = parser.traducir();
            return ResponseEntity.ok(Map.of("braille", braille));
        }catch (ParseException e){
            return ResponseEntity.badRequest().body(Map.of("error", "Error de sintaxis" + e.getMessage()));
        }
    }

}
