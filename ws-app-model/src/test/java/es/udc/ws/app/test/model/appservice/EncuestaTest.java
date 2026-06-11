package es.udc.ws.app.test.model.appservice;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import es.udc.ws.app.model.encuesta.Encuesta;

class EncuestaTest {

    @Test
    void testConstructores() {
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(7);
        LocalDateTime fechaCreacion = LocalDateTime.now().withNano(0);
        
        // Constructor completo
        Encuesta e1 = new Encuesta(1L, "¿Pregunta?", fechaFin, false, fechaCreacion, 5, 3);
        assertEquals(1L, e1.getEncuestaId());
        assertEquals("¿Pregunta?", e1.getPregunta());
        assertEquals(5, e1.getRespuestasPositivas());
        
        // Constructor para alta
        Encuesta e2 = new Encuesta("¿Nueva?", fechaFin);
        assertNull(e2.getEncuestaId());
        assertEquals(0, e2.getRespuestasPositivas());
    }

    @Test
    void testSettersYGetters() {
        Encuesta e = new Encuesta("Test", LocalDateTime.now().plusDays(1));
        e.setEncuestaId(100L);
        e.setPregunta("Nueva");
        e.setCancelada(true);
        e.setRespuestasPositivas(10);
        e.setRespuestasNegativas(5);
        
        assertEquals(100L, e.getEncuestaId());
        assertEquals("Nueva", e.getPregunta());
        assertTrue(e.isCancelada());
        assertEquals(10, e.getRespuestasPositivas());
        assertEquals(5, e.getRespuestasNegativas());
    }

    @Test
    void testIsAbierta() {
        Encuesta abierta = new Encuesta("Test", LocalDateTime.now().plusHours(5));
        assertTrue(abierta.isAbierta());
        
        abierta.setCancelada(true);
        assertFalse(abierta.isAbierta());
        
        Encuesta finalizada = new Encuesta("Test", LocalDateTime.now().minusHours(1));
        assertFalse(finalizada.isAbierta());
    }

    @Test
    void testEquals() {
        LocalDateTime fecha = LocalDateTime.now().withNano(0);
        Encuesta e1 = new Encuesta(1L, "Test", fecha, false, fecha, 5, 3);
        Encuesta e2 = new Encuesta(1L, "Test", fecha, false, fecha, 5, 3);
        Encuesta e3 = new Encuesta(2L, "Test", fecha, false, fecha, 5, 3);
        
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(null, e1);
        assertEquals(e1, e1);
    }

    @Test
    void testHashCode() {
        LocalDateTime fecha = LocalDateTime.now().withNano(0);
        Encuesta e1 = new Encuesta(1L, "Test", fecha, false, fecha, 5, 3);
        Encuesta e2 = new Encuesta(1L, "Test", fecha, false, fecha, 5, 3);
        
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testToString() {
        Encuesta e = new Encuesta(1L, "¿Test?", LocalDateTime.now(), false, LocalDateTime.now(), 5, 3);
        String str = e.toString();
        
        assertTrue(str.contains("encuestaId=1"));
        assertTrue(str.contains("pregunta='¿Test?'"));
        assertTrue(str.contains("respuestasPositivas=5"));
    }

}