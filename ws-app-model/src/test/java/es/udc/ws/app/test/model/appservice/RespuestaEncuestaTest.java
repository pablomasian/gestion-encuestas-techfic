package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.respuesta.RespuestaEncuesta;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RespuestaEncuestaTest {

    @Test
    void testConstructores() {
        LocalDateTime fecha = LocalDateTime.now();
        
        RespuestaEncuesta r1 = new RespuestaEncuesta(1L, "test@mail.com", true, fecha);
        assertEquals(1L, r1.getEncuestaId());
        assertEquals("test@mail.com", r1.getEmail());
        assertTrue(r1.isRespuestaPositiva());
        
        RespuestaEncuesta r2 = new RespuestaEncuesta(2L, "user@mail.com", false);
        assertNull(r2.getFechaRespuesta());
        assertFalse(r2.isRespuestaPositiva());
    }

    @Test
    void testSettersYGetters() {
        RespuestaEncuesta r = new RespuestaEncuesta(1L, "inicial@mail.com", true);
        r.setRespuestaId(50L);
        r.setEmail("nuevo@mail.com");
        r.setRespuestaPositiva(false);
        
        assertEquals(50L, r.getRespuestaId());
        assertEquals("nuevo@mail.com", r.getEmail());
        assertFalse(r.isRespuestaPositiva());
    }

    @Test
    void testEquals() {
        LocalDateTime fecha = LocalDateTime.now();
        RespuestaEncuesta r1 = new RespuestaEncuesta(1L, "test@mail.com", true, fecha);
        r1.setRespuestaId(10L);
        
        RespuestaEncuesta r2 = new RespuestaEncuesta(1L, "test@mail.com", true, fecha);
        r2.setRespuestaId(10L);
        
        RespuestaEncuesta r3 = new RespuestaEncuesta(2L, "test@mail.com", true, fecha);
        r3.setRespuestaId(10L);
        
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1, r1);
        assertNotEquals(null, r1);
    }


    @Test
    void testHashCode() {
        LocalDateTime fecha = LocalDateTime.now();
        RespuestaEncuesta r1 = new RespuestaEncuesta(1L, "test@mail.com", true, fecha);
        RespuestaEncuesta r2 = new RespuestaEncuesta(1L, "test@mail.com", true, fecha);
        
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        RespuestaEncuesta r = new RespuestaEncuesta(1L, "test@mail.com", true, LocalDateTime.now());
        r.setRespuestaId(10L);
        String str = r.toString();
        
        assertTrue(str.contains("encuestaId=1"));
        assertTrue(str.contains("respuestaId=10"));
        assertTrue(str.contains("email='test@mail.com'"));
        assertTrue(str.contains("respuestaPositiva=true"));
    }
}
