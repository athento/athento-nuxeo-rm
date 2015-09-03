package es.rm.platform.api.utils;

import java.util.Map;

/**
 * Representaci&oacute;n de un esquema de metadatos perteneciente a la categoria EMDRM.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 *
 */
public class RepresentacionEMRMEsquema {
	
	/**
	 * Nombre del esquema.
	 */
	private String nombreEsquema;
	
	/**
	 * Listado de campos y sus valores.
	 */
	private Map<String, Object> campos;
	
	/**
	 * Constructor.
	 * 
	 * @param nombre es el nombre del esquema representado
	 * @param camposParametro listado de campos y valores
	 */
	public RepresentacionEMRMEsquema(String nombre, Map<String,Object> camposParametro){
		this.nombreEsquema = nombre;
		this.campos = camposParametro;
	}
	
	/**
	 * Getter para el atributo nombreEsquema.
	 * 
	 * @return nombreEsquema
	 */
	public final String getNombreEsquema() {
		return nombreEsquema;
	}
	
	/**
	 * Setter para el atributo nombreEsquema.
	 * 
	 * @param nombreEsquema es el valor a asignar.
	 */
	public final void setNombreEsquema(String nombreEsquema) {
		this.nombreEsquema = nombreEsquema;
	}
	
	/**
	 * Getter para el atributo campos.
	 * 
	 * @return el listado de campos del esquema
	 */
	public final Map<String, Object> getCampos() {
		return campos;
	}
	
	/**
	 * Setter para el atributo campos.
	 * 
	 * @param campos valor a asignar al atributo
	 */
	public final void setCampos(Map<String, Object> campos) {
		this.campos = campos;
	}
	
	/**
	 * Extrae un campo del esquema.
	 * 
	 * @param nombreCampo nombre del campo que se desea extraer
	 * @return el objeto valor del campo
	 */
	public final Object extraerCampo(String nombreCampo){
		return campos.get(nombreCampo);
	}

}
