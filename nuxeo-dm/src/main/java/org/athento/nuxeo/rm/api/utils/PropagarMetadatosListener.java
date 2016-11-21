package org.athento.nuxeo.rm.api.utils;

import java.util.Arrays;
import java.util.Map;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.schema.types.Type;

/**
 * Propagacion de metadatos listener usado para la transicion de valores de
 * metadatos entre la jerarquia de subentidades del modelo documental emrm.
 * 
 * @author victorsanchez
 * 
 */
public class PropagarMetadatosListener implements EventListener {

	/**
	 * Tipolog&iacute;as aplicables.
	 */
	private static final String[] TIPOLOGIAS_APLICABLES = { "EntidadContenedora" };

	/**
	 * Esquemas propagaci&oacute;n entidades.
	 */
	private static final String[] ESQUEMAS_PROPAGACION_ENTIDADES = {
			"emrm_documento_grupofondo", "emrm_documento_fondo",
			"emrm_documento_serie", "emrm_actividad_funcionmarco",
			"emrm_actividad_funcion", "emrm_actividad_actividad",
			"emrm_actividad_accion" };

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory
			.getLog(PropagarMetadatosListener.class);

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
		try {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Manejando listener de propagacion de metadatos...");
			}
			if (evCtxt instanceof DocumentEventContext) {
				CoreSession session = evCtxt.getCoreSession();
				// Obtenemos documento vacio listo para crear desde el contexto
				DocumentModel documentoACrear = ((DocumentEventContext) evCtxt)
						.getSourceDocument();
				// Comprobamos que la tipologia es aplicable a la propagacion
				if (propagacionAplicable(documentoACrear)) {
					// Propagamos metadatos desde el padre actual
					propagarMetadatos(documentoACrear,
							documentoACrear.getParentRef(), session);
					// Guardamos informacion
					session.save();
				}
			}
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("Imposible realizar la propagacion de metadatos", e);
		}

	}

	/**
	 * Comprueba que la tipoliga del documento creado es aplicable a
	 * propagacion.
	 * 
	 * @param documento
	 *            creado para comprobar
	 * @return true si el documento debe obtener metadatos propagados, false en
	 *         otro caso
	 */
	private boolean propagacionAplicable(DocumentModel documento) {
		Type[] tipos = documento.getDocumentType().getTypeHierarchy();
		boolean result = false;
		for (Type tipo : tipos) {
			if (Arrays.asList(TIPOLOGIAS_APLICABLES).contains(tipo.getName())) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Metodo recursivo que propaga los metadatos del documento padre (si
	 * existe) al hijo.
	 * 
	 * @param documento
	 *            es el documento donde se propagarna los metadatos
	 * @param refDocumentoPadre
	 *            es la referencia del documento padre desde donde se propagaran
	 *            los metadatos
	 * @param session
	 *            La sesi&oacute;n de Nuxeo
	 * @throws ClientException
	 *             en caso de error de propagacion general del documento padre
	 */
	private void propagarMetadatos(DocumentModel documento,
			DocumentRef refDocumentoPadre, CoreSession session)
			throws ClientException {
		if (refDocumentoPadre == null) {
			return;
		} else {
			DocumentModel documentoPadre = session
					.getDocument(refDocumentoPadre);
			if (documentoPadre != null) {
				// Hacemos check de esquemas del documento hijo para obtenerlos
				// del padre
				String[] esquemasHijo = documento.getSchemas();
				for (String esquema : esquemasHijo) {
					if (documentoPadre.hasSchema(esquema)
							&& esEsquemaValidoEnPropagacion(esquema,
									ESQUEMAS_PROPAGACION_ENTIDADES)) {
						try {
							copiarEsquema(documentoPadre, documento, esquema);
							session.saveDocument(documento);
						} catch (ClientException e) {
							LOG.error("Imposible propagar el esquema "
									+ esquema, e);
						}
					}
				}
			}
		}
	}

	/**
	 * Comprueba la validez de un esquema para la propagacion.
	 * 
	 * @param esquema
	 *            a comprobar
	 * @param esquemasValidos
	 *            array de esquemas validos en propagacion
	 * @return true si el esquema es valido, en otro caso false
	 */
	private boolean esEsquemaValidoEnPropagacion(String esquema,
			String[] esquemasValidos) {
		return Arrays.asList(esquemasValidos).contains(esquema);
	}

	/**
	 * Copia el esquema de metadatos del documento origen al destino.
	 * 
	 * @param origen
	 *            es el documento origen
	 * @param destino
	 *            es el documento destino
	 * @param esquema
	 *            es el esquema a copiar
	 * @throws ClientException
	 *             en caso de error de copia
	 */
	private void copiarEsquema(DocumentModel origen, DocumentModel destino,
			String esquema) throws ClientException {
		if (origen == null || destino == null) {
			throw new IllegalArgumentException(
					"Es imposible copiar el esquema por que el documento origen o destino es nulo");
		}
		if (destino.hasSchema(esquema)) {
			// Obtenemos propiedades (metadados) del padre
			Map<String, Object> propiedadesOrigen = origen
					.getProperties(esquema);
			for (Map.Entry<String, Object> propiedadEntry : propiedadesOrigen
					.entrySet()) {
				destino.setProperty(esquema, propiedadEntry.getKey(),
						propiedadEntry.getValue());
			}
		}

	}
}
