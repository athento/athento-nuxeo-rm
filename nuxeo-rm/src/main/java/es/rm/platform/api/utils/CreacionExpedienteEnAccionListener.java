package es.rm.platform.api.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import es.rm.platform.api.services.ArchivoServicio;

/**
 * Listener que pone a un expediente a su estado de cerrado realizando las
 * acciones necesarias, cambio de estado, crear scheduler, etc...
 * 
 * @author yerbabuena
 * 
 */
public class CreacionExpedienteEnAccionListener implements EventListener {
	/** Traza errores LOG. */
	private static final Log LOG = LogFactory
			.getLog(CreacionExpedienteEnAccionListener.class);
	/** CONSTANTE repositorio. */
	public static final String REPOSITORY = "default";

	@Override
	public final void handleEvent(final Event event) throws ClientException {

		CoreSession session = null;
		try {
			EventContext ctx = event.getContext();
			if ((ctx instanceof DocumentEventContext)) {
				DocumentModel doc = ((DocumentEventContext) ctx)
						.getSourceDocument();
				if (doc != null) {
					String type = doc.getType();
					if (ArchivoConstantes.TIPO_EXPEDIENTE.equals(type)
							|| ArchivoConstantes.TIPO_EXPEDIENTE_REA
									.equals(type)
							|| ArchivoConstantes.TIPO_EXPEDIENTE_RELE
									.equals(type)
							|| ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS
									.equals(type)) {
						String identificador = null;
						Object objetoIdentificador = doc
								.getPropertyValue("expediente:Identificador");
						try {
							identificador = (String) objetoIdentificador;
						} catch (Exception ex) {
							LOG.error(
									"NO SE A PODIDO OBTENER EL IDENTIFICADOR DEL EXPEDIENTE",
									ex);
						}

						if (identificador != null) {

							// comprobar que el expediente tiene como padre una
							// accion.
							session = getCoreSession();

							DocumentModel docParent = session.getDocument(doc
									.getParentRef());

							if (docParent != null
									&& (docParent.getType().equals("Accion") || (docParent
											.getType().equals("Serie")))) {

								// Comprobamos que no tenga calendario asociado
								String idCalendario = (String) doc
										.getPropertyValue("calas:id_calendario");

								if (idCalendario == null) {

									LOG.info("handleEvent-es tipo de accion Accion type: "
											+ docParent.getType());

									ArchivoServicio servicioArchivo = getArchivoServicio();

									// Obtenemos el calendario del padre para
									// asociarlo al expediente
									idCalendario = (String) docParent
											.getPropertyValue("calas:id_calendario");
									LOG.info("handleEvent-idCalendario: "
											+ idCalendario);

									if (idCalendario != null) {

										DocumentModel calendario = session
												.getDocument(new IdRef(
														idCalendario));

										// asociamos el calendario.
										servicioArchivo.asociarCalendario(
												session, doc, calendario);

										// asignamos el calendario de la accion
										// al
										// expediente.
										idCalendario = (String) doc
												.getPropertyValue("calas:id_calendario");

										LOG.info("handleEvent-documento expediente con calendrario-idCalendario: "
												+ idCalendario);

										// ejecutamos accion de cierre de
										// expediente.

										servicioArchivo.cerrarCarpeta(session,
												doc);
									}
								}

							} else {
								LOG.error("No puede asociarse el calendario al expediente en vigencia por no estar en una Accion");
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			LOG.error("Error actualizando expediente.", ex);
		} finally {
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}
	}

	/**
	 * Obtiene un objeto de sesion.
	 * 
	 * @return objeto de sesion
	 * @throws ClientException
	 *             en caso de erro
	 */
	public static CoreSession getCoreSession() throws ClientException {
		LOG.debug("CoreSessionObject-getCoreSession-BEGIN");
		CoreSession systemSession = null;
		try {
			Framework.login();
			RepositoryManager manager = Framework
					.getService(RepositoryManager.class);
			Repository repository = manager.getRepository(REPOSITORY);
			if (repository == null) {
				LOG.debug("repository " + REPOSITORY
						+ " not in available repos: "
						+ manager.getRepositories());
				throw new ClientException("cannot get repository: "
						+ REPOSITORY);
			}
			systemSession = repository.open();
		} catch (ClientException e) {
			throw e;
		} catch (Exception e) {
			throw new ClientException(
					"Failed to open core session to repository " + REPOSITORY,
					e);
		}
		LOG.debug("CoreSessionObject-getCoreSession-END");
		return systemSession;
	}

	/**
	 * Obtener servicio de archivo.
	 * 
	 * @return servicio de archivo.
	 */
	public static ArchivoServicio getArchivoServicio() {
		ArchivoServicio archivoServicio = null;
		try {
			archivoServicio = Framework.getService(ArchivoServicio.class);
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
		return archivoServicio;
	}

}
