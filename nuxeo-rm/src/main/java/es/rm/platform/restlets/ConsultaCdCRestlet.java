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

package es.rm.platform.restlets;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.List;


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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.ecm.platform.ui.web.tag.fn.LiveEditConstants;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.nuxeo.ecm.core.api.IdRef;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.w3c.dom.Element;

import es.rm.platform.api.utils.ArchivoConstantes;

/**
 * Restlet que devuelve el cuadro de clasificaci&oacute;n.
 */
@Name("getCdC")
@Scope(EVENT)
public class ConsultaCdCRestlet extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {

	/**
	 * Atributo de serializacion.
	 */
	private static final long serialVersionUID = 2479047833509081515L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(ConsultaCdCRestlet.class);

	/**
	 * Bean de navegaci&oacute;n.
	 */
	@In(create = true)
	private NavigationContext navigationContext;

	/**
	 * Sesi&oacute;n Core.
	 */
	private CoreSession documentManager;

	/**
	 * Manejador principal del restlet. 
	 * 
	 * @param req
	 *            Request
	 * @param res
	 *            Response
	 */
	@Override
	public void handle(Request req, Response res) {
		/* Conflicto con Checkstyle. No se pueden declarar como final los m&eacute;todos de
		 * beans EJB que hagan uso de dependencias inyectadas, ya que dichas
		 * dependencias toman el valor null.
		 * No se declara como final debido a que en ese caso 
		 * la inyecci&oacute;n de dependencias dejar&iacute;a de funcionar.
		 */
		String repo = (String) req.getAttributes().get("repo");
		String cdcDocId = (String) req.getAttributes().get("docid");
		if (cdcDocId == null) {
			handleError(res, "you must specify a CdC source document Id.");
		} else {
			String depth = getQueryParamValue(req, "depth", "1");

			if (repo == null || repo.equals("*")) {
				handleError(res, "you must specify a repository");
			} else {

				int profundidad = Integer.parseInt(depth);
				// String cdcPath =
				// Framework.getProperty(ArchivoConstantes.PROPIEDAD_CDC_PATH);
				DOMDocumentFactory domFactory = new DOMDocumentFactory();
				DOMDocument result = (DOMDocument) domFactory.createDocument();
				try {

					navigationContext
							.setCurrentServerLocation(new RepositoryLocation(
									repo));
					documentManager = navigationContext
							.getOrCreateDocumentManager();
					DocumentModel cdcRoot = documentManager
							.getDocument(new IdRef(cdcDocId));
					if (cdcRoot != null) {
						Element current = result.createElement("document");

						current.setAttribute("title", cdcRoot.getTitle());
						current.setAttribute("type", cdcRoot.getType());
						current.setAttribute("id", cdcRoot.getId());
						current.setAttribute("path", cdcRoot.getPathAsString());

						result.setRootElement((org.dom4j.Element) current);

						addChildren(result, current, cdcRoot, profundidad);
					} else {
						Element noEncontrado = result
								.createElement("cdCNoRegistrado");
						noEncontrado.setAttribute("variable",
								ArchivoConstantes.PROPIEDAD_CDC_PATH);
						noEncontrado.setAttribute("valor", cdcDocId);
						result.setRootElement((org.dom4j.Element) noEncontrado);
						LOG.error("No se ha configurado la ruta del CdC; por favor configure la ruta en la propiedad "
								+ ArchivoConstantes.PROPIEDAD_CDC_PATH);
					}

					res.setEntity(result.asXML(), MediaType.TEXT_XML);
					res.getEntity().setCharacterSet(CharacterSet.UTF_8);

				}
				/**
				 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
				 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
				 * excepci&oacute;n. En caso contrario, este c&oacute;digo no compilar&iacute;a
				 */
				catch (Exception e) {
					LOG.error(e);
					handleError(res, e);
				}
			}
		}

	}

	/**
	 * A&ntilde;ade un hijo al arbol.
	 * 
	 * @param result
	 *            XML resultado
	 * @param parent
	 *            Elemento padre del xml
	 * @param root
	 *            Documento ra&iacute;z.
	 * @param depth
	 *            profundidad
	 * @return devuelve un elemento XML
	 */
	private Element addChildren(DOMDocument result, Element parent,
			DocumentModel root, int depth) {
		try {

			List<DocumentModel> hijos = documentManager.getChildren(root
					.getRef());

			for (DocumentModel documento : hijos) {
				String estado = documento.getCurrentLifeCycleState();
				if (!"deleted".equals(estado) && documento.isFolder()) {
					Element hijo = result.createElement("document");

					hijo.setAttribute("title", documento.getTitle());
					hijo.setAttribute("type", documento.getType());
					hijo.setAttribute("id", documento.getId());

					parent.appendChild(hijo);

					if (depth > ArchivoConstantes.NUMERO_UNO) {
						addChildren(result, hijo, documento, depth
								- ArchivoConstantes.NUMERO_UNO);
					}
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo a&ntilde;adir elementos al xml ", e);
		}
		return parent;
	}

}
