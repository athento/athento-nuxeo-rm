package es.rm.platform.api.utils;

import static es.rm.platform.api.utils.ArchivoConstantes.REPOSITORIO_POR_DEFECTO;


/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.yerbabuena.athento.ecm.utils.AthentoNXUtils;

/**
 * 
 * Este listener se encargar&aacute; de capturar el evento lanzado por los
 * schedulers y se encargar&aacute; de ejecutar la finalizaci&oacute;n del
 * periodo de retenci&oacute;n y, por tanto, transici&oacute;n a la fase de
 * archivo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public class CapturaEventoFinRetencion implements EventListener {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory
			.getLog(CapturaEventoFinRetencion.class);

	/**
	 * Manejador principal del Listener.
	 * 
	 * @param event
	 *            Evento
	 * @exception ClientException
	 *                excepci&oacute;n lanzada en caso de error
	 */
	@Override
	public final void handleEvent(Event event) throws ClientException {

		EventContext evCtxt = event.getContext();
		String docId = (String) evCtxt.getProperty("docId");

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
		} finally {
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}

	}

}
