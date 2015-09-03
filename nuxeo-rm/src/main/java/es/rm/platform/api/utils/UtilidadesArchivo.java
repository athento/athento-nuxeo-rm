package es.rm.platform.api.utils;

import static es.rm.platform.api.utils.ArchivoConstantes.CALENDARIO_CONSERVACION;
import static es.rm.platform.api.utils.ArchivoConstantes.DOC_ID;
import static es.rm.platform.api.utils.ArchivoConstantes.OBJETOS_ASOCIADOS_CALENDARIO;
import static es.rm.platform.api.utils.ArchivoConstantes.UNCHECKED_WARNING;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Conflicto con checkstyle. Es necesario usar el logger de Apache.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * --------------------------------------------------------
 */
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.ListDiff;
import org.nuxeo.ecm.core.api.VersionModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;
import org.yerbabuena.athento.ecm.utils.AthentoNXUtils;
import org.yerbabuena.athento.ecm.utils.vocabularies.AthentoNXVocabularies;

import es.rm.platform.api.services.ArchivoServicio;

/**
 * Clase con m&eacute;todos &uacute;tiles.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
public class UtilidadesArchivo {

	/**
	 * Logger.
	 */
	private static final Log LOG = LogFactory.getLog(UtilidadesArchivo.class);

	/**
	 * Devuelve una lista con una representacion de todos los esquemas EMDRM y
	 * sus metadatos.
	 * 
	 * @param documento
	 *            es el modelo documental de Nuxeo a examinar
	 * @return Listado de Metadatos EMDRM
	 * @throws ClientException
	 *             excepci&oacute;n lanzada en caso de error
	 */
	public static List<RepresentacionEMRMEsquema> extraerMetadatosEMRM(
			final DocumentModel documento) throws ClientException {

		List<RepresentacionEMRMEsquema> metadatos = new ArrayList<RepresentacionEMRMEsquema>();
		String[] esquemas = documento.getSchemas();
		if (esquemas != null) {
			for (String esquema : esquemas) {
				if (esEsquemaEMRM(esquema)) {
					Map<String, Object> campos = documento
							.getProperties(esquema);
					RepresentacionEMRMEsquema bloqueMetadatos = new RepresentacionEMRMEsquema(
							esquema, campos);
					metadatos.add(bloqueMetadatos);
				}
			}
		}
		return metadatos;
	}

	/**
	 * Comprueba si un esquema es EMRM o no.
	 * 
	 * @param nombreEsquema
	 *            es el nombre del esquema
	 * @return true en caso de ser esquema EMRM; false en caso contrario
	 */
	public static boolean esEsquemaEMRM(final String nombreEsquema) {
		return nombreEsquema.startsWith("emrm_");
	}

	/**
	 * Nueva instancia de Gregorian Calendar.
	 * 
	 * @return new GregorianCalendar
	 */
	public static GregorianCalendar getNewGregorianCalendar() {
		return new GregorianCalendar();
	}

	/**
	 * Nueva instancia de una lista de String vacia.
	 * 
	 * @return nueva lista vac&iacute;a de String
	 */
	public static List<String> getNuevaListaVacia() {
		return new ArrayList<String>();
	}

	/**
	 * Referencia de Nuxeo desde docId.
	 * 
	 * @param docId
	 *            id del documento
	 * @return Nueva referencia id
	 */
	public static IdRef getNuevaReferenciaId(final String docId) {
		return new IdRef(docId);
	}

	/**
	 * Nueva Lista de documentos.
	 * 
	 * @return Nueva lista de documentos vac&iacute;a
	 */
	public static DocumentModelList getNuevaListaDocumentos() {
		return new DocumentModelListImpl();
	}

	/**
	 * Finaliza el periodo de retenci&oacute;n de un calendario.
	 * 
	 * @param sesion
	 *            instancia de la sesi&oacute;n
	 * @param documento
	 *            documento calendario
	 * @throws Exception
	 *             excepci&oacute;n lanzada en caso de error
	 */
	public static void finalizarPeriodoDeRetencion(final CoreSession sesion,
			final DocumentModel documento) throws Exception {
		/*
		 * Conflicto con checkstyle. El m&eacute;todo debe lanzar la
		 * excepci&oacute;n Exception ya que usa objetos externos que la
		 * declaran.
		 */
		ArchivoServicio servicio = Framework.getService(ArchivoServicio.class);
		servicio.finalizarPeriodoRetencion(sesion, documento);
	}

	/**
	 * Devuelve la etiqueta de un vocabulario.
	 * 
	 * @param vocabulario
	 *            nombre del vocabulario
	 * @param campo
	 *            campo del vocaublario
	 * @return etiqueta del vocabulario
	 * @throws Exception
	 *             excepcion lanzada en caso de error
	 */
	public static String getEntryLabelFromDirectory(final String vocabulario,
			final String campo) throws Exception {
		/*
		 * Conflicto con checkstyle. El m&eacute;todo debe lanzar la
		 * excepci&oacute;n Exception ya que usa objetos externos que la
		 * declaran.
		 */
		AthentoNXVocabularies vocabularios = new AthentoNXVocabularies();
		return vocabularios.getEntryLabelFromDirectory(vocabulario, campo);
	}

	/**
	 * Ejecuta transici&oacute;n del ciclo de vida.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            Documento de la entidad
	 * @param transicion
	 *            transici&oacute;n
	 */
	public static void executeTransition(final CoreSession documentManager,
			final DocumentModel carpeta, final String transicion) {
		AthentoNXUtils.executeTransition(documentManager, carpeta, transicion);
	}

	/**
	 * Actualiza campos de un documento.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param carpeta
	 *            documento carpeta
	 * @param esquema
	 *            esquema a actualizar
	 * @param propiedades
	 *            campos a actualizar
	 */
	public static void updateFields(final CoreSession documentManager,
			final DocumentModel carpeta, final String esquema,
			final Map<String, Object> propiedades) {
		AthentoNXUtils.updateFields(documentManager, carpeta, esquema,
				propiedades);
	}

	/**
	 * Calcula los d&iacute;as de diferencia entre dos fechas.
	 * 
	 * @param fechaInicio
	 *            fecha1
	 * @param fechaFin
	 *            fecha2
	 * @return fechaFin - fechaInicio (en d&iacute;as)
	 */
	public static int daysOfDifference(final Calendar fechaInicio, final Calendar fechaFin) {
		return AthentoNXUtils.daysOfDifference(fechaInicio, fechaFin);
	}

	/**
	 * Resta dos fechas.
	 * 
	 * @param fecha1
	 *            fecha 1
	 * @param fecha2
	 *            fecha 2
	 * @return fecha2 - fecha1
	 */
	public static int deductDates(final GregorianCalendar fecha1,
			final GregorianCalendar fecha2) {
		return AthentoNXUtils.deductDates(fecha1, fecha2);
	}

	/**
	 * Devuelve un documento del tipo de la entidad pasada como
	 * par&aacute;metro.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param entidades
	 *            tipos documentales que representan la entidad buscada
	 * @param docId
	 *            id del documento
	 * @return Documento de Nuxeo
	 */
	public static DocumentModel obtenerEntidad(final CoreSession documentManager,
			final List<String> entidades, final String docId) {
		DocumentModel entidadModel = null;
		try {
			DocumentModel documento = documentManager
					.getDocument(UtilidadesArchivo.getNuevaReferenciaId(docId));
			boolean esEntidad = false;
			if (documento != null) {
				int indice = 0;
				while (!esEntidad && indice < entidades.size()) {
					String entidad = entidades.get(indice);
					if (documento.getType().equals(entidad)) {
						entidadModel = documento;
						esEntidad = true;
					}
					indice++;
				}
			}
		} catch (ClientException e) {
			LOG.error("No se pudo encontrar un documento con docId: " + docId,
					e);
		}
		return entidadModel;
	}

	/**
	 * Extrae el modelo de la version.
	 * 
	 * @param documentManager
	 *            Interfaz del servicio de documentos.
	 * @param versionLabel
	 *            Etiqueta de la version.
	 * @param documento
	 *            El documento afectado.
	 * @return El modelo de la version.
	 */
	public static VersionModel extraerModeloVersion(
			final CoreSession documentManager, final String versionLabel,
			final DocumentModel documento) {
		VersionModel version = null;
		try {
			List<VersionModel> versiones = documentManager
					.getVersionsForDocument(documento.getRef());
			for (VersionModel vers : versiones) {
				if (vers.getLabel().equals(versionLabel)) {
					version = vers;
				}
			}
		} catch (ClientException e) {
			LOG.error("Ha ocurrido un error al extraer el "
					+ "modelo de la version.", e);
		}
		return version;
	}

	/**
	 * Calcula la fecha en la que se debe acabar el periodo de vigencia en
	 * funci&oacute;n de los valores de los datos de a&ntilde;o, meses y
	 * d&iacute;as del calendario de conservaci&oacute;n asociado.
	 * 
	 * @param calendario
	 *            Documento que representa el calendario
	 * @param fechaCierre
	 *            Fecha de cierre
	 * @param  fase fase actual
	 * @param itemFase
	 *            es la posicion de la fase para calcular el delta de
	 *            incrementos para el lanzamiento de triggers de ejecucion
	 * @return Fecha de fin de Retencion
	 */
	@SuppressWarnings({ UNCHECKED_WARNING })
	public static Calendar calcularFechaFinRetencion(final DocumentModel calendario,
			final Calendar fechaCierre, final String fase, final int itemFase) {
		// Obtenemos los datos de la fase para el calculo
		Map<String, Object> faseParaCalculo = buscarDatosFase(calendario, fase);
		Calendar result = null;
		if (faseParaCalculo != null) {
			Map<String, Long> periodoVigencia = (Map<String, Long>) faseParaCalculo
					.get(ArchivoConstantes.CAMPO_CALCON_FASE_PERIODO);
			if (periodoVigencia != null) {
				// Obtenemos anyos
				Long anos = Long.valueOf(0);
				try {
					anos = periodoVigencia
							.get(ArchivoConstantes.CAMPO_CALCON_FASE_PERIODO_ANOS);
				} catch (Exception ex) {
					LOG.error("Error obteniendo el año:", ex);
				}
				// Obtenemos meses
				Long meses = Long.valueOf(0);
				try {
					meses = periodoVigencia
							.get(ArchivoConstantes.CAMPO_CALCON_FASE_PERIODO_MESES);
				} catch (Exception ex) {
					LOG.error("Error obteniendo el meses:", ex);
				}
				// Obtenemos dias
				Long dias = Long.valueOf(0);
				try {
					dias = periodoVigencia
							.get(ArchivoConstantes.CAMPO_CALCON_FASE_PERIODO_DIAS);
				} catch (Exception ex) {
					LOG.error("Error obteniendo el dias:", ex);
				}

				// Indicamos el numero de horas adicionales para incrementos
				// (por
				// defecto a 0)
				int deltaHoras = Integer.valueOf(Framework.getProperty(
						"delta.retencion.horas", "0"));

				// Indicamos el numero de minutos adicionales para incrementos
				// (por
				// defecto a 0)
				int deltaMinutos = Integer.valueOf(Framework.getProperty(
						"delta.retencion.minutos", "0"));

				result = Calendar.getInstance();
				result.setTime(fechaCierre.getTime());

				result.add(Calendar.DATE, dias.intValue());
				result.add(Calendar.MONTH, meses.intValue());
				result.add(Calendar.YEAR, anos.intValue());
				// Actualizamos valores de hora y minuto en el dia para el
				// lanzamiento del trigger despues de la retencion
				result.set(Calendar.HOUR_OF_DAY, deltaHoras * itemFase);
				result.set(Calendar.MINUTE, deltaMinutos * itemFase);

				LOG.info("Calendario fecha :" + result);

			} else {
				LOG.error("No se ha asginado periodo de retencion para la fase de vigencia");
			}
		} else {
			LOG.error("No se ha asginado datos para la fase de vigencia");
		}

		return result;
	}

	/**
	 * Devuelve los metadatos definidos para una fase concreta del calendario.
	 * 
	 * @param calendario
	 *            es el calendario
	 * @param fase
	 *            es la fase que se quiere buscar
	 * @return Datos de la fase buscada
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	public static Map<String, Object> buscarDatosFase(final DocumentModel calendario,
			final String fase) {
		Map<String, Object> datosFase = null;
		try {
			List<Map<String, Object>> fases = (List<Map<String, Object>>) calendario
					.getProperty(ArchivoConstantes.CALENDARIO_CONSERVACION,
							ArchivoConstantes.CAMPO_CALCON_FASES);
			for (Map<String, Object> faseD : fases) {
				String nombreFase = (String) faseD
						.get(ArchivoConstantes.CAMPO_CALCON_FASE_NOMBRE_FASE);
				if (nombreFase.equals(fase)) {
					datosFase = faseD;
					break;
				}
			}
		} catch (ClientException e) {
			LOG.error("Error calculando fecha fin de retencion: ", e);
		}
		return datosFase;
	}

	/**
	 * M&eacute;todo que devuelve un Mapa con los valores principales del
	 * calendario asociado al objeto.
	 * 
	 * @param calendario
	 *            es el calendario asociado
	 * @param objetoAsociado
	 *            Expediente asociado al calendario
	 * @return Listado de metadatos de metadatos
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	public static Map<String, Object> encuentraObjetoAsociado(
			final DocumentModel calendario, final DocumentModel objetoAsociado) {
		Map<String, Object> result = null;

		try {

			if (calendario != null) {

				Object ob = calendario.getProperty(CALENDARIO_CONSERVACION,
						OBJETOS_ASOCIADOS_CALENDARIO);
				LOG.debug("******Objetos obtenido del calendario: " + ob);
				if (ob != null) {
					List<Map<String, Object>> objetosAsociados = (List<Map<String, Object>>) ob;

					if (objetosAsociados != null) {
						for (Map<String, Object> elemento : objetosAsociados) {
							String docId = (String) elemento.get(DOC_ID);
							if (docId.equals(objetoAsociado.getId())) {
								result = elemento;
								break;
							}
						}
					}
				}
			}
		} catch (ClientException e) {
			LOG.error("Error leyendo objetos asociados al calendario: ", e);
		}
		return result;
	}

	/**
	 * Actualiza los valores del estado del calendario asociado a un expeciente
	 * (fecha de cierre, si est&aacute; retenido, fecha de retenci&oacute;n,
	 * etc...).
	 * 
	 * @param sesion
	 *            es la sesi&oacute;n
	 * @param calendario
	 *            es el calendario
	 * @param objetoAsociado
	 *            es el metadato a actualizar
	 * @param nuevosValores
	 *            son los nuevos valores a introducir
	 */
	@SuppressWarnings(UNCHECKED_WARNING)
	public static void actualizaObjetoAsociado(final CoreSession sesion,
			final DocumentModel calendario, final DocumentModel objetoAsociado,
			final Map<String, Object> nuevosValores) {
		try {
			List<Map<String, Object>> nuevosObjetos = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> objetosAsociados = (List<Map<String, Object>>) calendario
					.getProperty(CALENDARIO_CONSERVACION,
							OBJETOS_ASOCIADOS_CALENDARIO);
			for (Map<String, Object> elemento : objetosAsociados) {
				String docId = (String) elemento.get(DOC_ID);
				if (docId.equals(objetoAsociado.getId())) {
					nuevosObjetos.add(nuevosValores);
				} else {
					nuevosObjetos.add(elemento);
				}
			}
			calendario.setProperty(CALENDARIO_CONSERVACION,
					OBJETOS_ASOCIADOS_CALENDARIO, nuevosObjetos);
			sesion.saveDocument(calendario);
			sesion.save();
		} catch (ClientException e) {
			LOG.error("Error leyendo objetos asociados al calendario: ", e);
		}

	}

	/**
	 * Comprueba si es documento del CAdD.
	 * 
	 * @param documento
	 *            a comprobar
	 * @return true si el documento es del CAdD, false en caso contrario
	 */
	public static boolean esDocumentoDelCAdD(final DocumentModel documento) {
		String tipo = documento.getType();
		boolean res = false;
		if (ArchivoConstantes.TIPO_CARPETA_DE_DOCUMENTOS.equals(tipo)
				|| tipo.equals(ArchivoConstantes.TIPO_FUNCION_MARCO)
				|| tipo.equals(ArchivoConstantes.TIPO_FUNCION)
				|| tipo.equals(ArchivoConstantes.TIPO_ACTIVIDAD)) {
			res = true;
		}
		if (tipo.equals(ArchivoConstantes.TIPO_SERIE)
				|| tipo.equals(ArchivoConstantes.TIPO_EXPEDIENTE)
				|| tipo.equals(ArchivoConstantes.TIPO_EXPEDIENTE_REA)
				|| tipo.equals(ArchivoConstantes.TIPO_EXPEDIENTE_RELE)) {

			res = true;
		}
		return res;
	}

	/**
	 * M&eacute;todo que construye una instancia de ArchivoSchedule.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param documentoAsociado
	 *            documento asociado
	 * @param dayOfMonth
	 *            d&iacute;a para expresi&oacute;n cron
	 * @param month
	 *            mes para expresi&oacute;n cron
	 * @param year
	 *            a&ntilde;o para expresi&oacute;n cron
	 * @param horaEjecucion
	 *            hora para expresi&oacute;n cron
	 * @param minutoEjecucion
	 *            minuto para expresi&oacute;n cron
	 * @param id
	 *            id del Schedule
	 * @return Instancia de ArchivoSchedule
	 */
	public static ArchivoScheduleImpl construyeModeloScheduler(
			final CoreSession documentManager, final DocumentModel documentoAsociado,
			final int dayOfMonth, final int month, final int year, final String horaEjecucion,
			final String minutoEjecucion, final String id) {
		ArchivoScheduleImpl schedule = new ArchivoScheduleImpl();

		String cronExpression = "0 " + minutoEjecucion + " " + horaEjecucion
				+ " " + dayOfMonth + " " + month + " " + " ? " + year;
		LOG.info("Expresion cron a ejecutar:" + cronExpression);

		// FIXME SOLO PARA PROBAR QUE CAMBIA DE ESTADO.
		// CAMBIA DE ESTADO A LA MINUTO
		// cronExpression = "1 * * * * ?";

		// LOG.info("cronExpression de pruebas : " + cronExpression);

		String eventId = ArchivoConstantes.EVENTO_FIN_PERIODO_RETENCION;
		String eventCategory = "default";
		Principal username = documentManager.getPrincipal();

		schedule.setId(id);
		schedule.setEventId(eventId);
		schedule.setCronExpression(cronExpression);
		schedule.setEventCategory(eventCategory);
		schedule.setUserName(username.getName());
		schedule.setDocId(documentoAsociado.getId());
		return schedule;
	}

	/**
	 * Extrae de los metadatos del calendario el map con los campos relevantes
	 * del documento asociado y los rellena, devolviendo dicho map para
	 * asignarlo al Calendario.
	 * 
	 * @param documento
	 *            documento asociado al calendario
	 * @param calendarioAsociado
	 *            calendario de conservaci&oacute;n
	 * @param docId
	 *            id del documento
	 * @param fechaCierre
	 *            fecha de cierre
	 * @param fechaFinRetencion
	 *            fecha fin de retenci&oacute;n
	 * @return Map con los valores del objeto asociado al calendario
	 *         actualizados
	 */
	public static Map<String, Object> construyeObjetoAsociadoCaDC(
			final DocumentModel documento, final DocumentModel calendarioAsociado,
			final String docId, final Calendar fechaCierre, final Calendar fechaFinRetencion) {
		Map<String, Object> objetoAsociado = UtilidadesArchivo
				.encuentraObjetoAsociado(calendarioAsociado, documento);
		if (objetoAsociado == null) {
			objetoAsociado = new HashMap<String, Object>();
		}
		objetoAsociado.put(ArchivoConstantes.DOC_ID, docId);
		objetoAsociado.put("fecha_prevista_fin_retencion", fechaFinRetencion);
		objetoAsociado.put("fecha_cierre", fechaCierre);
		objetoAsociado.put("retenido_manual", false);
		objetoAsociado.put("fecha_ultima_retencion_manual", null);
		objetoAsociado.put("fase_calendario", "vigencia");
		return objetoAsociado;
	}

	/**
	 * M&eacute;todo que asigna una nueva fecha de retenci&oacute;n al documento
	 * asociado al calendario.
	 * 
	 * @param documentManager
	 *            instancia de la sesi&oacute;n
	 * @param documento
	 *            documento asociado al calendario
	 * @param calendarioAsociado
	 *            calendario
	 * @param docId
	 *            id del documento
	 * @param fechaCierre
	 *            Fecha de Cierre
	 * @param fechaFinRetencion
	 *            Fecha de fin de Retencion
	 * @return resultado de la operaci&oacute;n; true en caso satisfactorio,
	 *         false en caso contrario.
	 */
	public static boolean asignaNuevaFechaFinRetencion(
			final CoreSession documentManager, final DocumentModel documento,
			final DocumentModel calendarioAsociado, final String docId,
			final Calendar fechaCierre, final Calendar fechaFinRetencion) {
		boolean result = false;
		ListDiff listDiff = new ListDiff();
		Map<String, Object> objetoAsociado = construyeObjetoAsociadoCaDC(
				documento, calendarioAsociado, docId, fechaCierre,
				fechaFinRetencion);
		listDiff.add(objetoAsociado);
		try {
			calendarioAsociado.setProperty(
					ArchivoConstantes.CALENDARIO_CONSERVACION,
					ArchivoConstantes.CAMPO_CALCON_OBJETOS_ASOCIADOS, listDiff);
			documentManager.saveDocument(calendarioAsociado);
			documentManager.save();
			result = true;
		} catch (ClientException e) {
			LOG.error("Error actualizando calendario", e);
		}
		return result;
	}
}
