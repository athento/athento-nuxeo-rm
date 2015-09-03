function reloadPageAfterDeleteCdC(result) {
	document.location.reload();
}

/** Funcion para JS en menu contextual para la eliminacion de un calendario. */
function doCdCDelete(docid) {
	if (confirmDeleteDocuments()) {
		Seam.Component.getInstance("archivoBean").eliminarCalendario(docid,
				reloadPageAfterDeleteCdC);
	}
}