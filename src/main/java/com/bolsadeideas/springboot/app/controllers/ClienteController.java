package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.service.IClienteService;

import jakarta.validation.Valid;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	@Autowired
	private IClienteService clienteService; 
	
	@RequestMapping(value="/listar",method=RequestMethod.GET)
	public String listar(@RequestParam(name="page",defaultValue="0") int page,  Model model) {
		Pageable pageRequest = QPageRequest.of(page, 4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes",clientes);
		return "listar";
	}
	
	@RequestMapping(value="/form")
	public String crear(Map<String,Object> model) {
		model.put("titulo","formulario de cliente");
		
		Cliente cliente = new Cliente();
		
		model.put("cliente",cliente);
		
		return "form";
	}
	
	@RequestMapping(value="/form",method=RequestMethod.POST)
	public String guardar(@Valid Cliente cliente,BindingResult result,Model model,RedirectAttributes flash, SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			return "form";
		}
		
		String mensajeFlash = (cliente.getId()!=null)?"Cliente editado con exito":"Cliente Creado con éxito";
		
		clienteService.save(cliente);
		status.setComplete();
		
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";
		
	}
	
	@GetMapping("/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String,Object> model,RedirectAttributes flash) {
		Cliente cliente = new Cliente();
		
		if(id>0) {
			cliente = clienteService.findOne(id);
			
			if(cliente==null) {
				flash.addFlashAttribute("error", "El Cliente no existe en base de datos");
				return "redirect:/listar";				
			}
			
		}else {
			flash.addFlashAttribute("error", "El ID del Cliente no puede ser cero");
			return "redirect:/listar";
		}
		model.put("titulo","editar cliente");
		model.put("cliente",cliente);
		
		return "form";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id,RedirectAttributes flash) {
		
		if(id>0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con éxito");
		}	
		
		return "redirect:/listar";
	}
	
}
