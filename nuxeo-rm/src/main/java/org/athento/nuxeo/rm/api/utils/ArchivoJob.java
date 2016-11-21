package org.athento.nuxeo.rm.api.utils;

import static org.athento.nuxeo.rm.api.utils.ArchivoConstantes.REPOSITORIO_POR_DEFECTO;

import javax.security.auth.login.LoginContext;


/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreInstance;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.UserPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.core.event.impl.EventImpl;
import org.nuxeo.ecm.platform.ui.web.auth.NuxeoAuthenticationFilter;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.yerbabuena.athento.ecm.utils.AthentoNXUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Implementaci&oacute;n de la clase de Quartz Job para Archivo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public class ArchivoJob implements Job {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(ArchivoJob.class);

	/**
	 * M&eacute;todo que ejecutar&aacute; el trigger.
	 * 
	 * 
	 * @param dataMap
	 *            par&aacute;metros del Trigger
	 * @throws Exception
	 *             excepcion lanzada
	 */
	@SuppressWarnings("unchecked")
	protected final void execute(JobDataMap dataMap) throws Exception {
		/*
		 * Conflicto con PMD. El m&eacute;todo debe lanzar la excepci&oacute;n
		 * Exception ya que implementa una interfaz externa que la declara.
		 */

		LOG.debug("Ejecutando tarea programada:" + dataMap);
		String eventId = dataMap.getString("eventId");
		LOG.debug("Ejecutando tarea programada-eventId:" + eventId);
		String eventCategory = dataMap.getString("eventCategory");
		LOG.debug("Ejecutando tarea programada-eventCategory:" + eventCategory);
		String username = dataMap.getString("username");
		LOG.debug("Ejecutando tarea programada-username:" + username);

		EventService eventService = Framework.getService(EventService.class);
		if (eventService == null) {
			LOG.error("Cannot find EventService");
			return;
		}

		LoginContext loginContext = null;
		try {

			// login
			if (username == null) {
				loginContext = Framework.login();
			} else {
				loginContext = NuxeoAuthenticationFilter.loginAs(username);
			}

			// set up event context
			UserPrincipal principal = new UserPrincipal(username, null, false,
					false);
			EventContext eventContext = new EventContextImpl(null, principal);
			eventContext.setProperty("category", eventCategory);
			Map<String, Serializable> map = new HashMap();
			for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
				if (entry instanceof Serializable) {
					map.put(entry.getKey(), (Serializable) entry.getValue());
				}
			}
			eventContext.setProperties(map);
			Event event = new EventImpl(eventId, eventContext);

			// start transaction
			TransactionHelper.startTransaction();

			// send event
			LOG.info("Sending scheduled event id=" + eventId + ", category="
					+ eventCategory + ", username=" + username);
			eventService.fireEvent(event);
		} finally {
			// finish transaction
			TransactionHelper.commitOrRollbackTransaction();

			// logout
			if (loginContext != null) {
				loginContext.logout();
			}
		}
	}

	/**
	 * M&eacute;todo que ejecutar&aacute; el trigger (con el contexto como
	 * parametro).
	 * 
	 * @param context
	 *            es el contexto
	 * @exception JobExecutionException
	 *                es la excepci&oacute;n lanzada en caso de error
	 */
	@Override
	public final void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOG.debug("Ejecutando tarea programada-context:" + context);
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String docId = dataMap.getString("docId");

		LOG.debug("Ejecutando tarea programada-docId:" + docId);

		CoreSession session = null;
		try {
			session = AthentoNXUtils.getCoreSession(REPOSITORIO_POR_DEFECTO);
			DocumentModel documento = session.getDocument(new IdRef(docId));

			UtilidadesArchivo.finalizarPeriodoDeRetencion(session, documento);
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error(
					"Error al ejecutar Listener para finalizar retenci&oacute;n del expediente: ",
					e);
			throw new JobExecutionException(e);
		} finally {
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}

	}

}
