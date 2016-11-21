package org.athento.nuxeo.rm.api.utils;

import java.security.Principal;
import java.util.Arrays;


/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.Access;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.lifecycle.LifeCycleException;
import org.nuxeo.ecm.core.model.Document;
import org.nuxeo.ecm.core.query.sql.model.SQLQuery;
import org.nuxeo.ecm.core.security.AbstractSecurityPolicy;

/**
 * Pol&iacute;tica de seguridad para el RM.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
public class ArchivoPoliticaSeguridad extends AbstractSecurityPolicy {
	
	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(ArchivoPoliticaSeguridad.class);
	
	/**
	 * M&eacute;todo principal para comprobar permisos.
	 * 
	 * @param doc documento a analizar
	 * @param mergedAcp listado de permisos
	 * @param principal usuario logueado
	 * @param permission permiso a comprobar
	 * @param resolvedPermissions permisos herederos
	 * @param additionalPrincipals usuarios adicionales
	 * @return permisos sobre el documento
	 */
	public final Access checkPermission(Document doc, ACP mergedAcp,
			Principal principal, String permission,
			String[] resolvedPermissions, String[] additionalPrincipals) {
		Access access = null;
		
		if (resolvedPermissions == null
				|| !Arrays.asList(resolvedPermissions).contains(
						SecurityConstants.WRITE)) {
			access = Access.UNKNOWN;
		}else{
		
			Document document = doc;
			
			if(doc.getType().getName().equals(ArchivoConstantes.TIPO_DOCUMENTO_SIMPLE)){
				LOG.info("Documento Simple...");
				try {
					document = doc.getParent();
				} catch (DocumentException e) {
					LOG.error("Error extrayendo parent: ", e);
				}
			}
	
			if(document.getType().getName().equals(ArchivoConstantes.TIPO_EXPEDIENTE)){
				try {
					String estado = document.getLifeCycleState();
					// FIXME: ¿SOLO FASE DE VIGENCIA O A PARTIR DE FASE DE VIGENCIA?
					if(estado.equals(ArchivoConstantes.FASE_VIGENCIA)){
						LOG.info("denegando escritura...");
						access = Access.DENY;
					}
				} catch (LifeCycleException e) {
					LOG.error("Error extrayendo ciclo de vida: ", e);
				}
			}
		}
		return access;
	}
	
	/**
	 * Restringe el permiso Browse.
	 * 
	 * @param permission permiso a comprobar
	 * @return true en caso de permiso "Browse"; false en caso contrario
	 */
	@Override
	public final boolean isRestrictingPermission(String permission) {
		assert "Browse".equals(permission); 
		return false;
	}
	
	/**
	 * Devuelve si es usable en una expresi&oacute;n o query.
	 * 
	 * @return true en cualquier caso
	 */
	@Override
	public final boolean isExpressibleInQuery() {
		return true;
	}
	
	/**
	 * Devuelve el 'Transformer' Identidad de Nuxeo (por defecto).
	 * 
	 * @return devuelve el 'Transformer' Identidad
	 */
	@Override
	public final SQLQuery.Transformer getQueryTransformer() {
		return SQLQuery.Transformer.IDENTITY;
	}

}
