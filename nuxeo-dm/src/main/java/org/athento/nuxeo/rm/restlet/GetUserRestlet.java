package org.athento.nuxeo.rm.restlet;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.List;

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
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
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

@Name("getUserRestlet")
@Scope(EVENT)
public class GetUserRestlet extends BaseNuxeoRestlet implements
		LiveEditConstants, Serializable {

	private static final long serialVersionUID = -7223939557577366747L;

	@In(create = true)
	protected transient NavigationContext navigationContext;

	private static Log log = LogFactory.getLog(GetUserRestlet.class);

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
				handleError(res, "you must specify an user to search");
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
			DocumentModel userModel = null;
			if (user != null && !user.isEmpty()) {
				userModel = userMgr.getUserModel(user);
			}

			// build the XML response document holding the ref
			DOMDocumentFactory domfactory = new DOMDocumentFactory();
			DOMDocument resultDocument = (DOMDocument) domfactory
					.createDocument();

			if (userModel == null) {
				handleError(res, "User is not found");
				return;
			}

			NuxeoPrincipal principal = userMgr.getPrincipal((String) userModel
					.getProperty("user", "username"));
			Element userElement = resultDocument.addElement("user");
			userElement.addAttribute("username", principal.getName());
			userElement.addAttribute("firstName", principal.getFirstName());
			userElement.addAttribute("lastName", principal.getLastName());
			userElement.addAttribute("email",
					(String) userModel.getProperty("user", "email"));
			userElement.addAttribute("company", principal.getCompany());

			List<String> groups = principal.getAllGroups();
			if (groups != null && groups.size() > 0) {
				for (String groupName : groups) {
					Element groupElement = userElement.addElement("group");
					groupElement.addAttribute("name", groupName);
				}
			}

			Representation rep = new StringRepresentation(
					resultDocument.asXML(), MediaType.APPLICATION_XML);
			rep.setCharacterSet(CharacterSet.UTF_8);
			res.setEntity(rep);
		} catch (ClientException e) {
			handleError(res, e);
		}
	}
}