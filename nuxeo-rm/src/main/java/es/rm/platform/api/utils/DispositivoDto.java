package es.rm.platform.api.utils;
/**
 * Clase Dispositivo.
 * 
 *
 */
public class DispositivoDto {
	/** Identificador.*/
	private String id;
	/** Nombre.*/
	private String nombre;
	/**
	 * Devuelve el identificador.
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * Asigna el identificador.
	 * @param id the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}
	/**
	 * Devuelve el nombre.
	 * @return the nombre
	 */
	public String getNombre() {
		return this.nombre;
	}
	/**
	 * Asigna el nombre.
	 * @param nombre the nombre to set
	 */
	public void setNombre(final String nombre) {
		this.nombre = nombre;
	}
	
	
}
