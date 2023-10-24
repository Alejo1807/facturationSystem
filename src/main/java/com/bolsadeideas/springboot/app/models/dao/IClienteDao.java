package com.bolsadeideas.springboot.app.models.dao;


import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

public interface IClienteDao extends JpaRepository<Cliente, Long>{
	
	@Query("select c from Cliente c left join fetch c.facturas f where c.id =?1")
	public Cliente fecthByidWithFacturas(Long id);
	
}


/*
 * public interface IClienteDao extends CrudRepository<Cliente,Long> {
 * 
 * }
 */
