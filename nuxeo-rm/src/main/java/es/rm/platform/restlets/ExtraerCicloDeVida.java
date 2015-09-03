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
import java.util.Collection;












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
import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.lifecycle.LifeCycle;
import org.nuxeo.ecm.core.lifecycle.LifeCycleService;
import org.nuxeo.ecm.core.lifecycle.LifeCycleState;
import org.nuxeo.ecm.core.lifecycle.LifeCycleTransition;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.ecm.platform.ui.web.tag.fn.LiveEditConstants;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.w3c.dom.Element;

/**
 * 
 * Restlet para la extracci&oacute;n del ciclo de vida.
 * 
 */
@Name("lifeCyclePolicy")
@Scope(EVENT)
public class ExtraerCicloDeVida extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {
	
	/**
	 * Constante 'destinationState'.
	 */
	private static final String DESTINATION_STATE = "destinationState";
	
	/**
	 * Constante 'transition'.
	 */
	private static final String TRANSITION = "transition";
	
	/**
	 * Constante 'transitions'.
	 */
	private static final String TRANSITIONS = "transitions";
	
	/**
	 * Constante 'allowedTransition'.
	 */
	private static final String ALLOWED_TRANSITION = "allowedTransition";
	
	/**
	 * Constante 'allowedTransitions'.
	 */
	private static final String ALLOWED_TRANSITIONS = "allowedTransitions";
	
	/**
	 * Constante 'state'.
	 */
	private static final String STATE = "state";
	
	/**
	 * Constante 'states'.
	 */
	private static final String STATES = "states";
	
	/**
	 * Constante 'lifeCycle'.
	 */
	private static final String LIFE_CYCLE = "lifeCycle";

	/**
	 * Constante '*'.
	 */
	private static final String AN_OBJECT = "*";
	
	/**
	 * Constante 'docid'.
	 */
	private static final String DOCID = "docid";
	
	/**
	 * Constante 'repo'.
	 */
	private static final String REPO = "repo";

	/**
	 * Constante name.
	 */
	private static final String NAME = "name";

	/**
	 * Version Serial.
	 */
	private static final long serialVersionUID = -5354813433902271188L;

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(ExtraerCicloDeVida.class);

	/**
	 * Contexto de navegacion.
	 */
	@In(create = true)
	private transient NavigationContext navigationContext;

	/**
	 * Interfaz de la implementaci&oacute;n de la sesi&oacute;n.
	 */
	private transient CoreSession documentManager;

	/**
	 * Interfaz del servicio de ciclos de vida.
	 */
	private transient LifeCycleService lifeCycleService;

	/**
	 * Manejador principal del restlet. 
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
		 * dependencias toman el valor null. No se declara como final debido a que en
		 * ese caso la inyecci&oacute;n de dependencias dejar&iacute;a de funcionar.
		 */
		// Getting the repository and the document id of the document
		String repo = (String) req.getAttributes().get(REPO);
		String docid = (String) req.getAttributes().get(DOCID);

		DocumentModel dm = null;
		try {
			if (repo == null || repo.equals(AN_OBJECT)) {
				handleError(res, "Repositorio no especificado.");
				throw new ClientException("Repositorio no especificado.");
			}

			// Getting the document...
			navigationContext.setCurrentServerLocation(new RepositoryLocation(
					repo));
			documentManager = navigationContext.getOrCreateDocumentManager();
			if (docid == null || docid.equals(AN_OBJECT)) {
				handleError(res, "identificador de documento no especificado");
				throw new ClientException("Identificador de documento no"
						+ "especificado.");
			} else {
				dm = documentManager.getDocument(new IdRef(docid));
			}

			lifeCycleService = NXCore.getLifeCycleService();
			String lifeCycleName = dm.getLifeCyclePolicy();

			LOG.debug("Ciclo de vida asociado : " + lifeCycleName);

			LifeCycle lifeCycle = lifeCycleService
					.getLifeCycleByName(lifeCycleName);
			Collection<LifeCycleState> states = lifeCycle.getStates();
			Collection<LifeCycleTransition> transitions = lifeCycle
					.getTransitions();

			DOMDocumentFactory domFactory = new DOMDocumentFactory();
			DOMDocument result = (DOMDocument) domFactory.createDocument();

			Element current = result.createElement(LIFE_CYCLE);
			current.setAttribute(NAME, lifeCycleName);
			result.setRootElement((org.dom4j.Element) current);

			Element statesElement = result.createElement(STATES);

			for (LifeCycleState state : states) {
				String stateName = state.getName();
				Element stateElement = result.createElement(STATE);
				stateElement.setAttribute(NAME, stateName);
				Element transitionsElement = result
						.createElement(ALLOWED_TRANSITIONS);
				Collection<String> allowedTransitions = state
						.getAllowedStateTransitions();
				for (String transitionName : allowedTransitions) {
					Element transitionElement = result
							.createElement(ALLOWED_TRANSITION);
					transitionElement.setAttribute(NAME, transitionName);
					transitionsElement.appendChild(transitionElement);
				}
				stateElement.appendChild(transitionsElement);
				statesElement.appendChild(stateElement);
			}
			current.appendChild(statesElement);

			Element transitionsListElement = result
					.createElement(TRANSITIONS);

			for (LifeCycleTransition transition : transitions) {
				Element transitionElement = result.createElement(TRANSITION);
				transitionElement.setAttribute(NAME, transition.getName());
				transitionsListElement.appendChild(transitionElement);
				transitionElement.setAttribute(DESTINATION_STATE,
						transition.getDestinationStateName());
			}

			current.appendChild(transitionsListElement);

			res.setEntity(result.asXML(), MediaType.TEXT_XML);
			res.getEntity().setCharacterSet(CharacterSet.UTF_8);

		} catch (ClientException e) {
			LOG.info(
					"[RESTLET]Error consultando el ciclo de vida del documento.",
					e);
			handleError(res, e);
		}
		/**
		 * Conflicto con checkstyle. Es necesario capturar la excepci&oacute;n
		 * Exception, dado que el c&oacute;digo nativo de Nuxeo lanza dicha excepci&oacute;n.
		 * En caso contrario, este c&oacute;digo no compilar&iacute;a
		 */
		catch (Exception e) {
			LOG.error("[RESTLET] Error en la ejecuci&oacute;n del restlet ", e);
			handleError(res, e);
		}
	}

}
