package com.bolsadeideas.springboot.app.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final static String UPLOADS_FOLDER = "uploads";

	@Override
	public Resource load(String filename) throws MalformedURLException {
		
		Path pathFoto = this.getPath(filename);
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		
		recurso = new  UrlResource(pathFoto.toUri());
		
		if(!recurso.exists() || !recurso.isReadable()) {
			throw new RuntimeException("Error: no se puede cargar la imagen" + pathFoto.toString());
		}
		
		return recurso;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		
		String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path rootPath=this.getPath(uniqueFilename);

		log.info("rootPath: " + rootPath);
		
			/** Esta es una alternativa A para crear la imagen**/
			/* byte[] bytes = foto.getBytes(); Path rutaCompleta=Paths.get(rootPath + "//" +
			 * foto.getOriginalFilename()); Files.write(rutaCompleta, bytes);
			 * flash.addFlashAttribute("info","Ha subido correctamente" +
			 * foto.getOriginalFilename());
			 */
			
			/** Esta es una alternativa B para crear la imagen**/
			Files.copy(file.getInputStream(), rootPath);
			
		return uniqueFilename;
	}

	@Override
	public Boolean delete(String filename) {
		
		Path rootPath = this.getPath(filename);
		
		File archivo = rootPath.toFile();
		
		if(archivo.exists() && archivo.canRead()) {
			if(archivo.delete()) {
				return true;
			}
		}
		return false;
	}
	
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(Paths.get(UPLOADS_FOLDER).toFile());
		
	}

	@Override
	public void init() throws IOException {
		Files.createDirectory(Paths.get(UPLOADS_FOLDER));	
	}

}
