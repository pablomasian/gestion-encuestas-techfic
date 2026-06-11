# Plataforma de Gestión de Encuestas - TechFic

## Descripción del Proyecto
El objetivo de la práctica es desarrollar una aplicación que dé soporte a una plataforma simplificada de encuestas para los empleados de una empresa ficticia llamada TechFic. 


## Arquitectura y Tecnologías
La aplicación sigue una arquitectura estructurada en las siguientes capas:
*   **Capa Interfaz de Usuario:** Cliente de línea de comandos para invocar las operaciones.
*   **Capa Acceso a Servicios:** Implementada utilizando REST.
*   **Capa Servicios:** Expone la funcionalidad mediante un servicio web REST que trabaja con datos en formato JSON.
*   **Capa Lógica de Negocio:** Gestiona las reglas de las encuestas y respuestas.
*   **Capa Acceso a Datos:** Utiliza una Base de Datos relacional para persistir la información.

*(Nota: Opcionalmente, la arquitectura contempla el uso de Apache Thrift para implementar la capa de Servicios y Acceso a Servicios mediante un parámetro de configuración).*

## Funcionalidades Principales
El sistema permite realizar las siguientes operaciones:
*   Crear encuestas definiendo una pregunta y una fecha/hora de finalización futura.
*   Buscar encuestas mediante un identificador único.
*   Buscar encuestas especificando una palabra clave contenida en la pregunta.
*   Responder a encuestas activas utilizando un email de empleado, limitando las opciones a una respuesta positiva o negativa.
*   Actualizar la respuesta de un empleado si este vuelve a participar en la misma encuesta.
*   Cancelar encuestas antes de su fecha de finalización, bloqueando la recepción de nuevas respuestas.
*   Recuperar todas las respuestas asociadas a una encuesta específica.
