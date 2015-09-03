package es.rm.platform.bean;

import static org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager.CURRENT_DOCUMENT_SELECTION;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

import es.rm.platform.api.services.ArchivoServicio;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
/**
 * --------------------------------------------------------
 */

/**
 * Bean de asociacion de calendario.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
@Name("asociacionCalendario")
@Scope(ScopeType.CONVERSATION)
public class AsociacionCalendarioDCBean implements Serializable {

	private static int contador = 0;
	
	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory
			.getLog(AsociacionCalendarioDCBean.class);

	/**
	 * Manejador de documentos.
	 */
	@In(create = true, required = false)
	private transient CoreSession documentManager;

	/**
	 * Contexto de navegacion.
	 */
	@In(create = true, required = false)
	private transient NavigationContext navigationContext;

	/**
	 * FacesMessages para la muestra de mensajes en pantalla.
	 */
	@In(create = true, required = false)
	private FacesMessages facesMessages;

	/**
	 * Resources accesor.
	 */
	@In(create = true)
	private ResourcesAccessor resourcesAccessor;

	/**
	 * Manejador de listas de documentos.
	 */
	@In(required = false, create = true)
	private transient DocumentsListsManager documentsListsManager;

	/**
	 * Servicio de archivo.
	 */
	private ArchivoServicio servicioArchivo;

	/**
	 * Control de herencia.
	 */
	private boolean heredar;

	/**
	 * Flag para indicar si mostrar los resultados de busqueda.
	 */
	private boolean mostrarResultadosBusqueda;

	/**
	 * Calendario asociado.
	 */
	private DocumentModel calendarioAsociado;

	/**
	 * 
	 * @return flag de herencia.
	 */
	public final boolean isHeredar() {
		return this.heredar;
	}

	/**
	 * 
	 * @param heredar
	 *            flag de herencia a establecer
	 */
	public final void setHeredar(final boolean heredar) {
		this.heredar = heredar;
	}

	/**
	 * Devuelve el calendario asociado.
	 * 
	 * @return el calendario asociado
	 */
	public DocumentModel getCalendarioAsociado() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		return this.calendarioAsociado;
	}

	/**
	 * Asigna el calendario asociado.
	 * 
	 * @param calendarioAsociado
	 *            calendario asociado a establecer
	 */
	public void setCalendarioAsociado(final DocumentModel calendarioAsociado) {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		this.calendarioAsociado = calendarioAsociado;
	}

	/**
	 * 
	 * @return flag indicador de si mostrar los resultados de busqueda
	 */
	public final boolean isMostrarResultadosBusqueda() {
		return this.mostrarResultadosBusqueda;
	}

	/**
	 * 
	 * @param mostrarResultadosBusqueda
	 *            valor a establecer en el flag de mostrar resultados de
	 *            busqueda
	 */
	public final void setMostrarResultadosBusqueda(
			final boolean mostrarResultadosBusqueda) {
		this.mostrarResultadosBusqueda = mostrarResultadosBusqueda;
	}

	/**
	 * Indica si hay un calendario asociado al documento.
	 * 
	 * @param documentoActual
	 *            a comprobar
	 * @return true si hay calendario asociado, false en otro caso.
	 */
	public boolean calendarioAsociado(final DocumentModel documentoActual) {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		this.calendarioAsociado = this.servicioArchivo
				.buscarCalendarioAsociado(this.documentManager, documentoActual);
		return this.calendarioAsociado != null;
	}

	/**
	 * Cambia el valor del flag de mostrar los resultados de busqueda.
	 * 
	 * @param value
	 *            valor a modificar
	 */
	public final void toggleMostrarResultado(final boolean value) {
		LOG.info("toggling...");
		this.mostrarResultadosBusqueda = value;
	}

	
	public String obtenerNombreCalendario(final DocumentModel calendario) throws PropertyException, ClientException{
		String nombreCal="";
		
		DocumentModelList lista=this.documentManager.query("SELECT * FROM CalendarioDC");
		if(lista.size()<=contador){
			contador=0;
		}
		DocumentModel calendario1=lista.get(contador);
		contador++;
		nombreCal=calendario1.getPropertyValue("dc:title").toString();
		
		/*if(calendario!=null){
			nombreCal=calendario.getPropertyValue("dc:title").toString();
		}*/
		
		return nombreCal;
	}
	
	/**
	 * Asocia un calendario al documento actual.
	 * 
	 * @param calendario
	 *            a asociar
	 * @throws Exception 
	 */
	public void asociarCalendario(final DocumentModel calendario) throws Exception {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		
//		if(calendario==null){
//			throw new Exception("SII");
//		}

		DocumentModel documentoActual = this.navigationContext
				.getCurrentDocument();
		/*DocumentModelList lista=this.documentManager.query("SELECT * FROM CalendarioDC");
		DocumentModel calendario1=lista.get(0);*/
		
		
		/*try{
			this.servicioArchivo.asociarCalendario(this.documentManager,
				documentoActual, calendario1);
		} catch (ClientException e) {
			LOG.error("No se pudo asignar el calendario", e);
		}*/
		
		if(calendario!=null){
			this.servicioArchivo.asociarCalendario(this.documentManager,
					documentoActual, calendario);
		

		if (this.heredar) {
			DocumentModelList hijos = this.servicioArchivo
					.extraerTodosHijosCdC(this.documentManager, documentoActual);
			for (DocumentModel documento : hijos) {
				try{
					this.servicioArchivo.asociarCalendario(this.documentManager,
							documento, calendario);
				} catch (ClientException e) {
					LOG.error("No se pudo asignar el calendario", e);
				}
			}
		}

		LOG.info("Calendario asignado correctamente al documento '"
				+ documentoActual.getName() + "'");
		this.facesMessages.add(
				StatusMessage.Severity.INFO,
				this.resourcesAccessor.getMessages().get(
						"label.calendario.asignado"), this.resourcesAccessor
						.getMessages().get(documentoActual.getType()));
		}
	}

	/**
	 * Elimina un calendario asociado.
	 * 
	 * @param calendario
	 *            a eliminar
	 */
	public void eliminarCalendarioAsociado(final DocumentModel calendario) {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		DocumentModel documentoActual = this.navigationContext
				.getCurrentDocument();
		this.servicioArchivo.eliminarCalendario(this.documentManager,
				documentoActual, calendario);

		LOG.info("Calendario eliminado correctamente");
		this.facesMessages.add(
				StatusMessage.Severity.INFO,
				this.resourcesAccessor.getMessages().get(
						"label.calendario.eliminado"), this.resourcesAccessor
						.getMessages().get(documentoActual.getType()));
	}

	/**
	 * Extrae un calendario y lo establece como calendario asociado.
	 * 
	 * @param idCalendario
	 *            a extraer
	 */
	private void extraerCalendario(final String idCalendario) {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		try {
			if (idCalendario != null && !idCalendario.isEmpty()) {
				this.calendarioAsociado = this.documentManager
						.getDocument(new IdRef(idCalendario));
			}
		} catch (ClientException e) {
			LOG.error("Error extrayendo el calendario", e);
		}
	}

	/**
	 * Extrae el calendario del documento actual y lo establece como calendario
	 * asociado.
	 * 
	 */
	private void extraerCalendario() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		try {
			DocumentModel documentoActual = this.navigationContext
					.getCurrentDocument();
			String idCalendario = (String) documentoActual.getProperty(
					"calendario_asociado", "id_calendario");
			extraerCalendario(idCalendario);
		} catch (ClientException e) {
			LOG.error("Error extrayendo el calendario", e);
		}
	}

	/**
	 * Inicia un disposicion.
	 * 
	 */
	public void iniciarDisposicion() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		try {
			if (this.servicioArchivo == null) {
				this.servicioArchivo = Framework
						.getService(ArchivoServicio.class);
			}
			if (!this.documentsListsManager
					.isWorkingListEmpty(CURRENT_DOCUMENT_SELECTION)) {
				List<DocumentModel> documentos = this.documentsListsManager
						.getWorkingList(CURRENT_DOCUMENT_SELECTION);
				for (DocumentModel documento : documentos) {
					this.servicioArchivo.iniciarDisposicion(
							this.documentManager, documento);
				}

				this.facesMessages.add(
						StatusMessage.Severity.INFO,
						this.resourcesAccessor.getMessages().get(
								"label.iniciado.pol&iacute;tica.disposicion"),
						this.resourcesAccessor.getMessages().get(""));

			}
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("No se pudo iniciar disposicion ", e);
		}
	}

	/**
	 * Puede iniciar disposici&oacute;n sobre un documento del cdc.
	 * 
	 * @return true si se puede iniciar disposicion, false en otro caso
	 */
	public boolean puedeIniciarDisposicion() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		List<DocumentModel> docs = this.documentsListsManager
				.getWorkingList(CURRENT_DOCUMENT_SELECTION);
		boolean resultado = docs.size() > 0;
		for (DocumentModel documento : docs) {
			resultado = resultado
					& documento.hasFacet("elemento_cdc")
					& this.servicioArchivo.puedeIniciarDisposicion(
							this.documentManager, documento);
		}
		return resultado;
	}

	/**
	 * Inicializa el bean.
	 * @throws Exception 
	 * 
	 */
	@Create
	@Begin(join = true)
	public void iniciarBean() throws Exception {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		
		LOG.debug("Iniciando Bean para asociar calendarios...");
		try {
			this.servicioArchivo = Framework.getService(ArchivoServicio.class);
			extraerCalendario();
			this.mostrarResultadosBusqueda = false;
			this.heredar = false;
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("Error iniciando el bean", e);
		}
	}

	/**
	 * Resetea el Bean.
	 */
	public final void reset() {
		this.mostrarResultadosBusqueda = false;
		this.heredar = false;
	}

}
