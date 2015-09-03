package es.rm.platform.api.services;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

import es.rm.platform.api.exception.RMException;
import es.rm.platform.api.utils.DispositivoDto;

/**
 * Interfaz de archivo servicio.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public interface ArchivoServicio {

    /**
     * Metodo que devuelve todos los workflows definidos para el RM.
     * 
     * @return ids de los flujos de trabajo
     */
    List<String> extraerIdsFlujosTrabajo();

    /**
     * Metodo que devuelve todos los eventos definidos para el RM.
     * 
     * @return eventos extraidos
     */
    List<String> extraerEventos();

    /**
     * Metodo que devuelve las posibles fases para asociar a un Calendario de
     * Conservacion.
     * 
     * @return frases extraidas
     */
    List<String> extraerFases();

    /**
     * 
     * Metodo que ejecuta la accion de cerrar una CAdD.
     * 
     * @param documentManager la sesi&oacute;n
     * @param carpeta es el Documento carpeta a cerrar
     * @return true si se ejecut&oacute; con &eacute;xito la acci&oacute;n; false en caso contrario
     */
    boolean cerrarCarpeta(CoreSession documentManager, DocumentModel carpeta);

    /**
     * Metodo que ejecuta la accion de reabrir una CAdD cerrada.
     * 
     * @param documentManager la sesi&oacute;n
     * @param carpeta a reabrir
     */
    void reabrirCarpeta(CoreSession documentManager, DocumentModel carpeta);

    /**
     * Metodo que ejecuta la accion de retener una CAdD.
     * 
     * @param documentManager la sesi&oacute;n
     * @param carpeta es el Documento carpeta a cerrar
     * @throws RMException en caso de error
     */
    void retenerCarpeta(CoreSession documentManager, DocumentModel carpeta) throws RMException;
    
    /**
     * Metodo que cancela la retenci&oacute;n aplicada a la CAdD.
     * 
     * @param documentManager instancia de la sesi&oacute;n
     * @param carpeta Documento carpeta a desbloquear
     */
    void desbloquearRetencionCarpeta (CoreSession documentManager, DocumentModel carpeta);

    /**
     * Mediante este metodo se comprueba si el documento actual es una CAdD y
     * no esta cerrada, o bien, en caso de no ser una CAdD, comprueba si esta
     * dentro de la ruta de alguna CAdD que este cerrada.
     * 
     * El objetivo de este metodo es comprobar si se puede crear o no contenido
     * en el documento Actual, ya que, de acuerdo con los requisitos, cuando una
     * CAdD este cerrada, no se puede crear contenido en su interior.
     * 
     * @param documentoActual es el documento sobre el que se quiere consultar
     *            si es una CAdD y y no esta cerrada, o, en caso de no ser una
     *            CAdD, si no existe en toda la ruta que le contiene ninguna
     *            CAdD cerrada
     * @param sesion es la sesion
     * @return true si la carpeta no esta cerrada, false en otro caso
     */
    boolean carpetaNoCerrada(DocumentModel documentoActual,
            CoreSession sesion);

    /**
     * Metodo que indica si un documento es elemento de un Cuadro de
     * Clasificacion. Se comprueba que el modelo contiene la faceta de
     * 'elemento_cdc'.
     * 
     * @param documento el documento a analizar
     * @return true si es documento del CdC, false en caso contrario
     */
    boolean esDocumentoDelCdD(DocumentModel documento);

    /**
     * Metodo que asocia un calendario a un elemento de una CdC y
     * recursivamente a sus hijos si esta marcada la opcion correspondiente.
     * 
     * @param documentManager sesion
     * @param documento documento actual del CAdD
     * @param calendario calendario a asociar
     * @throws Exception 
     */
    void asociarCalendario(CoreSession documentManager,
            DocumentModel documento, DocumentModel calendario) throws Exception;

    /**
     * Metodo que elimina la asociacion entre un calendario y un documento del
     * CdC.
     * 
     * @param documentManager sesion
     * @param documento documento actual del CAdD
     * @param calendario calendario a asociar
     */
    void eliminarCalendario(CoreSession documentManager,
            DocumentModel documento, DocumentModel calendario);

    /**
     * Metodo que devuelve el calendario asociado a un elemento del CdC.
     * 
     * @param documentManager sesion
     * @param documento documento a analizar
     * @return calendario
     */
    DocumentModel sacarCalendario(CoreSession documentManager,
            DocumentModel documento);

    /**
     * Metodo que devuelve todo el contenido (recursivo) del documento pasado
     * como parametro, con la condicion de que solo se incluyen aquellos
     * elementos que pertenecen al CdC.
     * 
     * @param documentManager sesion
     * @param documentoInicial documento a analizar
     * @return todos los hijos del CdC
     */
    DocumentModelList extraerTodosHijosCdC(CoreSession documentManager,
            DocumentModel documentoInicial);

    /**
     * Metodo que envia un calendario a la papelera.
     * 
     * @param documentManager sesion
     * @param calendario calendario a eliminar
     */
    void eliminarCalendario(CoreSession documentManager,
            DocumentModel calendario);

    /**
     * Metodo que recupera un calendario de la papelera.
     * 
     * @param documentManager sesion
     * @param calendario calendario a asociar
     */
    void recuperarCalendario(CoreSession documentManager,
            DocumentModel calendario);

    /**
     * Metodo que devuelve si existen elementos asociados a un calendario.
     * 
     * @param documentManager sesion
     * @param calendario calendario a analizar
     * @return true si los elementos estan asociados al calendario
     */
    boolean elementosAsociadosAlCalendario(CoreSession documentManager,
            DocumentModel calendario);

    /**
     * Metodo que permite iniciar una disposicion sobre un elemento del CdC.
     * 
     * @param documentManager sesion
     * @param documento elemento del CdC
     */
    void iniciarDisposicion(CoreSession documentManager,
            DocumentModel documento);

    /**
     * Metodo que permite comprobar si se puede iniciar una disposicion sobre
     * un docE.
     * 
     * @param documentManager sesion
     * @param documento para comprobar si puede iniciar disposicion
     * @return true si el documento puede iniciar disposicion, false en otro
     *         caso
     * 
     */
    boolean puedeIniciarDisposicion(CoreSession documentManager,
            DocumentModel documento);
    
    /**
     * Metodo que busca si existen calendarios asociados al documento pasado como par&aacute;metro.
     * En caso de existir lo devuelve.
     * 
     * @param documentManager sesion
     * @param documento modelo de documento sobre el cual se quiere buscar esta asociaci&oacute;n de calendario
     * @return Documento de Nuxeo
     */
    DocumentModel buscarCalendarioAsociado(CoreSession documentManager, DocumentModel documento);
    
    /**
     * M&eacute;todo encargado de finalizar el per&iacute;odo de retenci&oacute;n de un Expediente (de acuerdo a su calendario
     * de conservaci&oacute;n asociado).
     * 
     * @param documentManager sesion
     * @param documento expediente en fase de vigencia
     */
    void finalizarPeriodoRetencion(CoreSession documentManager, DocumentModel documento);
    
    /**
     * Obtiene una entidad Actividad del Modelo.
     * 
     * @param documentManager es la sesi&oacute;n
     * @param docId es el id del documento del repositorio
     * @return Documento de Nuxeo
     */
    DocumentModel obtenerActividad(CoreSession documentManager, String docId);
    
    /**
     * Obtiene una entidad Agente del Modelo.
     * 
     * @param documentManager es la sesi&oacute;n
     * @param docId es el id del documento del repositorio
     * @return Documento de Nuxeo
     */
    DocumentModel obtenerAgente(CoreSession documentManager, String docId);
    
    /**
     * Obtiene una entidad Regulaci&oacute;n del Modelo.
     * 
     * @param documentManager es la sesi&oacute;n
     * @param docId es el id del documento del repositorio
     * @return Documento de Nuxeo
     */
    DocumentModel obtenerRegulacion(CoreSession documentManager, String docId);
    
    /**
     * Obtiene una entidad Relaci&oacute;n del Modelo.
     * 
     * @param documentManager es la sesi&oacute;n
     * @param docId es el id del documento del repositorio
     * @return Documento de Nuxeo
     */
    DocumentModel obtenerRelacion(CoreSession documentManager, String docId);
    
    /**
     * Obtiene una entidad Documento del Modelo.
     * 
     * @param documentManager es la sesi&oacute;n
     * @param docId es el id del documento del repositorio
     * @return Documento de Nuxeo
     */
    DocumentModel obtenerDocumento(CoreSession documentManager, String docId);
    
	/**
	 * Obtiene todos los dispositovos del directorio de dispositivos.
	 * 
	 * @return lista de dispositivos.
	 */
    List<DispositivoDto> getDispositivos();
    
}