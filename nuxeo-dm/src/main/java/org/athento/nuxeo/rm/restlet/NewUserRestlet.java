package org.athento.nuxeo.rm.restlet;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMDocumentFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseNuxeoRestlet;
import org.nuxeo.ecm.platform.ui.web.tag.fn.LiveEditConstants;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.StringRepresentation;

@Name("newUserRestlet")
@Scope(EVENT)
public class NewUserRestlet extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {

	private static final long serialVersionUID = -2345345345477366747L;

	@In(create = true)
	protected transient NavigationContext navigationContext;

	private static Log log = LogFactory.getLog(NewUserRestlet.class);

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Request req, Response res) {
		UserManager userMgr = null;
		String user = null;
		try {

			userMgr = Framework.getService(UserManager.class);
			if (userMgr == null) {
				handleError(res, "User Manager can't be loaded");
				return;
			}

			user = (String) req.getAttributes().get("username");
			if (user == null) {
				handleError(res,
						"debe especificar el nombre usuario para la creacion");
				return;
			}
		} catch (ClientException e) {
			handleError(res, e);
			return;
		} catch (Exception e) {
			handleError(res, "User Manager can't be loaded");
			return;
		}

		try {

			DocumentModel newUserModel = userMgr.getBareUserModel();
			newUserModel.setPropertyValue("user:username", user);
			newUserModel.setPropertyValue("user:password", "_" + user);
			userMgr.createUser(newUserModel);

			// FIXME: Comprobar actualizacion del grupo
			NuxeoGroup grupoMiembros = userMgr.getGroup("members");
			if (grupoMiembros != null) {
				grupoMiembros.getMemberUsers().add(user);
				userMgr.updateGroup(grupoMiembros);
			}

			// build the XML response
			DOMDocumentFactory domfactory = new DOMDocumentFactory();
			DOMDocument resultDocument = (DOMDocument) domfactory
					.createDocument();
			Element docElement = resultDocument.addElement("user");
			docElement.addAttribute("username", user);

			Representation rep = new StringRepresentation(
					resultDocument.asXML(), MediaType.APPLICATION_XML);
			rep.setCharacterSet(CharacterSet.UTF_8);
			res.setEntity(rep);
		} catch (ClientException e) {
			handleError(res, e);
		}
	}

}