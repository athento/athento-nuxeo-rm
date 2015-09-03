package es.rm.platform.restlets;

import static org.jboss.seam.ScopeType.EVENT;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

import es.rm.platform.api.services.ArchivoServicio;

/**
 * 
 * Restlet para obtener una entidad Agente del Modelo.
 *
 */
@Name("obtenerAgente")
@Scope(EVENT)
public class ObtenerAgente extends ObtenerEntidadRestlet{
	
	/**
	 * Implementacion del restlet para obtener un agente. No se declara como
	 * final debido a que en ese caso la inyecci&oacute;n de dependencias dejar&iacute;a de
	 * funcionar.
	 * 
	 * @param docId Id del documento
	 * @return Document Model
	 * @throws Exception excepci&oacute;n lanzada por el m&eacute;todo.
	 */
	@Override
	public final DocumentModel extraerEntidad(String docId) throws Exception {
		/* Conflicto con checkstyle. El m&eacute;todo debe lanzar la excepci&oacute;n Exception ya
		 * que hereda de una clase externa que la declara.
		 */
		ArchivoServicio servicio = Framework.getService(ArchivoServicio.class);
		return servicio.obtenerAgente(getDocumentManager(), docId);
	}

}
