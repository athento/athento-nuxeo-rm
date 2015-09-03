package es.rm.platform.document.test;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

/**
 * Test para la comprobacion de los tipos de documentos correctos del rm.
 * 
 * @author Victor Sanchez
 * 
 */
public class TestRMDocumentType extends SQLRepositoryTestCase {
	
	/**
	 * Constante fondo.
	 */
	private static final String FONDO2 = "fondo";

	/**
	 * Método setUp del test.
	 * 
	 * Conflicto con checkstyle. El método debe lanzar la excepción Exception ya
	 * que hereda de una clase externa que la declara.
	 * 
	 * @exception Exception
	 *                excepción lanzada en caso de error
	 */
	@Override
	public final void setUp() throws Exception {
//		super.setUp();
//		deployBundle("es.rm.platform.dm");
//		openSession();
	}

	/**
	 * Test Base Document Type.
	 * 
	 * Conflicto con checkstyle. El método debe lanzar la excepción Exception ya
	 * que hereda de una clase externa que la declara.
	 * 
	 * @throws Exception
	 *             excepcion lanzada en caso de error
	 */
	public final void testBaseDocumentType() throws Exception {

//		// Creamos documento y comprobamos la propagacion del documento padre.
//		DocumentModel grupoDeFondo = session
//				.createDocumentModel("GrupoDeFondo");
//		grupoDeFondo.setProperty(FONDO2, "DenominacionEstado",
//				"GRUPO_FONDO_DENOMINACION");
//		grupoDeFondo.setProperty(FONDO2, "Cantidad", "100");
//
//		grupoDeFondo = session.saveDocument(grupoDeFondo);
//
//		// Creamos documento y comprobamos la propagacion del documento padre.
//		DocumentModel fondo = session.createDocumentModel("Fondo");
//		fondo.setProperty(FONDO2, "ClasificacionSeguridad", "SEGURIDAD");
//
//		fondo = session.saveDocument(fondo);
//
//		// Creamos documento y comprobamos la propagacion del documento padre.
//		DocumentModel serie = session.createDocumentModel("Serie");
//		serie.setProperty("serie", "Jurisdiccion", "NACIONAL");
//
//		serie = session.saveDocument(serie);
//		session.save();
//
//		// Comprobamos metadatos
//		fondo = session.getDocument(fondo.getRef());
//		assertTrue(fondo.getProperty(FONDO2, "Cantidad").equals("100"));

	}

}
