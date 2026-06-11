package es.udc.ws.app.client.ui;

import java.util.List;

import es.udc.ws.app.client.service.ClientEncuestaService;
import es.udc.ws.app.client.service.ClientEncuestaServiceFactory;
import es.udc.ws.app.client.service.dto.ClientEncuestaDto;
import es.udc.ws.app.client.service.dto.ClientRespuestaEncuestaDto;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

public class AppServiceClient {

    public static void main(String[] args) {

        if (args.length == 0) {
            printUsageAndExit();
        }

        ClientEncuestaService clientEncuestaService = ClientEncuestaServiceFactory.getService();

        if ("-N".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[]{});

            try {
                Long encuestaId = clientEncuestaService.crearEncuesta(new ClientEncuestaDto(null,
                        args[1], args[2], false, null, 0, 0));

                System.out.println("Encuesta " + encuestaId + " creada correctamente");

            } catch (NumberFormatException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-K".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{});

            try {
                List<ClientEncuestaDto> encuestas = clientEncuestaService.buscarPorPalabraClave(args[1]);
                System.out.println("Encontrada(s) " + encuestas.size() +
                        " encuesta(s) con la palabra clave '" + args[1] + "'");
                for (ClientEncuestaDto encuestaDto : encuestas) {
                    System.out.println("Id: " + encuestaDto.getEncuestaId() +
                            ", Pregunta: " + encuestaDto.getPregunta() +
                            ", Fin: " + encuestaDto.getFechaHoraFin() +
                            ", Cancelada: " + (encuestaDto.isCancelada() ? "Yes" : "No") +
                            ", Positivas: " + encuestaDto.getRespuestasPositivas() +
                            ", Total: " + encuestaDto.getTotalRespuestas());
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        

        } else if ("-B".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{1});

            try {
                ClientEncuestaDto encuesta = clientEncuestaService.buscar(Long.valueOf(args[1]));

                System.out.println("Id: " + encuesta.getEncuestaId() +
                        ", Pregunta: " + encuesta.getPregunta() +
                        ", Fin: " + encuesta.getFechaHoraFin() +
                        ", Cancelada: " + (encuesta.isCancelada() ? "Sí" : "No") +
                        ", Positivas: " + encuesta.getRespuestasPositivas() +
                        ", Total: " + encuesta.getTotalRespuestas());

            } catch (NumberFormatException | InstanceNotFoundException ex) {
                ex.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-R".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, new int[]{1});

            try {
                boolean respuestaPositiva = "positiva".equalsIgnoreCase(args[3]);
                Long respuestaId = clientEncuestaService.responderEncuesta(Long.valueOf(args[1]),
                        args[2], respuestaPositiva);

                System.out.println("Respuesta " + respuestaId + " creada correctamente");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }


        } else if ("-C".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[]{1});

            try {
                clientEncuestaService.cancelarEncuesta(Long.valueOf(args[1]));
                System.out.println("Encuesta " + args[1] + " cancelada correctamente");

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        } else if ("-P".equalsIgnoreCase(args[0])) {
            validateArgs(args, 3, new int[]{1});

            try {
                boolean soloPositivas = "true".equalsIgnoreCase(args[2]);
                List<ClientRespuestaEncuestaDto> respuestas = clientEncuestaService.obtenerRespuestas(
                        Long.valueOf(args[1]), soloPositivas);
                
                String tipoRespuestas = soloPositivas ? "positivas" : "todas";
                System.out.println("Encontradas " + respuestas.size() +
                        " respuesta(s) " + tipoRespuestas + " para la encuesta " + args[1]);
                
                for (ClientRespuestaEncuestaDto respuestaDto : respuestas) {
                    System.out.println("Id: " + respuestaDto.getRespuestaId() +
                            ", Email: " + respuestaDto.getEmail() +
                            ", Respuesta: " + (respuestaDto.isRespuestaPositiva() ? "POSITIVA" : "NEGATIVA"));
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }

        }
    }

    public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
        if (expectedArgs != args.length) {
            printUsageAndExit();
        }
        for (int position : numericArguments) {
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [N]    AppServiceClient -N <pregunta> <endDateTime>\n" +
                "    [K]    AppServiceClient -K <keyword>\n" +
                "    [B]    AppServiceClient -B <encuestaId>\n" +
                "    [R]    AppServiceClient -R <encuestaId> <email> <positiva|negativa>\n" +
                "    [C]    AppServiceClient -C <encuestaId>\n" +
                "    [P]    AppServiceClient -P <encuestaId> <true|false>\n");
    }
}