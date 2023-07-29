package com.bolsadeideas.springboot.app.models.dao;


import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;

public interface IClienteDao extends JpaRepository<Cliente, Long>{
}


/*
 * public interface IClienteDao extends CrudRepository<Cliente,Long> {
 * 
 * }
 */
