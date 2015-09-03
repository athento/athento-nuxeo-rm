var prefs = new gadgets.Prefs();

var requestScope = getTargetContextPath();
// configure Automation REST call
var NXRequestParams = {
		operationId: 'Document.PageProvider',
		operationParams: {
			providerName: 'user_series',
			pageSize: 10,
			queryParams: requestScope
		},
		operationContext: {},
		operationDocumentProperties: "common,dublincore",
		entityType: 'documents',
		usePagination: true,
		displayMethod: displayDocumentList,
		displayColumns: [
		                 {type: 'builtin', field: 'icon'},
		                 {type: 'builtin', field: 'titleWithLink', label: '__MSG_label.dublincore.title__'},
		                 {type: 'date', field: 'dc:modified', label: '__MSG_label.dublincore.modified__'},
		                 {type: 'text', field: 'dc:creator', label: '__MSG_label.dublincore.creator__'}
		                 ],
		                 noEntryLabel: '__MSG_label.gadget.no.document__'
};

// execute automation request onload
gadgets.util.registerOnLoadHandler(function() {
	initContextPathSettingsButton();
	doAutomationRequest(NXRequestParams);
});

function getTargetContextObject() {
	return 'Actividad';
}

function availableContextsReceived(entries, nxParams) {

	var elSel = _gel("contextPathChooser");

	var selectedValue = getTargetContextPath();

	for (var i = 0; i < entries.length; i++) {

		var elOptNew = document.createElement('option');
		elOptNew.text = entries[i].title;
		elOptNew.value = entries[i].uid;
		if (elOptNew.value == selectedValue) {
			elOptNew.selected = true;
		}
		try {
			elSel.add(elOptNew, null); // standards compliant; doesn't work in IE
		}
		catch(ex) {
			elSel.add(elOptNew); // IE only
		}
	}
	contextListLoaded = true;
	showContextPathSelector();
}