package com.bolsadeideas.springboot.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {

	public Cliente() {
		facturas = new ArrayList<Factura>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @Column(name="NOMBRE_CLIENTE") => Se usa si el campo en la tabla es difernte
	@NotEmpty
	@Size(min=4,max=12)
	private String nombre;

	@NotEmpty
	private String apellido;
	
	@NotEmpty
	@Email
	private String email;

	@Column(name = "CREATE_AT")
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@NotNull
	private Date createAt;
	
	@OneToMany(mappedBy="cliente",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private List<Factura> facturas;
	
	private String foto;

	private static final long serialVersionUID = 1L;

	/* Este método sirve para que se creen valores al hacer submit antes de crear la entidad (en este caso, después de enviar
	 * el form con los datos del nuevo cliente, crea la fecha y se la añade como atributo a la entidad enviada)
	 * @PrePersist 
	 * public void prePersist() {
	 *  createAt = new Date();
	 *   }
	 */
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	public void addFactura(Factura factura) {
		facturas.add(factura);
	}

	@Override
	public String toString() {
		return nombre + " " + apellido;
	}
	
	

}
