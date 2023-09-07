package com.bolsadeideas.springboot.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long> {
	
	@Query("select p from Producto p where lower(p.nombre) like lower(concat('%', ?1,'%'))")
	public List<Producto> findByNombre(String term);
	
	/*
	 * Otra opción de hacer lo de arriba es:
	 * 
	 * public List<Producto> findByNombreLikeIgnoreCase(String term);
	 */
}
