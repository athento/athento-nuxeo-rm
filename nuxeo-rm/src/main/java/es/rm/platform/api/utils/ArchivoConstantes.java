package es.rm.platform.api.utils;

/**
 * Esta clase agrupa todas las constantes usadas por el RM.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
public class ArchivoConstantes {

    /**
     * Constante para cerrar carpeta de documentos.
     */
    public static final String CERRAR_CARPETA_DE_DOCUMENTOS = "cerrar";

    /**
     * Constante para retener carpeta de documentos.
     */
    public static final String RETENER_CARPETA_DE_DOCUMENTOS = "retener";

    /**
     * Constante para reabrir carpeta de documentos.
     */
    public static final String REABRIR_CARPETA_DE_DOCUMENTOS = "reabrir";

    /**
     * Constante para tipo de carpeta de documentos.
     */
    public static final String TIPO_CARPETA_DE_DOCUMENTOS = "CAdD";

    /**
     * Constante para funcion marco.
     */
    public static final String TIPO_FUNCION_MARCO = "FuncionMarco";

    /**
     * Constante para funcion.
     */
    public static final String TIPO_FUNCION = "Funcion";

    /**
     * Constante para actividad.
     */
    public static final String TIPO_ACTIVIDAD = "Actividad";

    /**
     * Constante para accion.
     */
    public static final String TIPO_ACCION = "Accion";

    /**
     * Constante para expediente.
     */
    public static final String TIPO_EXPEDIENTE = "Expediente";
    
    /**
     * Constante para ExpedienteREA.
     */
    public static final String TIPO_EXPEDIENTE_REA = "ExpedienteREA";
    
    /**
     * Constante para ExpedienteRELE.
     */
    public static final String TIPO_EXPEDIENTE_RELE = "ExpedienteRELE";

    /**
     * Constante para tipo calendario root.
     */
    public static final String TIPO_CALENDARIO_ROOT = "CalendarioDCRoot";

    /**
     * Constante para tipo root.
     */
    public static final String TIPO_ROOT = "Root";

    /**
     * Constante para estado cerrado carpeta de documentos.
     */
    public static final String ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS = "cerrada";
    
    /**
     * Constante para estado cerrado desbloqueada carpeta de documentos.
     */
    public static final String ESTADO_CERRADO_DESBLOQUEADA_CARPETA_DE_DOCUMENTOS = "cerrada desbloqueada";

    /**
     * Constante para estado retenido carpeta de documentos.
     */
    public static final String ESTADO_RETENIDO_CARPETA_DE_DOCUMENTOS = "retenida";

    /**
     * Constante para estado abierto carpeta de documentos.
     */
    public static final String ESTADO_ABIERTO_CARPETA_DE_DOCUMENTOS = "abierta";

    /**
     * Constante para fase archivo.
     */
    public static final String FASE_ARCHIVO = "archivo";

    /**
     * Constante para fase vigencia.
     */
    public static final String FASE_VIGENCIA = "vigencia";
    
    /**
     * Constante para fase Tramitacion.
     */
    public static final String FASE_TRAMITACION = "tramitacion";
    
    /**
     * Constante para fase Creacion.
     */
    public static final String FASE_CREACION = "creacion";

    /**
     * Constante para faceta elemento cuadro de clasificacion.
     */
    public static final String FACETA_ELEMENTO_CUADRO_CLASIFICACION = "elemento_cdc";
    
    /**
     * Constante con el nombre del esquema de CADD.
     */
    public static final String ESQUEMA_CADD = "cadd";
    
    /**
     * Constante con el nombre de un campo estado.
     */
    public static final String CAMPO_ESTADO_EXPEDIENTE_CADD = "estado";

    /**
     * Constante para saber la version de la referencia del documento RM.
     */
    public static final String CAMPO_VERSION_REFERENCIA = "version";

    /**
     * Constante para restaurar documento electronico.
     */
    public static final String RESTAURAR_DOCUMENTO_ELECTRONICO = "restaurar";

    /**
     * Constante para estado restaurado.
     */
    public static final String ESTADO_RESTAURADO = "restaurado";
    
    /**
     * Propiedad que indica el path hacia la ra&iacute;z CdC.
     */
    public static final String PROPIEDAD_CDC_PATH = "org.yerbabuena.rm.path";
    
    /**
     * Constante para el estado Vigencia (Fase).
     */
    public static final String FASE_TRANSFERENCIA_ELIMINACION = "transferencia";
    
    /**
     * Constante para la transici&oacute;n a fase de vigencia.
     */
    public static final String TRANSICION_FASE_VIGENCIA = "aVigencia";
    
    
    /**
     * Constante para la transici&oacute;n a fase de Creacion..
     */
    public static final String TRANSICION_FASE_CREACION = "aCreacion";
    
    /**
     * Constante para la transici&oacute;n a fase de Tramitaci&oacute;n.
     */
    public static final String TRANSICION_FASE_TRAMITACION = "aTramitacion";
    
    /**
     * Constante para la transici&oacute;n a fase de Archivo.
     */
    public static final String TRANSICION_FASE_ARCHIVO = "aArchivo";
    
    /**
     * Constante para definir el tipo DocSimple.
     */
    public static final String TIPO_DOCUMENTO_SIMPLE = "DocSimple";
    
    /**
     * Constante para definir el esquema de los calendarios.
     */
    public static final String ESQUEMA_CALENDARIO_CONSERVACION = "calendario_conservacion";
    
    /**
     * Constante para definir el campo del calendario 'objetosAsociados'.
     */
    public static final String CAMPO_CALCON_OBJETOS_ASOCIADOS = "objetosAsociados";
    
    /**
     * Constante para definir el campo 'fases' del calendario de conservaci&oacute;n.
     */
    public static final String CAMPO_CALCON_FASES = "fase";
    
    /**
     * Constante que define el campo 'nombre_fase' para la estructura de datos que almacena una
     * fase del calendario de conservaci&oacute;n.
     */
    public static final String CAMPO_CALCON_FASE_NOMBRE_FASE = "nombre_fase";
    
    /**
     * Constante que define el campo 'periodo' para la estructura de datos que almacena una
     * fase del calendario de conservaci&oacute;n.
     */
    public static final String CAMPO_CALCON_FASE_PERIODO = "periodo";
    
    /**
     * Constante que define el dato relativo al a&ntilde;o que forma parte del periodo de vigencia 
     * dentro del esquema de calendarios de conservacion.
     */
    public static final String CAMPO_CALCON_FASE_PERIODO_ANOS = "anos";
    
    /**
     * Constante que define el dato relativo a meses que forma parte del periodo de vigencia 
     * dentro del esquema de calendarios de conservacion.
     */
    public static final String CAMPO_CALCON_FASE_PERIODO_MESES = "meses";
    
    /**
     * Constante que define el dato relativo a los dias que forma parte del periodo de vigencia 
     * dentro del esquema de calendarios de conservacion.
     */
    public static final String CAMPO_CALCON_FASE_PERIODO_DIAS = "dias";
    
    /**
     * Constante para definir el evento lanzado al finalizar el periodo de retencion.
     */
    public static final String EVENTO_FIN_PERIODO_RETENCION = "finPeriodoRetencion";
    
    /**
     * Constante para definir el prefijo de nombre que llevar&aacute;n todos los triggers registrados.
     */
    public static final String PREFIJO_TIRGGERS = "triggerRetencion";
    
    /**
     * Constante para definir el nombre por defecto que llevar&aacute;n los grupos de triggers del RM.
     */
    public static final String GRUPO_TRIGGERS_RM = "rm";
    
    /**
     * Constante que define el repositorio por defecto.
     */
    public static final String REPOSITORIO_POR_DEFECTO = "default";
    
    /**
     * Constante que define el vocabulario donde almacenar valores de configuraci&oacute;n por defecto.
     */
    public static final String VOCABULARIO_CONFIGURACION_RM = "rmConfiguracion";
    
    /**
     * Constante que define el campo del vocabulario donde est&aacute; definido el valor para la hora de ejeci&oacute;n
     * de los schedulers.
     */
    public static final String HORA_EJECUCION_SCHEDULER_VOCABULARIO = "schedulerHora";
    
    /**
     * Constante que define el campo del vocabulario donde est&aacute; definido el valor para el minuto de 
     * ejeci&oacute;n de los schedulers.
     */
    public static final String MINUTO_EJECUCION_SCHEDULER_VOCABULARIO = "schedulerMinuto";
    
    /**
     * Constante que representa la instrucci&oacute;n SELECT.
     */
    public static final String INSTRUCCION_SELECT = "SELECT";
    
    /**
     * Constante que representa la directiva SQL FROM.
     */
    public static final String DIRECTIVA_FROM = "FROM";
    
    /**
     * Constante que representa la directiva SQL WHERE.
     */
    public static final String DIRECTIVA_WHERE = "WHERE";
    
    /**
     * Constante que representa la directiva SQL LIKE.
     */
    public static final String DIRECTIVA_LIKE = "LIKE";
    
    /**
     * Constante que representa una consulta b&aacute;sica por docId a Nuxeo.
     */
    public static final String CONSULTA_BASICA_DOCUMENTOS_POR_DOCID = INSTRUCCION_SELECT + " * " + DIRECTIVA_FROM + " %s " 
    		+ DIRECTIVA_WHERE + " ecm:uuid " + DIRECTIVA_LIKE + " '%s'";
    
    /**
     * Consulta que define la query parametrizada para b&uacute;squeda de calendarios asociados a un elemento.
     */
    public static final String BUSQUEDA_CALENDARIOS = INSTRUCCION_SELECT + " * " + DIRECTIVA_FROM 
    		+ " CalendarioDC WHERE calcon:idDocumentos IN ('%s')";
    
    /**
     * Representa el documento Actividad dentro de la entidad Actividad.
     */
    public static final String ACTIVIDAD = "Actividad";
    
    /**
     * Representa el documento FuncionMarco dentro de la entidad Actividad.
     */
    public static final String FUNCION_MARCO = "FuncionMarco";
    
    /**
     * Representa el documento Funcion dentro de la entidad Actividad.
     */
    public static final String FUNCION = "Funcion";
    
    /**
     * Representa el documento Accion dentro de la entidad Actividad.
     */
    public static final String ACCION = "Accion";
    
    /**
     * Representa el documento Institucion dentro de la entidad Agente.
     */
    public static final String INSTITUCION = "Institucion";
    
    /**
     * Representa el documento Organo dentro de la entidad Agente.
     */
    public static final String ORGANO = "Organo";
    
    /**
     * Representa el documento Persona dentro de la entidad Agente.
     */
    public static final String PERSONA = "Persona";
    
    /**
     * Representa el documento Dispositivo dentro de la entidad Agente.
     */
    public static final String DISPOSITIVO = "Dispositivo";
    
    /**
     * Representa el documento GrupoDeFondo dentro de la entidad Documento.
     */
    public static final String GRUPO_DE_FONDO = "GrupoDeFondo";
    
    /**
     * Representa el documento Fondo dentro de la entidad Documento.
     */
    public static final String FONDO = "Fondo";
    
    /**
     * Representa el documento Serie dentro de la entidad Documento.
     */
    public static final String SERIE = "Serie";
    
    /**
     * Representa el documento Agregacion dentro de la entidad Documento.
     */
    public static final String AGREGACION = "Agregacion";
    
    /**
     * Representa el documento Expediente dentro de la entidad Documento.
     */
    public static final String EXPEDIENTE = "Expediente";
    
    /**
     * Representa el documento DocSimple dentro de la entidad Documento.
     */
    public static final String DOCUMENTO_SIMPLE = "DocSimple";
    
    /**
     * Representa el documento Normativa dentro de la entidad Regulacion.
     */
    public static final String NORMATIVA = "Normativa";
    
    /**
     * Representa el documento Procedimiento dentro de la entidad Regulacion.
     */
    public static final String PROCEDIMIENTO = "Procedimiento";
    
    /**
     * Representa el documento EventoGestionDocumentos dentro de la entidad Relacion.
     */
    public static final String EVENTO_GESTION_DOCUMENTOS = "EventoGestionDocumentos";
    
    /**
     * Representa el documento Procedencia dentro de la entidad Relacion.
     */
    public static final String PROCEDENCIA = "Procedencia";
    
    /**
     * Constante con el valor 1.
     */
    public static final int NUMERO_UNO = 1;
    
    /**
	 * Calendario de consrvacion.
	 */
	public static final String CALENDARIO_CONSERVACION = "calendario_conservacion";
	
	/**
	 * Warnings de unchecked.
	 */
	public static final String UNCHECKED_WARNING = "unchecked";
	
	/**
	 * Objetos (Expediente, Serie, etc...) asociados a un calendario.
	 */
	public static final String OBJETOS_ASOCIADOS_CALENDARIO = "objetosAsociados";
	
	/**
	 * Constante para el docId.
	 */
	public static final String DOC_ID = "docId";
	
	/**
	 * ID calendario.
	 */
	public static final String ID_CALENDARIO = "id_calendario";

	/**
	 * Calendario asociado.
	 */
	public static final String CALENDARIO_ASOCIADO = "calendario_asociado";

	/**
	 * ID de documentos.
	 */
	public static final String ID_DOCUMENTOS = "idDocumentos";
	
	/**
	 * Workflow.
	 */
	public static final String WORKFLOW_EP = "workflow";
	
	/**
	 * Numero de fases del Calendario.
	 */
	public static final Integer NUMERO_FASES_CALENDARIO = 3;
	
	/**
	 * Nombre del vocabulario de Fases del Calendario.
	 */
    public static final String DIRECTORIO_FASES_CALENDARIO= "valoresFase";
}
