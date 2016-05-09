package es.rm.platform.bean;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager.CURRENT_DOCUMENT_SELECTION;
import static org.nuxeo.ecm.webapp.helpers.EventNames.DOCUMENT_CHILDREN_CHANGED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.action.DeleteActions;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

import es.rm.platform.api.exception.RMException;
import es.rm.platform.api.services.ArchivoServicio;
import es.rm.platform.api.utils.ArchivoConstantes;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
/**
 * --------------------------------------------------------
 */

/**
 * Bean del archivo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
@Name("archivoBean")
@Scope(CONVERSATION)
public class ArchivoBean extends InputController implements Serializable {

	/**
	 * Numero de serializacion.
	 */
	private static final long serialVersionUID = 3339085290071870496L;

	/**
	 * Atributo para gestion de trazas de la aplicacion.
	 */
	private static final Log LOG = LogFactory.getLog(ArchivoBean.class);

	/** Constante numero 1. */
	private static final int CONST_1 = 1;

	/**
	 * Interfaz al servicio de sesion de Nuxeo.
	 */
	@In(create = true, required = false)
	private transient CoreSession documentManager;

	/**
	 * Contexto de navegacion.
	 */
	@In(create = true, required = false)
	private transient NavigationContext navigationContext;

	/**
	 * Utilizado para mostrar mensajes de informacion por pantalla.
	 */
	@In(create = true, required = false)
	private transient FacesMessages facesMessages;

	/**
	 * Acceso a los recursos de Nuxeo.
	 */
	@In(create = true)
	private ResourcesAccessor resourcesAccessor;

	/**
	 * Manejador de listas de documentos.
	 */
	@In(required = false, create = true)
	private transient DocumentsListsManager documentsListsManager;

	/**
	 * Inyeccion del "bean" de borrado de Nuxeo.
	 */
	@In(create = true)
	private transient DeleteActions deleteActions;

	/**
	 * Servicio de archivo.
	 */
	private ArchivoServicio servicioArchivo;

	/**
	 * Parametro inyectado desde la vista indicando el path de un documento.
	 */
	@RequestParameter
	protected String parentDocumentPath;

	/**
	 * Metodo usado para cerrar una CAdD.
	 * 
	 */
	public void cerrarCarpeta() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		DocumentModel carpeta = this.navigationContext.getCurrentDocument();
		boolean cerrada = this.servicioArchivo.cerrarCarpeta(
				this.documentManager, carpeta);
		if (cerrada) {
			this.facesMessages.add(
					StatusMessage.Severity.INFO,
					this.resourcesAccessor.getMessages().get(
							"label.carpeta.cerrada"), this.resourcesAccessor
							.getMessages().get(carpeta.getType()));
		} else {
			this.facesMessages
					.add(StatusMessage.Severity.ERROR,
							this.resourcesAccessor.getMessages().get(
									"label.error.cerrar.carpeta"),
							this.resourcesAccessor.getMessages().get(
									carpeta.getType()));
		}

	}

	/**
	 * Metodo usado para reabrir una CAdD.
	 * 
	 */
	public void reabrirCarpeta() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		DocumentModel carpeta = this.navigationContext.getCurrentDocument();
		this.servicioArchivo.reabrirCarpeta(this.documentManager, carpeta);
		this.facesMessages.add(
				StatusMessage.Severity.INFO,
				this.resourcesAccessor.getMessages().get(
						"label.carpeta.reabierta"), this.resourcesAccessor
						.getMessages().get(carpeta.getType()));
	}

	/**
	 * Metodo usado para retener una CAdD.
	 * 
	 */
	public void retenerCarpeta() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		DocumentModel carpeta = this.navigationContext.getCurrentDocument();
		boolean retenido = false;
		try {
			this.servicioArchivo.retenerCarpeta(this.documentManager, carpeta);
			retenido = true;
		} catch (RMException ex) {

			LOG.error(
					"***No ha producido un error reteniendo el expediente o carpeta... ",
					ex);

			this.facesMessages
					.add(StatusMessage.Severity.WARN,
							this.resourcesAccessor.getMessages().get(
									"label.carpeta.retenida.sin.calendario"),
							this.resourcesAccessor.getMessages().get(
									carpeta.getType()));

		}
		if (retenido) {
			this.facesMessages.add(
					StatusMessage.Severity.INFO,
					this.resourcesAccessor.getMessages().get(
							"label.carpeta.retenida"), this.resourcesAccessor
							.getMessages().get(carpeta.getType()));
		}
	}

	/**
	 * Metodo usado para desbloquear la retenci&oacute;n de una CAdD.
	 * 
	 */
	public void desbloquearCarpeta() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		DocumentModel carpeta = this.navigationContext.getCurrentDocument();
		this.servicioArchivo.desbloquearRetencionCarpeta(this.documentManager,
				carpeta);
		this.facesMessages.add(
				StatusMessage.Severity.INFO,
				this.resourcesAccessor.getMessages().get(
						"label.carpeta.desbloqueada"), this.resourcesAccessor
						.getMessages().get(carpeta.getType()));
	}

	/**
	 * Metodo que devuelve si el documento es una carpeta de documentos y no
	 * esta cerrada o, en el caso de no ser una carpeta de documentos, si alguna
	 * de las carpetas que le contiene no esta cerrada.
	 * 
	 * @return true si la carpeta no esta cerrada, false en otro caso
	 * 
	 */
	public boolean carpetaNoCerrada() {
		return this.servicioArchivo.carpetaNoCerrada(this.navigationContext
                        .getCurrentDocument(),
				this.documentManager);
	}

	/**
	 * M&eacute;todo que devuelve si la carpeta actual est&aacute; Abierta.
	 * 
	 * @return true si la carpeta est&aacute; en estado abierto
	 */
	public boolean carpetaAbierta() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		boolean result = false;
		try {
			DocumentModel documentoActual = this.navigationContext
					.getCurrentDocument();
			if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_EXPEDIENTE)) {
				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
				String cicloDeVida = documentoActual.getCurrentLifeCycleState();
				result = (cicloDeVida
						.equals(ArchivoConstantes.FASE_TRAMITACION) || cicloDeVida
						.equals(ArchivoConstantes.FASE_CREACION))
						&& (estado
								.equals(ArchivoConstantes.ESTADO_ABIERTO_CARPETA_DE_DOCUMENTOS) || estado
								.isEmpty());
			} else if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {

				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);

				String cicloDeVida = documentoActual.getCurrentLifeCycleState();
				result = cicloDeVida.equals(ArchivoConstantes.FASE_CREACION)
						&& (estado
								.equals(ArchivoConstantes.ESTADO_ABIERTO_CARPETA_DE_DOCUMENTOS) || estado
								.isEmpty());
			}
		} catch (ClientException e) {
			LOG.error(
					"No se pudo consultar el estado de la carpeta o expediente ",
					e);
		}
		return result;
	}

	/**
	 * M&eacute;todo que devuelve si la carpeta actual est&aacute; cerrada.
	 * 
	 * @return true si la carpeta est&aacute; en estado cerrada
	 */
	public boolean carpetaCerrada() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		boolean result = false;
		try {
			DocumentModel documentoActual = this.navigationContext
					.getCurrentDocument();
			if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_EXPEDIENTE)
					|| documentoActual.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_REA)
					|| documentoActual.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
				String cicloDeVida = documentoActual.getCurrentLifeCycleState();
				result = cicloDeVida.equals(ArchivoConstantes.FASE_VIGENCIA)
						&& (estado
								.equals(ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS) || estado
								.isEmpty());
			} else if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {

				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
				String cicloDeVida = documentoActual.getCurrentLifeCycleState();
				result = cicloDeVida.equals(ArchivoConstantes.FASE_VIGENCIA)
						&& (estado
								.equals(ArchivoConstantes.ESTADO_CERRADO_CARPETA_DE_DOCUMENTOS)
								|| estado.isEmpty() || estado
									.equals(ArchivoConstantes.ESTADO_CERRADO_DESBLOQUEADA_CARPETA_DE_DOCUMENTOS));
			}
		} catch (ClientException e) {
			LOG.error(
					"No se pudo consultar el estado de la carpeta o expediente ",
					e);
		}
		return result;
	}

	/**
	 * M&eacute;todo que devuelve si la carpeta actual est&aacute; Retenida.
	 * 
	 * @return true si la carpeta est&aacute; en estado retenida
	 */
	public boolean carpetaRetenida() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		boolean result = false;
		try {
			DocumentModel documentoActual = this.navigationContext
					.getCurrentDocument();
			if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_EXPEDIENTE)
					|| documentoActual.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_REA)
					|| documentoActual.getType().equals(
							ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {
				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
                if (estado != null) {
                    result = estado
                            .equals(ArchivoConstantes.ESTADO_RETENIDO_CARPETA_DE_DOCUMENTOS)
                            || estado.isEmpty();
                }
			} else if (documentoActual.getType().equals(
					ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS)) {
				String estado = (String) documentoActual.getProperty(
						ArchivoConstantes.ESQUEMA_CADD,
						ArchivoConstantes.CAMPO_ESTADO_EXPEDIENTE_CADD);
                if (estado != null) {
                    result = estado
                            .equals(ArchivoConstantes.ESTADO_RETENIDO_CARPETA_DE_DOCUMENTOS)
                            || estado.isEmpty();
                }
			}
		} catch (ClientException e) {
			LOG.error(
					"No se pudo consultar el estado de la carpeta o expediente ",
					e);
		}
		return result;
	}

	/**
	 * Elimina el calendario indicado como argumento.
	 * 
	 * @param docId
	 *            es el identificador del documento
	 */
	@WebRemote
	public String eliminarCalendario(String docId) {
		DocumentModel calendario;
		try {
			LOG.debug("Eliminando calendario " + docId);
			calendario = documentManager.getDocument(new IdRef(docId));
			if (calendario != null
					&& !this.servicioArchivo.elementosAsociadosAlCalendario(
							this.documentManager, calendario)) {
				documentManager.removeDocument(new IdRef(docId));
				// Globally refresh content views
	            Events.instance().raiseEvent(DOCUMENT_CHILDREN_CHANGED);
				this.facesMessages.add(
						StatusMessage.Severity.INFO,
						this.resourcesAccessor.getMessages().get(
								"label.eliminar.calendario.eliminado"),
						this.resourcesAccessor.getMessages().get(""));
			} else {
				this.facesMessages.add(
						StatusMessage.Severity.WARN,
						this.resourcesAccessor.getMessages().get(
								"label.eliminar.calendario.con.asociacion"),
						this.resourcesAccessor.getMessages().get(""));
			}
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return "OK";
	}

	/**
	 * Elimina los calendarios seleccionados.
	 * 
	 */
	public void eliminarCalendariosSeleccionados() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		try {
			DocumentModel documentoActual = this.navigationContext
					.getCurrentDocument();

			if (!documentoActual.getType().equals(
					ArchivoConstantes.TIPO_CALENDARIO_ROOT)) {
				this.deleteActions.deleteSelection();
			} else {
				if (!this.documentsListsManager
						.isWorkingListEmpty(CURRENT_DOCUMENT_SELECTION)) {
					List<DocumentModel> calendarios = this.documentsListsManager
							.getWorkingList(CURRENT_DOCUMENT_SELECTION);
					boolean eliminable = true;
					for (DocumentModel calendario : calendarios) {
						eliminable = eliminable
								& !this.servicioArchivo
										.elementosAsociadosAlCalendario(
												this.documentManager,
												calendario);
					}
					if (eliminable) {
						this.deleteActions.deleteSelection();
					} else {
						this.facesMessages
								.add(StatusMessage.Severity.WARN,
										this.resourcesAccessor
												.getMessages()
												.get("label.eliminar.calendario.con.asociacion"),
										this.resourcesAccessor.getMessages()
												.get(""));
					}
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo eliminar el calendario ", e);
		}
	}

	/**
	 * Extrae los workflows.
	 * 
	 * @return lista de SelectItem con los workflows
	 */
	public List<SelectItem> extraerWorkflows() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		List<SelectItem> workflows = new ArrayList<SelectItem>();
		List<String> ids = this.servicioArchivo.extraerIdsFlujosTrabajo();
		SelectItem noItem = new SelectItem();
		noItem.setLabel(this.resourcesAccessor.getMessages().get(
				"label.vocabulary.selectValue"));
		noItem.setValue(this.resourcesAccessor.getMessages().get(
				"label.vocabulary.selectValue"));
		workflows.add(noItem);
		for (String id : ids) {
			SelectItem item = new SelectItem();
			item.setLabel(this.resourcesAccessor.getMessages().get(id));
			item.setValue(this.resourcesAccessor.getMessages().get(id));
			workflows.add(item);
		}
		return workflows;
	}

	/**
	 * Extrae los eventos.
	 * 
	 * @return lista de SelectItem con los workflows
	 */
	public List<SelectItem> extraerEventos() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		List<SelectItem> eventos = new ArrayList<SelectItem>();
		List<String> ids = this.servicioArchivo.extraerEventos();
		for (String id : ids) {
			SelectItem item = new SelectItem();
			item.setLabel(id);
			item.setValue(id);
			eventos.add(item);
		}
		return eventos;
	}

	/**
	 * Extrae las fases.
	 * 
	 * @return lista de SelectItem con las fases
	 */
	public List<SelectItem> extraerFases() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		List<SelectItem> fases = new ArrayList<SelectItem>();
		List<String> ids = this.servicioArchivo.extraerFases();
		for (String id : ids) {
			SelectItem item = new SelectItem();
			item.setLabel(id);
			item.setValue(id);
			fases.add(item);
		}
		return fases;
	}

	/**
	 * Inicializador del bean.
	 */
	@Create
	@Begin(join = true)
	public void iniciarBean() {
		/*
		 * Conflicto con Checkstyle. No se pueden declarar como final los
		 * m&eacute;todos de beans EJB que hagan uso de dependencias inyectadas,
		 * ya que dichas dependencias toman el valor null.
		 */
		LOG.debug("Iniciando Bean de Archivo...");
		try {
			this.servicioArchivo = Framework.getService(ArchivoServicio.class);
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("Error obteniendo el servicio de archivo", e);
		}
	}

	/**
	 * Crea un documento tipo Calendario de Conservación.
	 * 
	 * @return un String que redirigirá a la pantalla adecuada.
	 * @throws ClientException
	 *             excepción manejada.
	 */
	@SuppressWarnings("unchecked")
	public String crearCalendario() throws ClientException {
		DocumentModel nuevoDocumento = this.navigationContext
				.getChangeableDocument();

		String redireccion = "";

		if (nuevoDocumento.getId() != null) {
			LOG.debug("Calendario " + nuevoDocumento.getName() + " ya creado");
			redireccion = this.navigationContext.navigateToDocument(
					nuevoDocumento, "after-create");
		} else {
			try {
				PathSegmentService pss;
				try {
					pss = Framework.getService(PathSegmentService.class);
				} catch (Exception e) {
					throw new ClientException(e);
				}
				DocumentModel documentoActual = this.navigationContext
						.getCurrentDocument();
				if (this.parentDocumentPath == null) {
					if (documentoActual == null) {
						this.parentDocumentPath = this.documentManager
								.getRootDocument().getPathAsString();
					} else {
						this.parentDocumentPath = this.navigationContext
								.getCurrentDocument().getPathAsString();
					}
				}

				List<Map<String, Object>> fases = (List<Map<String, Object>>) nuevoDocumento
						.getProperty("calendario_conservacion", "fase");

				if (fases == null) {
					this.facesMessages.add(
							StatusMessage.Severity.INFO,
							this.resourcesAccessor.getMessages().get(
									"label.fases.calendario.vacias"),
							this.resourcesAccessor.getMessages().get(
									nuevoDocumento.getType()));
				} else {

					List<Map<String, Object>> fasesPurgadas = purgarFases(fases);

					if (fases.size() > ArchivoConstantes.NUMERO_FASES_CALENDARIO
							|| fasesPurgadas.size() != ArchivoConstantes.NUMERO_FASES_CALENDARIO) {
						this.facesMessages.add(
								StatusMessage.Severity.INFO,
								this.resourcesAccessor.getMessages().get(
										"label.fases.calendario.incorrectas"),
								this.resourcesAccessor.getMessages().get(
										nuevoDocumento.getType()));
						return "";
					} else {

						nuevoDocumento.setPathInfo(this.parentDocumentPath,
								pss.generatePathSegment(nuevoDocumento));

						nuevoDocumento = this.documentManager
								.createDocument(nuevoDocumento);
						this.documentManager.save();

						logDocumentWithTitle("Created the document: ",
								nuevoDocumento);
						this.facesMessages.add(
								StatusMessage.Severity.INFO,
								this.resourcesAccessor.getMessages().get(
										"document_saved"),
								this.resourcesAccessor.getMessages().get(
										nuevoDocumento.getType()));

						Events.instance().raiseEvent(
								EventNames.DOCUMENT_CHILDREN_CHANGED,
								documentoActual);
						redireccion = this.navigationContext
								.navigateToDocument(nuevoDocumento,
										"after-create");
					}
				}
			} catch (Throwable t) {
				throw ClientException.wrap(t);
			}
		}
		return redireccion;
	}

	/**
	 * Validador JSF para comprobar que un String sea un numero Entero.
	 * 
	 * @param context
	 *            contexto JSF
	 * @param component
	 *            componente JSF
	 * @param value
	 *            valor del componente JSF
	 */
	public final void esEntero(final FacesContext context,
			final UIComponent component, final Object value) {
		if (value instanceof String) {

			if (!checkEntero((String) value)) {

				FacesMessage message = new FacesMessage(
						FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(
								context, "label.not.integer"), null);
				// also add global message
				context.addMessage(null, message);
				throw new ValidatorException(message);
			}
		}
	}

	/**
	 * Elimina las fases duplicadas en un calendario.
	 * 
	 * @param fasesCalendario
	 *            fases del calendario extraídas.
	 * @return lista de fases purgadas (sin duplicados).
	 */
	private List<Map<String, Object>> purgarFases(
			final List<Map<String, Object>> fasesCalendario) {
		List<Map<String, Object>> fases = new ArrayList<Map<String, Object>>();
		fases.addAll(fasesCalendario);

		for (int index = 0; index < fasesCalendario.size() - CONST_1; index++) {
			Map<String, Object> fase = fasesCalendario.get(index);
			String nombreFase = (String) fase.get("nombre_fase");
			for (int index2 = index + CONST_1; index2 < fasesCalendario.size(); index2++) {
				Map<String, Object> fase2 = fasesCalendario.get(index2);
				String nombreFase2 = (String) fase2.get("nombre_fase");
				if (nombreFase.equals(nombreFase2)) {
					fases.remove(index2);
				}
			}
		}

		return fases;
	}

	/**
	 * Comprueba que una cadena String sea un numero entero.
	 * 
	 * @param s
	 *            cadena de caracteres
	 * @return booleano
	 */
	private final boolean checkEntero(final String s) {
		boolean result = true;
		try {
			if (s.contains(".")) {
				result = false;
			} else {
				Integer.parseInt(s);
			}
		} catch (NumberFormatException e) {
			result = false;
		}
		// only got here if we didn't return false
		return result;
	}

}
