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

import static es.rm.platform.api.utils.ArchivoConstantes.NUMERO_UNO;
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
import org.nuxeo.ecm.platform.audit.api.AuditException;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.api.Logs;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.ecm.platform.ui.web.tag.fn.LiveEditConstants;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Retlet que devuelve el historial de un documento.
 */
@Name("getDocumentHistory")
@Scope(EVENT)
public class HistoricoDocumentoRestlet extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2479047833509081515L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(HistoricoDocumentoRestlet.class);

	/**
	 * Bean de navegaci&oacute;n de Nuxeo.
	 */
	@In(create = true)
	private NavigationContext navigationContext;

	/**
	 * Sesi&oacute;n.
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
		 * dependencias toman el valor null. No se declara como final debido a que en
		 * ese caso la inyecci&oacute;n de dependencias dejar&iacute;a de funcionar.
		 */
		// Getting the repository and the document id of the document
		String repo = (String) req.getAttributes().get("repo");
		String docid = (String) req.getAttributes().get("docid");

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

			documentManager.save();
			List<LogEntry> docHistory = this.computeLogEntries(dm);
			// build the XML response document holding the ref
			DOMDocumentFactory domfactory = new DOMDocumentFactory();
			DOMDocument resultDocument = (DOMDocument) domfactory
					.createDocument();
			Element docCreatedElement = resultDocument.addElement("LogEntries");
			for (LogEntry logEntry : docHistory) {
				Element logEnt = docCreatedElement.addElement("LogEntry");
				logEnt.addAttribute("category", logEntry.getCategory());
				logEnt.addAttribute("action", logEntry.getEventId());
				logEnt.addAttribute("date", logEntry.getEventDate().toString());
				logEnt.addAttribute("user", logEntry.getPrincipalName());
				String comment = "";
				if (logEntry.getComment() != null) {
					comment = logEntry.getComment().toString();
				}
				logEnt.addAttribute("comment", comment);

			}
			res.setEntity(resultDocument.asXML(), MediaType.TEXT_XML);

		} catch (ClientException e) {
			LOG.info("[RESTLET]Error en cambiando el estado del documento.", e);
			handleError(res, e);
			return;
		}

	}

	/**
	 * Devuelve una lista de entradas de Logs.
	 * 
	 * @param document
	 *            Documento a analizar
	 * @return listado de logs.
	 * @throws AuditException
	 *             excepci&oacute;n lanzada en caso de error.
	 */
	private List<LogEntry> computeLogEntries(DocumentModel document)
			throws AuditException {
		List<LogEntry> logEntries = null;
		if (document != null) {
			try {
				Logs service;
				service = Framework.getService(Logs.class);
				Logs logsBean = service;

				String query = "";

				query = new StringBuffer().append("log.docUUID like ")
						.append(document.getId()).toString();
				query = new StringBuffer()
						.append("order by log.eventDate desc").toString();
				// query += "log.docUUID like '" + document.getId() + "'";
				// query += " order by log.eventDate desc";
				logEntries = logsBean.nativeQueryLogs(query, NUMERO_UNO,
						Integer.MAX_VALUE);
			}
			/**
			 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
			 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha
			 * excepci&oacute;n. En caso contrario, este c&oacute;digo no compilar&iacute;a
			 */
			catch (Exception e) {
				String message = "An error occurred while grabbing log entries for "
						+ document.getId();
				throw new AuditException(message, e);
			}
		}
		return logEntries;
	}
}