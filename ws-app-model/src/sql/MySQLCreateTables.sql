-- ----------------------------------------------------------------------------   
-- Modelo: esquema de encuestas TechFic (primero se borran hijas, luego padres; crear tablas e índices)
-------------------------------------------------------------------------------    
-- Borrados (por orden de dependencias)   
DROP TABLE IF EXISTS RespuestaEncuesta;
DROP TABLE IF EXISTS Encuesta;

-- ------------------ Tabla de encuestas ---------------------------------------    
CREATE TABLE Encuesta (
    encuestaId     BIGINT AUTO_INCREMENT PRIMARY KEY,
    pregunta       VARCHAR(500) NOT NULL,
    fechaHoraFin   DATETIME NOT NULL,
    cancelada      BOOLEAN NOT NULL DEFAULT FALSE,
    fechaCreacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    respuestasPositivas INT NOT NULL DEFAULT 0,
    respuestasNegativas INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;


-- ------------------ Tabla de respuestas a encuestas ---------------------------  
CREATE TABLE RespuestaEncuesta (
    respuestaId     BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    encuestaId      BIGINT NOT NULL,
    email           VARCHAR(100) NOT NULL,
    respuesta       ENUM('POSITIVA','NEGATIVA') NOT NULL,
    fechaRespuesta  DATETIME NOT NULL,

    UNIQUE KEY uq_encuesta_email (encuestaId, email),
    CONSTRAINT fk_respuesta_encuesta FOREIGN KEY (encuestaId)
        REFERENCES Encuesta(encuestaId)
        ON DELETE CASCADE
) ENGINE=InnoDB;
