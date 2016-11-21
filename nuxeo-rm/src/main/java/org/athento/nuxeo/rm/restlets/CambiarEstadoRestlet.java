/**
 * (C) Copyright 2005-2009 Yerbabuena Software <http://www.yerbabuena.es>
 * Authors: vs@athento.com (Yerbabuena Software)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * $Id$
 */

package org.athento.nuxeo.rm.restlets;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.ecm.platform.ui.web.tag.fn.LiveEditConstants;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Restlet que se encarga de cambiar el estado de un documento.
 */
@Name("changeState")
@Scope(EVENT)
public class CambiarEstadoRestlet extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {

	/**
	 * Atributo de serializacion.
	 */
	private static final long serialVersionUID = -622641023265086737L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory
			.getLog(CambiarEstadoRestlet.class);

	/**
	 * Contexto de navegacion.
	 */
	@In(create = true)
	private transient NavigationContext navigationContext;

	/**
	 * Interfaz del servicio de documentos.
	 */
	private transient CoreSession documentManager;

	/**
	 * Retrieve the arguments of the restlet and returns a XML with the result.
	 * 
	 *  
	 * @param req
	 *            the req
	 * @param res
	 *            the res
	 */
	@Override
	public void handle(Request req, Response res) {
		/* Conflicto con Checkstyle. No se pueden declarar como final los m&eacute;todos de
		 * beans EJB que hagan uso de dependencias inyectadas, ya que dichas
		 * dependencias toman el valor null. 
		 * No se declara como final debido a que en ese caso la inyecci&oacute;n de
		 * dependencias dejar&iacute;a de funcionar.
		 */
		// Getting the repository and the document id of the document
		String repo = (String) req.getAttributes().get("repo");
		String docid = (String) req.getAttributes().get("docid");
		String transition = (String) req.getAttributes().get("transition");

		DocumentModel dm = null;
		try {
			if (repo == null || repo.equals("*")) {
				handleError(res, "Repositorio no especificado.");
				throw new ClientException("Repositorio no especificado.");
			}

			// Getting the document...
			navigationContext.setCurrentServerLocation(new RepositoryLocation(
					repo));
			documentManager = navigationContext.getOrCreateDocumentManager();
			if (docid == null || docid.equals("*")) {
				handleError(res, "dentificador de documento no especificado");
				throw new ClientException("Identificador de documento no"
						+ "especificado.");
			} else {
				dm = documentManager.getDocument(new IdRef(docid));
			}
			if (!dm.followTransition(transition)) {
				handleError(res, "Transicion incorrecta");
				throw new ClientException("Transicion incorrecta.");
			}
			documentManager.save();
			String finalState = dm.getCurrentLifeCycleState();
			// build the XML response document holding the ref
			DOMDocumentFactory domfactory = new DOMDocumentFactory();
			DOMDocument resultDocument = (DOMDocument) domfactory
					.createDocument();
			Element docCreatedElement = resultDocument
					.addElement("documentNewState");
			docCreatedElement.addAttribute("newState", finalState);
			res.setEntity(resultDocument.asXML(), MediaType.TEXT_XML);

		} catch (ClientException e) {
			LOG.info("[RESTLET]Error en cambiando el estado del documento.", e);
			handleError(res, e);
			return;
		}

	}
}