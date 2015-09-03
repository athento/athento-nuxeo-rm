package es.rm.platform.restlets;

import java.util.List;
import java.util.Map;



/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.w3c.dom.Element;
import org.yerbabuena.athento.ecm.utils.AthentoNXUtils;

import es.rm.platform.api.services.ArchivoServicio;
import es.rm.platform.api.utils.RepresentacionEMRMEsquema;
import es.rm.platform.api.utils.UtilidadesArchivo;

/**
 * 
 * Clase abstracta que implementa los m&eacute;todos b&aacute;sicos de consulta
 * a entidades del modelo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 * 
 */
public abstract class ObtenerEntidadRestlet extends BaseNuxeoRestlet {

	/**
	 * Logger.
	 */
	protected static final Log LOG = LogFactory
			.getLog(ObtenerEntidadRestlet.class);

	/**
	 * Interfaz de la implementaci&oacute;n de la sesi&oacute;n.
	 */
	private transient CoreSession documentManager;

	/**
	 * Servicio de archivo.
	 */
	private ArchivoServicio archivoServicio;

	/**
	 * Devuelve el core session.
	 * 
	 * @return el atributo documentManager.
	 */
	public final CoreSession getDocumentManager() {
		return documentManager;
	}

	/**
	 * Asigna una instancia al core session.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 */
	public final void setDocumentManager(CoreSession documentManager) {
		this.documentManager = documentManager;
	}

	/**
	 * Devuelve la instancia del servicio.
	 * 
	 * @return instancia del servicio de Archivo
	 */
	public final ArchivoServicio getArchivoServicio() {
		return archivoServicio;
	}

	/**
	 * Asigna una instancia al servicio.
	 * 
	 * @param archivoServicio
	 *            valor a asignar al servicio
	 */
	public final void setArchivoServicio(ArchivoServicio archivoServicio) {
		this.archivoServicio = archivoServicio;
	}

	/**
	 * Manejador principal del restlet.
	 * 
	 * @param req
	 *            the req
	 * @param res
	 *            the res
	 */
	@Override
	public final void handle(Request req, Response res) {
		// Extraemos el repositorio y el docId
		String repo = (String) req.getAttributes().get("repo");
		String docid = (String) req.getAttributes().get("docid");

		DocumentModel dm = null;
		try {
			if (repo == null || repo.equals("*")) {
				handleError(res, "Repositorio no especificado.");
				throw new ClientException("Repositorio no especificado.");
			}

			// Construimos el CoreSession
			documentManager = AthentoNXUtils.getCoreSession(repo);
			// Arrancamos el servicio
			archivoServicio = Framework.getService(ArchivoServicio.class);

			if (docid == null || docid.equals("*")) {
				handleError(res, "identificador de documento no especificado");
				throw new ClientException("Identificador de documento no"
						+ "especificado.");
			} else {
				dm = extraerEntidad(docid);
			}

			if (dm == null) {
				handleError(res,
						"El docId pasado no corresponde a ninguna entidad buscada: "
								+ docid);
				throw new ClientException("No existe una entidad con el docId "
						+ docid);
			}

			LOG.debug("Entidad con docId : " + docid + " encontrada");

			DOMDocument result = construirResultadoRestlet(dm);

			res.setEntity(result.asXML(), MediaType.TEXT_XML);
			res.getEntity().setCharacterSet(CharacterSet.UTF_8);

		} catch (ClientException e) {
			LOG.info("[RESTLET]Error consultando Entidad.", e);
			handleError(res, e);
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
		 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
		 * compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("[RESTLET] Error en la ejecuci&oacute;n del restlet ", e);
			handleError(res, e);
		} finally {
			if (documentManager != null) {
				CoreInstance.getInstance().close(documentManager);
			}
		}
	}

	/**
	 * Construye el XML de salida del restlet. No se declara como final debido a
	 * que en ese caso la inyecci&oacute;n de dependencias dejar&iacute;a de
	 * funcionar.
	 * 
	 * 
	 * @param entidad
	 *            Documento con los valores buscados.
	 * @return XML de salida del restlet.
	 * @throws ClientException
	 *             excepci&oacute;n lanzada
	 */
	public final DOMDocument construirResultadoRestlet(DocumentModel entidad)
			throws ClientException {
		List<RepresentacionEMRMEsquema> metadatos = UtilidadesArchivo
				.extraerMetadatosEMRM(entidad);

		DOMDocumentFactory domFactory = new DOMDocumentFactory();
		DOMDocument result = (DOMDocument) domFactory.createDocument();

		Element current = result.createElement("documento");
		current.setAttribute("docId", entidad.getId());
		current.setAttribute("titulo", entidad.getTitle());
		result.setRootElement((org.dom4j.Element) current);

		Element metadatasElement = result.createElement("metadatos");

		for (RepresentacionEMRMEsquema bloqueMetadatos : metadatos) {
			String esquema = bloqueMetadatos.getNombreEsquema();
			Element schemaElement = result.createElement("esquema");
			schemaElement.setAttribute("nombre", esquema);

			Map<String, Object> campos = bloqueMetadatos.getCampos();

			for (String clave : campos.keySet()) {
				Object valor = campos.get(clave);

				if (valor == null) {
					valor = "";
				}

				Element metadato = result.createElement("metadato");
				metadato.setAttribute("nombre", clave);
				metadato.setAttribute("Valor", valor.toString());
				schemaElement.appendChild(metadato);
			}

			metadatasElement.appendChild(schemaElement);
		}

		current.appendChild(metadatasElement);

		return result;
	}

	/**
	 * M&eacute;todo a implementar por las clases herederas que definir&aacute;
	 * qu&eacute; tipo de entidad estamos buscando. No se declara como final
	 * debido a que en ese caso la inyecci&oacute;n de dependencias
	 * dejar&iacute;a de funcionar.
	 * 
	 * @param docId
	 *            id del documento a buscar
	 * @return Documento que cumpla con las condiciones buscadas.
	 * @throws Exception
	 *             excepci&oacute;n lanzada
	 */
	public abstract DocumentModel extraerEntidad(String docId) throws Exception;
	/*
	 * Conflicto con checkstyle. Es necesario lanzar la excepci&oacute;n
	 * Exception, dado que el c&oacute;digo nativo de Nuxeo importado usa esa
	 * excepci&oacute;n. En caso contrario, este c&oacute;digo no
	 * compilar&iacute;a
	 */

}
