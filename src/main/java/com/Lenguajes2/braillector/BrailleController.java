package com.Lenguajes2.braillector;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/braille")
public class BrailleController {

    @PostMapping("/traducir")
    public ResponseEntity<Map<String,String>> traducir(@RequestBody Map<String, String> request){
        String texto = request.get("texto");
        if(texto == null || texto.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error","Texto vacio"));
        }
        Set<Character> simbolosInvalidos = new HashSet<>();
        for (char c : texto.toCharArray()) {
            // Si el carácter no es letra, número, espacio o signo permitido
            if (!esCaracterValido(c)) {
                simbolosInvalidos.add(c);
            }
        }
        if (!simbolosInvalidos.isEmpty()) {
            StringBuilder mensaje = new StringBuilder("Símbolos no válidos en Braille: ");
            for (char c : simbolosInvalidos) {
                mensaje.append("'").append(c).append("' ");
            }
            mensaje.append("\n\nBraille solo acepta los siguientes símbolos: + - / = . , ; : ¿ ? ¡ ! ( )");
            return ResponseEntity.badRequest().body(Map.of("error", mensaje.toString().trim()));
        }

        try {
            BrailleParser parser = new BrailleParser(new StringReader(texto));
            String braille = parser.traducir();
            return ResponseEntity.ok(Map.of("braille", braille));

        } catch (ParseException e){
            return ResponseEntity.badRequest().body(Map.of("error", "Error de sintaxis" + e.getMessage()));
        } catch (TokenMgrError e){
            return ResponseEntity.badRequest().body(Map.of("error", "Símbolo no válido detectado."));
        }
    }
    // Función que define qué caracteres sí están permitidos
    private boolean esCaracterValido(char c) {
        if (Character.isLetter(c)) return true;
        if (Character.isDigit(c)) return true;
        if (Character.isWhitespace(c)) return true;
        // Signos de puntuación permitidos en tu .jj
        String signosPermitidos = "+-/=.,;:¿?¡!()\"";
        return signosPermitidos.indexOf(c) != -1;
    }

}
